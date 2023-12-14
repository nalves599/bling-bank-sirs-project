<template>
  <div class="account-selector">
    <h1 style="color: white">Payments</h1>

    <label for="accountDropdown" style="color: white; font-size: 18px; margin-bottom: 10px"
      >Select Account:</label
    >
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

    <!-- Display payments of selected account -->
    <div class="create-payment-container">
      <router-link :to="'/create-payment/' + username" class="create-payment-button"
        >Create Payment</router-link
      >
    </div>

    <div class="payment-table-container">
      <div v-if="selectedAccount" class="account-details">
        <h2>Payments of Account {{ selectedAccount.accountId }}</h2>
        <v-data-table :headers="headers" :items="payments">
          <template v-slot:item="{ item }">
            <tr>
              <td>{{ item.id }}</td>
              <td>{{ item.date }}</td>
              <td>{{ item.description }}</td>
              <td>{{ item.amount }}</td>
              <td>{{ item.currencyType }}</td>
              <td>{{ item.approvedApprovals }}</td>
              <td>{{ item.requiredApprovals }}</td>
              <td>{{ item.approved }}</td>
            </tr>
          </template>
        </v-data-table>
      </div>
    </div>

    <BottomBar />
    <LogoutButton />
  </div>
</template>

<script setup lang="ts">
import { useAuthStore } from '@/stores/auth'
import { storeToRefs } from 'pinia'
import { ref, onMounted, computed, watch } from 'vue'
import { getAccountsFromHolder, getAccountPayments } from '@/services/api'
import type { AccountDto } from '@/models/AccountDto'
import type { PaymentDto } from '@/models/PaymentDto'
import BottomBar from '@/components/BottomBar.vue'
import LogoutButton from '@/components/LogoutButton.vue'

const accounts = ref<AccountDto[]>([])
const payments = ref<PaymentDto[]>([])
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
// get account payments
async function fetchAccountpayments() {
  payments.value = await getAccountPayments(selectedAccount.value?.accountId)
}

watch(
  () => selectedAccount.value,
  () => {
    if (selectedAccount.value) {
      fetchAccountpayments()
    }
  }
)

const headers = [
  { title: 'payment ID', key: 'paymentId', sortable: true },
  { title: 'Date', key: 'paymentDate', sortable: true },
  { title: 'Description', key: 'paymentDescription', sortable: false },
  { title: 'Amount', key: 'paymentAmount', sortable: true },
  { title: 'Currency', key: 'paymentCurrencyType', sortable: true },
  { title: 'Approvals', key: 'paymentApprovedApprovals', sortable: true },
  { title: 'Required', key: 'paymentRequiredApprovals', sortable: true },
  { title: 'Approved', key: 'paymentApproved', sortable: true }
]
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

.create-payment-button {
  background-color: #4caf50;
  color: white;
  padding: 10px 20px;
  text-decoration: none;
  font-size: 15px;
  border-radius: 5px;
  margin-right: 10px; /* Adjusted margin value */
}

.create-payment-button:hover {
  background-color: #45a049;
}

/* Add the following styles to create space between the button and the table */
.create-payment-container {
  margin-bottom: 20px;
}

.payment-table-container {
  margin-top: 20px;
}
</style>
