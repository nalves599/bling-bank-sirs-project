<template>
  <div class="create-payment">
    <h2>Create Payment</h2>
    <form @submit.prevent="createPayment">
      <div class="form-group">
        <label for="destination">Destination:</label>
        <input type="text" v-model="destination" id="destination" required />
      </div>
      <div class="form-group">
        <label for="amount">Amount:</label>
        <input type="number" v-model="amount" id="amount" required />
      </div>
      <div class="form-group">
        <label for="date">Date to be processed:</label>
        <input type="date" v-model="date" id="date" required />
      </div>
      <div class="form-group">
        <label for="description">Description:</label>
        <textarea v-model="description" id="description" required></textarea>
      </div>
      <div class="form-group">
        <label for="accountDropdown">Source Account:</label>
        <select v-model="selectedAccountId" id="accountDropdown" @change="fetchAccountsFromHolder">
          <option value="" disabled>Select an account</option>
          <option
            v-for="account in sortedAccounts"
            :key="account.accountId"
            :value="account.accountId"
          >
            {{ account.accountId }} - Balance: {{ account.balance }} - Holders:
            {{ account.holders.sort().join(', ') }}
          </option>
        </select>
      </div>
      <button type="submit">Submit Payment</button>
    </form>
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

const destination = ref('')
const amount = ref(0)
const date = ref('')
const description = ref('')
const selectedAccountId = ref<string | null>(null)

const authStore = useAuthStore()
const { username } = storeToRefs(authStore)

const accounts = ref<AccountDto[]>([])

onMounted(() => {
  fetchAccountsFromHolder()
})

const fetchAccountsFromHolder = async () => {
  accounts.value = await getAccountsFromHolder(username.value)
}

const sortedAccounts = computed(() => {
  return [...accounts.value].sort((a, b) => {
    const idA = parseInt(a.accountId)
    const idB = parseInt(b.accountId)
    return idA - idB
  })
})

watch(
  () => selectedAccountId.value,
  (newVal) => {
    if (newVal) {
      selectedAccount.value = accounts.value.find((account) => account.accountId === newVal) || null
    }
  }
)

const selectedAccount = ref<AccountDto | null>(null)

const createPayment = () => {
  console.log(destination, amount, date, description, selectedAccountId)
}
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
