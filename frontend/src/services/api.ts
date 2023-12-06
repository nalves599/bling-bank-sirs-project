import axios from 'axios'

import { HolderDto } from '@/models/HolderDto'
import { AccountDto } from '@/models/AccountDto'

const http = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  headers: {
    'Content-Type': 'application/json'
  }
})

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
