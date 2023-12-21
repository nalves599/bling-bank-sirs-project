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
        v-for="account in accounts"
        :key="account.id"
        :value="account.id"
        style="background-color: #f8f8f8"
      >
        {{ account.id }} - {{ account.currency }}
      </option>
    </select>

    <!-- Display payments of selected account -->
    <div class="create-payment-container">
      <router-link :to="'/create-payment/' + email" class="create-payment-button"
        >Create Payment</router-link
      >
    </div>

    <div class="payment-table-container">
      <div v-if="selectedAccount" class="account-details">
        <h2>Payments of Account {{ selectedAccount.name }}</h2>
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
              <td>
                <template v-if="!item.approved">
                  <button
                    :class="{ 'green-button': !item.approved, 'red-button': item.approved }"
                    @click="signPayment(Number(item.id))"
                  >
                    Sign Payment
                  </button>
                </template>
                <template v-else>
                  <span>Signed</span>
                </template>
              </td>
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
import { ref, onMounted, watch } from 'vue'
import { getAccountsFromHolder, getAccountPayments } from '@/services/api'
import type { AccountDto } from '@/models/AccountDto'
import type { PaymentDto } from '@/models/PaymentDto'
import BottomBar from '@/components/BottomBar.vue'
import LogoutButton from '@/components/LogoutButton.vue'
import router from '@/router'

const accounts = ref<AccountDto[]>([])
const payments = ref<PaymentDto[]>([])
const selectedAccountId = ref<string | null>(null)
const authStore = useAuthStore()
const { email } = storeToRefs(authStore)

async function fetchAccountsFromHolder() {
  accounts.value = await getAccountsFromHolder()
}

onMounted(() => {
  fetchAccountsFromHolder()
})

// Watcher to ensure selected option stays in the dropdown
watch(
  () => selectedAccountId.value,
  (newVal) => {
    if (newVal) {
      selectedAccount.value = accounts.value.find((account) => account.id === newVal) || null
    }
  }
)

const selectedAccount = ref<AccountDto | null>(null)
// get account payments
async function fetchAccountpayments() {
  payments.value = await getAccountPayments(selectedAccount.value?.id ?? '')
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
  { title: 'payment ID', key: 'id', sortable: true },
  { title: 'Date', key: 'Date', sortable: true },
  { title: 'Description', key: 'description', sortable: false },
  { title: 'Amount', key: 'amount', sortable: true },
  { title: 'Currency', key: 'currencyType', sortable: true },
  { title: 'Approvals', key: 'approvedApprovals', sortable: true },
  { title: 'Required', key: 'requiredApprovals', sortable: true },
  { title: 'Approved', key: 'approved', sortable: true }
]

const signPayment = async (paymentId: number) => {
  router.push(`/sign-payment/${paymentId}`)
}
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

.green-button {
  background-color: green;
  color: white;
}

.red-button {
  background-color: red;
  color: white;
}
</style>
