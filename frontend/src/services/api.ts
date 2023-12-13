import axios from 'axios'

import { HolderDto } from '@/models/HolderDto'
import { AccountDto } from '@/models/AccountDto'
import type { LoginRequestDto } from '@/models/LoginRequestDto'
import { useAuthStore } from '@/stores/auth'
import { LoginResponseDto } from '@/models/LoginResponseDto'

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

export async function register(loginRequest: LoginRequestDto) {
  try {
    const response = await http.post('/auth/register', loginRequest)
    const responseData = new LoginResponseDto(response.data)
    const { setToken } = useAuthStore()
    setToken(responseData.token)
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
