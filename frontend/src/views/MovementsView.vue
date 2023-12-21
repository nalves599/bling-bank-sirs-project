<template>
  <div class="account-selector">
    <h1 style="color: white">Movements</h1>

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
        :key="account.id"
        :value="account.id"
        style="background-color: #f8f8f8"
      >
        {{ account.name }}
      </option>
    </select>

    <!-- Display movements of selected account -->
    <div v-if="selectedAccount" class="account-details">
      <div v-if="movements.length != 0">
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
      <div v-if="movements.length === 0">
        <form @submit.prevent="fetchAccountMovements">
          <div class="form-group">
            <label for="shamir">Shamir Secret:</label>
            <input v-model="shamir" id="shamir" type="text" required />
          </div>

          <button type="submit">Unlock</button>
        </form>
      </div>
    </div>

    <BottomBar />
    <LogoutButton />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue'
import { getAccountsFromHolder, getAccountMovements } from '@/services/api'
import type { AccountDto } from '@/models/AccountDto'
import type { MovementDto } from '@/models/MovementDto'
import BottomBar from '@/components/BottomBar.vue'
import LogoutButton from '@/components/LogoutButton.vue'
import { unlockAccount } from '@/services/api'

const accounts = ref<AccountDto[]>([])
const movements = ref<MovementDto[]>([])
const selectedAccountId = ref<string | null>(null)
const shamir = ref('')

async function fetchAccountsFromHolder() {
  accounts.value = await getAccountsFromHolder()
}

onMounted(() => {
  fetchAccountsFromHolder()
})

const sortedAccounts = computed(() => {
  return [...accounts.value].sort((a, b) => {
    const idA = parseInt(a.name)
    const idB = parseInt(b.name)
    return idA - idB
  })
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
// get account movements
async function fetchAccountMovements() {
  await unlockAccount(selectedAccount.value?.id || '', shamir.value)
  movements.value = await getAccountMovements(selectedAccount.value?.id || '')
}

const headers = [
  { title: 'Movement ID', key: 'movementId', sortable: true },
  { title: 'Date', key: 'movementDate', sortable: true },
  { title: 'Description', key: 'movementDescription', sortable: false },
  { title: 'Value', key: 'movementValue', sortable: true }
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
</style>

<style scoped>
.create-account {
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
select,
VueMultiselect {
  width: 100%;
  padding: 10px;
  box-sizing: border-box;
  border: 1px solid #555; /* Darker border color */
  border-radius: 4px;
  background-color: #444; /* Darker background color */
  color: #fff; /* Light text color */
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

.error-message {
  color: red;
  margin-top: 10px;
}
</style>
