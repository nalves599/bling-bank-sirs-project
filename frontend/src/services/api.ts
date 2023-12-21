import axios from 'axios'

import { HolderDto } from '@/models/HolderDto'
import { AccountDto } from '@/models/AccountDto'
import type { LoginRequestDto } from '@/models/LoginRequestDto'
import { useAuthStore } from '@/stores/auth'
import { useKeyStore } from '@/stores/keys'
import { MovementDto } from '@/models/MovementDto'
import { PaymentDto } from '@/models/PaymentDto'
import type { RegisterRequestDto } from '@/models/RegisterRequestDto'
import { ChallengeResolvedDto } from '@/models/ChallengeResolvedDto'
import { crypto as lib } from 'blingbank-lib'
import { ChallengeDto } from '@/models/ChallengeDto'

const http = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  headers: {
    'Content-Type': 'application/json'
  }
})

http.interceptors.request.use(
  (config) => {
    if (config.headers && !config.headers.Authorization) {
      const { token } = useAuthStore()
      if (token) config.headers.Authorization = `Bearer ${token}`
    }

    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

export async function register(loginRequest: RegisterRequestDto) {
  try {
    await http.post('/register', loginRequest)
  } catch (error) {
    console.error(error)
    throw new Error('Register failed') // Throw an error on register failure
  }
}

export async function login(loginRequest: LoginRequestDto, sharedSecret: string) {
  try {
    const { setToken, setEmail } = useAuthStore()
    const { setSessionKey, setPublicKey, setPrivateKey, setSecretShared } = useKeyStore()
    setToken('')
    setSecretShared(sharedSecret)
    setEmail(loginRequest.email)
    const response = await http.post('/login', loginRequest)

    const cipheredChallenge = new ChallengeDto(response.data).challenge

    const secretHash = new Uint8Array(await lib.sha256(sharedSecret))

    const challengeHash = await lib.paramUnprotect(cipheredChallenge, secretHash)

    const challenge = lib.decoder.decode(challengeHash)

    const solution = lib.solvePOWChallenge(challenge)

    const protectedSolution = await lib.paramProtect(solution, secretHash)

    const challengeResponse = new ChallengeResolvedDto({
      email: loginRequest.email,
      solution: protectedSolution
    })

    const responseToken = await http.post('/login/token', challengeResponse)
    setToken(responseToken.data.token)

    const encryptedSessionKey = responseToken.data.encryptedSessionKey

    const sessionKey = await lib.paramUnprotect(encryptedSessionKey, secretHash)

    setSessionKey(sessionKey)

    const { publicKey, privateKey } = await lib.createECDSAKey()

    setPrivateKey(privateKey)

    const publicKeyBuffer = await crypto.subtle.exportKey('raw', publicKey)

    const publicKeyHex = lib.bufferToHex(publicKeyBuffer)

    setPublicKey(publicKeyHex)

    const protectedPublicKey = await lib.paramProtect(publicKeyHex, sessionKey)

    const payload = {
      signKey: protectedPublicKey
    }

    await http.post('/me/keys', payload)
  } catch (error) {
    console.error(error)
    throw new Error('Login failed') // Throw an error on login failure
  }
}

export async function getHolders(): Promise<HolderDto[]> {
  const response = await http.get('/users')

  const holders = response.data.users

  return holders.map((holder: any) => new HolderDto(holder))
}

export async function getAccountsFromHolder(): Promise<AccountDto[]> {
  const response = await http.get(`/me`)

  const accounts = response.data.accounts

  return accounts.map((account: any) => new AccountDto(account))
}

export async function unlockAccount(accountId: string, shamirSecret: string) {
  const request = { secret: shamirSecret }

  await http.post(`/accounts/${accountId}/unlock`, request)
}

export async function createAccount(account: AccountDto): Promise<AccountDto> {
  const response = await http.post('/accounts', account)

  return new AccountDto(response.data)
}

export async function getAccountMovements(accountId: string) {
  const response = await http.get(`/accounts/${accountId}`)

  const movements = response.data.movements

  return movements.map((movement: any) => new MovementDto(movement))
}

export async function getAccountPayments(accountId: string) {
  const response = await http.get(`/payments/${accountId}`)

  const payments = response.data

  return payments.map((payment: any) => new PaymentDto(payment))
}

export async function createPayment(payment: PaymentDto, id: string) {
  const protectedTotp = await lib.paramProtect(payment.totp, useKeyStore().sessionKey)
  if (typeof protectedTotp === 'string') payment.totp = protectedTotp
  else payment.totp = lib.decoder.decode(protectedTotp)

  const protectedValue = await lib.paramProtect(payment.value, useKeyStore().sessionKey)
  if (typeof protectedValue === 'string') payment.value = protectedValue
  else payment.value = lib.decoder.decode(protectedValue)

  const protectedDescription = await lib.paramProtect(payment.description, useKeyStore().sessionKey)
  if (typeof protectedDescription === 'string') payment.description = protectedDescription
  else payment.description = lib.decoder.decode(protectedDescription)

  const response = await http.post(`/accounts/${id}/payments`, payment)

  return new PaymentDto(response.data)
}

export async function approvePayment(paymentId: number) {
  const response = await http.post(`/payments/${paymentId}/approve`)

  return new PaymentDto(response.data)
}
