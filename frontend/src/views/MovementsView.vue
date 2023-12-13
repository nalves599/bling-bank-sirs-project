<template>
  <div class="account-selector">
    <h1 style="color: white;">Movements</h1>

    <label for="accountDropdown" style="color: white; font-size: 18px; margin-bottom: 10px;">Select Account:</label>
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
        margin-bottom: 20px;
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
        {{ account.holders.sort().join(', ') }}
      </option>
    </select>

    <!-- Display movements of selected account -->
    <div v-if="selectedAccount" class="account-details">
      <h2>Movements of Account {{ selectedAccount.accountId }}</h2>
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

    <BottomBar />
    <LogoutButton />
  </div>
</template>

<script setup lang="ts">
import { useAuthStore } from '@/stores/auth'
import { storeToRefs } from 'pinia'
import { ref, onMounted, computed, watch } from 'vue'
import { getAccountsFromHolder, getAccountMovements } from '@/services/api'
import type { AccountDto } from '@/models/AccountDto'
import type { MovementDto } from '@/models/MovementDto'
import BottomBar from '@/components/BottomBar.vue'
import LogoutButton from '@/components/LogoutButton.vue'

const accounts = ref<AccountDto[]>([])
const movements = ref<MovementDto[]>([])
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
// get account movements
async function fetchAccountMovements() {
  movements.value = await getAccountMovements(selectedAccount.value?.accountId)
}

watch(
  () => selectedAccount.value,
  () => {
    if (selectedAccount.value) {
      fetchAccountMovements();
    }
  }
);

const headers = [
  { title: 'Movement ID', key: 'movementId', sortable: true },
  { title: 'Date', key: 'movementDate', sortable: true },
  { title: 'Description', key: 'movementDescription', sortable: false },
  { title: 'Value', key: 'movementValue', sortable: true },
];
</script>

<style>
@media (min-width: 1024px) {
  .account-selector {
    min-height: 100vh;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
  }

  .account-details {
    text-align: center;
  }
}
</style>
