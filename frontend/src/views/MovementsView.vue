<template>
  <div class="account-selector">
    <label for="accountDropdown">Select Account:</label>
    <select v-model="selectedAccount" id="accountDropdown" @change="fetchAccountsFromHolder">
      <option value="" disabled>Select an account</option>
      <option v-for="account in accounts" :key="account.accountId" :value="account.balance">
        {{ account.accountId }}
      </option>
    </select>

    <!-- Display movements or other components based on selected account -->
    <div v-if="selectedAccount">
      <h2>Movements for {{ selectedAccount }}</h2>
      <!-- Add code to display movements here -->
    </div>

    <BottomBar />
    <LogoutButton />
  </div>
</template>

<script setup lang="ts">
import { useAuthStore } from '@/stores/auth'
import { storeToRefs } from 'pinia'
import { ref, onMounted } from 'vue'
import { getAccountsFromHolder } from '@/services/api'
import type { AccountDto } from '@/models/AccountDto'
import BottomBar from '@/components/BottomBar.vue'
import LogoutButton from '@/components/LogoutButton.vue'

const accounts = ref<AccountDto[]>([]) // Add AccountDto type definition
const selectedAccount = ref<string>('')
//const movements = ref<MovementDto[]>([])

const authStore = useAuthStore()
const { username } = storeToRefs(authStore)

async function fetchAccountsFromHolder() {
  accounts.value = await getAccountsFromHolder(username.value)
}

fetchAccountsFromHolder()

/*async function fetchMovements() {
  // Fetch movements for the selected account
  if (selectedAccount.value) {
    movements.value = await getAccountMovements(selectedAccount.value)
  }
}*/
</script>

<style>
@media (min-width: 1024px) {
  .about {
    min-height: 100vh;
    display: flex;
    align-items: center;
  }
}
</style>
