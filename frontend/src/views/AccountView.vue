<template>
  <div class="accounts">
    <h1>This is a accounts page</h1>
    <v-data-table :items="accounts" :headers="headers"> </v-data-table>
    <BottomBar />
    <LogoutButton />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { getAccountsFromHolder } from '@/services/api'
import type { AccountDto } from '@/models/AccountDto'
import { useRoute } from 'vue-router'
import BottomBar from '@/components/BottomBar.vue'
import LogoutButton from '@/components/LogoutButton.vue';

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

const id = useRoute().params.id[0]

async function fetchAccountsFromHolder() {
  accounts.value = await getAccountsFromHolder(id)
}

fetchAccountsFromHolder()
</script>
