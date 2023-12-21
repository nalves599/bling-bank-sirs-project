<template>
  <div class="create-payment">
    <h2>Create Payment</h2>
    <form @submit.prevent="paymentCreate">
      <div class="form-group">
        <label for="amount">Amount:</label>
        <input type="number" v-model="amount" id="amount" required />
      </div>
      <div class="form-group">
        <label for="date">Date to be processed:</label>
        <input type="date" v-model="date" id="date" @input="formatDate" />
      </div>
      <div class="form-group">
        <label for="description">Description:</label>
        <textarea v-model="description" id="description" required></textarea>
      </div>
      <div class="form-group">
        <label for="accountDropdown">Source Account:</label>
        <select v-model="selectedAccountId" id="accountDropdown" @change="fetchAccountsFromHolder">
          <option value="" disabled>Select an account</option>
          <option v-for="account in accounts" :key="account.id" :value="account.id">
            {{ account.id }} - {{ account.name }}
          </option>
        </select>
      </div>
      <div class="form-group">
        <button type="submit">Create Payment</button>
      </div>
    </form>
    <BottomBar />
    <LogoutButton />
  </div>
</template>

<script setup lang="ts">
import { useAuthStore } from '@/stores/auth'
import { storeToRefs } from 'pinia'
import { ref, onMounted, computed, watch } from 'vue'
import { getAccountsFromHolder, createPayment } from '@/services/api'
import type { AccountDto } from '@/models/AccountDto'
import BottomBar from '@/components/BottomBar.vue'
import LogoutButton from '@/components/LogoutButton.vue'
import { PaymentDto } from '@/models/PaymentDto'
import router from '@/router'

const destination = ref('')
const amount = ref(0)
const date = ref('')
const description = ref('')
const selectedAccountId = ref<number | null>(null)

const authStore = useAuthStore()
const { email } = storeToRefs(authStore)
const selectedAccount = ref<AccountDto | null>(null)

const accounts = ref<AccountDto[]>([])

async function fetchAccountsFromHolder() {
  const response = await getAccountsFromHolder(email.value)
  accounts.value = response.map((item) => ({
    ...item,
    name: item.name // Sort holders alphabetically
  }))
}

const formatDate = () => {
  const formattedDate = new Date(date.value).toISOString().split('T')[0]
  date.value = formattedDate
}

const paymentCreate = () => {
  const paymentDto: PaymentDto = {
    amount: amount.value,
    date: new Date(date.value),
    description: description.value,
    accountId: selectedAccountId.value || 0
  }
  createPayment(paymentDto)
  router.push(`/payments/${email.value}`)
}

fetchAccountsFromHolder()
</script>

<style scoped>
.create-payment {
  font-size: 20px;
  max-width: 600px;
  margin: auto;
  padding: 20px;
  text-align: center;
  color: #fff; /* Light text color */
}

h2 {
  color: #fff; /* Light text color */
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  margin-bottom: 5px;
  color: #ccc; /* Light text color */
}

input,
textarea,
select {
  width: 100%;
  padding: 10px;
  box-sizing: border-box;
  border: 1px solid #555; /* Darker border color */
  border-radius: 4px;
  background-color: #444; /* Darker background color */
  color: #fff; /* Light text color */
}

/* Style the placeholder text of the date input */
input[type='date']::placeholder {
  color: #fff; /* Light text color */
}

/* Style the calendar icon in the date input */
input[type='date']::-webkit-calendar-picker-indicator {
  filter: invert(1); /* Invert the color (white) */
}

button {
  background-color: #4caf50;
  color: white;
  padding: 10px 15px;
  border: none;
  cursor: pointer;
}

button:hover {
  background-color: #45a049;
}
</style>
