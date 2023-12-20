<template>
  <div class="create-account">
    <h2>Create Account</h2>
    <form @submit.prevent="createAcc">
      <div class="form-group">
        <VueMultiselect v-model="selected" :options="options" :multiple="true"></VueMultiselect>
      </div>
      <div class="form-group">
        <label for="accountname">Name:</label>
        <input v-model="accountname" id="accountname" type="text" required />
      </div>
      <div class="form-group">
        <label for="currency">Currency:</label>
        <select v-model="currency" id="currency" required>
          <option value="EUR">EUR</option>
          <option value="USD">USD</option>
          <option value="BTC">BTC</option>
        </select>
      </div>
      <button type="submit">Create</button>
    </form>
    <div v-if="error" class="error-message">{{ error }}</div>
    <BottomBar />
    <LogoutButton />
  </div>
</template>

<script setup lang="ts">
import BottomBar from '@/components/BottomBar.vue'
import LogoutButton from '@/components/LogoutButton.vue'
import VueMultiselect from 'vue-multiselect'
import { getHolders } from '@/services/api'
import { ref, onMounted } from 'vue'
import { AccountDto } from '@/models/AccountDto'
import router from '@/router'
import { createAccount } from '@/services/api'
import { storeToRefs } from 'pinia'
import { useAuthStore } from '@/stores/auth'

const selected = ref([])
const options = ref([])
const accountname = ref('')
const currency = ref('')
const error = ref('')

onMounted(async () => {
  options.value = await getHolders().then((response) => {
    return response.map((item) => item.email)
  })
})

const authStore = useAuthStore()
const { email } = storeToRefs(authStore)

async function createAcc() {
  try {
    const accountDto: AccountDto = {
      accountHolders: selected.value.map((item: string) => item),
      name: accountname.value,
      currency: currency.value
    }

    await createAccount(accountDto)

    router.push(`/accounts/${email.value}`)
  } catch (e) {
    // Set the error message if there's an issue
    error.value = 'Failed to create the account. You must include your user as one of the holders.'
    console.error(e)
  }
}
</script>

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

<style src="vue-multiselect/dist/vue-multiselect.css"></style>
