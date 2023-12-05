import axios from 'axios'

import { HolderDto } from '@/models/HolderDto'

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
