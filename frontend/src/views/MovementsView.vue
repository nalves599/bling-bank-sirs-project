<template>
  <div class="account-selector">
    <label for="accountDropdown" style="color: white; font-size: 18px">Select Account:</label>
    <select
      v-model="selectedAccountId"
      id="accountDropdown"
      @change="fetchAccountsFromHolder"
      style="
        width: 500px;
        background-color: #fff;
        color: #333;
        border: 1px solid #ccc;
        padding: 10px;
        border-radius: 5px;
      "
    >
      <option value="" disabled>Select an account</option>
      <option
        v-for="account in sortedAccounts"
        :key="account.accountId"
        :value="account.accountId"
        style="background-color: #f8f8f8"
      >
        {{ account.accountId }} - Balance: {{ account.balance }} - Holders:
        {{ account.holders.join(', ') }}
      </option>
    </select>

    <!-- Display movements or other components based on selected account -->
    <div v-if="selectedAccount" class="account-details">
      <h2>Details for Account {{ selectedAccount.accountId }}</h2>
      <p>Balance: {{ selectedAccount.balance }}</p>
      <p>Holders: {{ selectedAccount.holders.join(', ') }}</p>
      <!-- Add code to display movements or other details here -->
    </div>

    <BottomBar />
    <LogoutButton />
  </div>
</template>

<script setup lang="ts">
import { useAuthStore } from '@/stores/auth'
import { storeToRefs } from 'pinia'
import { ref, onMounted, computed, watch } from 'vue'
import { getAccountsFromHolder } from '@/services/api'
import type { AccountDto } from '@/models/AccountDto'
import BottomBar from '@/components/BottomBar.vue'
import LogoutButton from '@/components/LogoutButton.vue'

const accounts = ref<AccountDto[]>([]) // Add AccountDto type definition
const selectedAccountId = ref<string | null>(null)
const authStore = useAuthStore()
const { username } = storeToRefs(authStore)

async function fetchAccountsFromHolder() {
  accounts.value = await getAccountsFromHolder(username.value)
}

onMounted(() => {
  fetchAccountsFromHolder()
})

const sortedAccounts = computed(() => {
  return [...accounts.value].sort((a, b) => {
    const idA = parseInt(a.accountId)
    const idB = parseInt(b.accountId)
    return idA - idB
  })
})

// Watcher to ensure selected option stays in the dropdown
watch(
  () => selectedAccountId.value,
  (newVal) => {
    if (newVal) {
      selectedAccount.value = accounts.value.find((account) => account.accountId === newVal) || null
    }
  }
)

const selectedAccount = ref<AccountDto | null>(null)
</script>

<style>
@media (min-width: 1024px) {
  .account-selector {
    min-height: 100vh;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center; /* Center content vertically */
  }

  .account-details {
    text-align: center;
  }
}
</style>
