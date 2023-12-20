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
    const { setToken } = useAuthStore()
    const { setSessionKey, setPublicKey, setPrivateKey, setSecretShared } = useKeyStore()
    setToken('')
    setSecretShared(sharedSecret)
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
  const response = await http.get('/holders')

  const holders = response.data.accountHolders

  return holders.map((holder: any) => new HolderDto(holder))
}

export async function getAccountsFromHolder(holderId: string): Promise<AccountDto[]> {
  const response = await http.get(`/accounts/holder/${holderId}`)

  const accounts = response.data.accounts

  return accounts.map((account: any) => new AccountDto(account))
}

export async function createAccount(account: AccountDto, holderName: string): Promise<AccountDto> {
  const response = await http.post('/accounts/create/' + holderName, account)

  return new AccountDto(response.data)
}

export async function getAccountMovements(accountId: string) {
  const response = await http.get(`/movements/account/${accountId}`)

  const movements = response.data.movements

  return movements.map((movement: any) => new MovementDto(movement))
}

export async function getAccountPayments(accountId: string) {
  const response = await http.get(`/payments/${accountId}`)

  const payments = response.data

  return payments.map((payment: any) => new PaymentDto(payment))
}

export async function createPayment(payment: PaymentDto) {
  const response = await http.post('/payments/create', payment)

  return new PaymentDto(response.data)
}

export async function approvePayment(paymentId: number) {
  const response = await http.post(`/payments/${paymentId}/approve`)

  return new PaymentDto(response.data)
}
