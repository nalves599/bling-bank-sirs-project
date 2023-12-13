<template>
  <div class="accounts">
    <h1>Accounts of user {{ username }}</h1>
    <v-data-table :items="accounts" :headers="headers">
      <template #item.holders="{ item }">
        {{ item.holders.sort().join(', ') }}
      </template>
    </v-data-table>

    <div class="create-account-container">
      <router-link to="/create-account" class="create-account-button">Create Account</router-link>
    </div>

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
import router from '@/router'

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
  const response = await getAccountsFromHolder(username.value)
  accounts.value = response.map((item) => ({
    ...item,
    holders: item.holders.sort() // Sort holders alphabetically
  }))
}

async function createAccount() {
  router.push('/create-account')
}

fetchAccountsFromHolder()
</script>
<style scoped>
.create-account-container {
  margin-top: 20px; /* Add some margin to separate the button from the table */
}

.create-account-button {
  background-color: #4caf50;
  color: white;
  padding: 10px 20px;
  text-decoration: none;
  font-size: 15px;
  border-radius: 5px;
  margin-right: 30px; /* Margin between buttons */
}

.create-account-button:hover {
  background-color: #45a049;
}
</style>