<template>
  <div class="accounts">
    <h1>Accounts of user {{ email }}</h1>
    <v-data-table :items="accounts" :headers="headers">
      <template v-slot:item="row">
        <tr>
          <td>{{ row.item.id }}</td>
          <td>{{ row.item.name }}</td>
          <td>{{ row.item.currency }}</td>
          <td>
            <v-btn class="mx-2" fab dark small color="pink" @click="onButtonClick(row.item)">
              <v-icon dark>mdi-heart</v-icon>
            </v-btn>
          </td>
        </tr>
      </template>
    </v-data-table>

    <div class="create-account-container">
      <router-link to="/create-account" class="create-account-button">Create Account</router-link>
    </div>

    <!-- Display movements of selected account -->
    <div v-if="selectedAccount" class="account-details">
      <h2>Movements of Account {{ selectedAccount.name }}</h2>
      <v-data-table :headers="headers" :items="movements">
        <template v-slot:item="{ item }">
          <tr>
            <td>{{ item.movementId }}</td>
            <td>{{ item.movementDate }}</td>
            <td>{{ item.movementDescription }}</td>
            <td>{{ item.movementValue }}</td>
          </tr>
        </template>
      </v-data-table>
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
import { getAccountMovements } from '@/services/api'
import type { MovementDto } from '@/models/MovementDto'

const accounts = ref<AccountDto[]>([])
const movements = ref<MovementDto[]>([])

const headers = [
  { title: 'Account Number', key: 'id', sortable: true },
  {
    title: 'Name',
    key: 'name',
    sortable: true
  },
  {
    title: 'Currency',
    key: 'currency',
    sortable: true
  },
  {
    title: 'Actions',
    key: 'actions'
  }
]

const authStore = useAuthStore()
const { email } = storeToRefs(authStore)
const selectedAccount = ref<AccountDto | null>(null)

async function fetchAccountsFromHolder() {
  const response = await getAccountsFromHolder(email.value)
  accounts.value = response.map((item) => ({
    ...item,
    name: item.name // Sort holders alphabetically
  }))
}

async function onButtonClick(account: AccountDto) {
  movements.value = await getAccountMovements(account.id || '')
  selectedAccount.value = account
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
