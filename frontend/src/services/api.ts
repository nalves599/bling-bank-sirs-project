import axios from 'axios'

import { HolderDto } from '@/models/HolderDto'
import { AccountDto } from '@/models/AccountDto'
import type { LoginRequestDto } from '@/models/LoginRequestDto'
import { useAuthStore } from '@/stores/auth'
import { LoginResponseDto } from '@/models/LoginResponseDto'
import { MovementDto } from '@/models/MovementDto'
import { PaymentDto } from '@/models/PaymentDto'
import type { RegisterRequestDto } from '@/models/RegisterRequestDto'

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

export async function login(loginRequest: LoginRequestDto) {
  try {
    const { setToken } = useAuthStore()
    setToken('')
    const response = await http.post('/auth/authenticate', loginRequest)
    const responseData = new LoginResponseDto(response.data)
    setToken(responseData.token)
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
