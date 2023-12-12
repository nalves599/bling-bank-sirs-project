<template>
  <div class="accounts">
    <h1>This is a accounts page</h1>
    <v-data-table :items="accounts" :headers="headers"> </v-data-table>
    <LogoutButton />
    <BottomBar />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { getAccountsFromHolder } from '@/services/api'
import type { AccountDto } from '@/models/AccountDto'
import { useAuthStore } from '@/stores/auth'
import { storeToRefs } from 'pinia'
import BottomBar from '@/components/BottomBar.vue'
import LogoutButton from '@/components/LogoutButton.vue'

const accounts = ref<AccountDto[]>([])

const headers = [
  { title: 'Account Number', key: 'accountId', sortable: true },
  {
    title: 'Holders',
    key: 'holders',
    sortable: false
  },
  {
    title: 'Balance',
    key: 'balance',
    sortable: true
  },
  {
    title: 'Currency',
    key: 'currency',
    sortable: true
  },
  {
    title: '# of Movements',
    key: 'numberOfMovements',
    sortable: true
  }
]

const authStore = useAuthStore()
const { username } = storeToRefs(authStore)

async function fetchAccountsFromHolder() {
  accounts.value = await getAccountsFromHolder(username.value)
}

fetchAccountsFromHolder()
</script>
