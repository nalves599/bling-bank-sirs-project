<template>
  <div class="d-flex fill-height align-center justify-center">
    <v-card width="400" class="mx-auto">
      <!-- add Welcome to blingbank text -->
      <v-card-title>Welcome to BlingBank!</v-card-title>
      <v-card-title></v-card-title>
      <v-card-text>
        <v-form v-model="form" @submit.prevent="onSubmit">
          <v-text-field v-model="holderName" label="Holder Name" required></v-text-field>
          <v-text-field
            v-model="password"
            :type="showPassword ? 'text' : 'password'"
            name="password"
            label="Password"
            :append-inner-icon="showPassword ? 'mdi-eye' : 'mdi-eye-off'"
            @click:append-inner="togglePasswordVisibility"
            required
          >
          </v-text-field>
          <div v-if="error" class="error-message">{{ error }}</div>
          <v-btn type="submit" color="green" class="mr-4">Register</v-btn>
          <v-btn color="blue" @click="togglePasswordVisibility">View Password</v-btn>
        </v-form>
      </v-card-text>
    </v-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { register } from '@/services/api'
import { LoginRequestDto } from '@/models/LoginRequestDto'
import router from '@/router'

const holderName = ref('')
const password = ref('')
const showPassword = ref(false)
const form = ref(null)
const error = ref('')

function togglePasswordVisibility() {
  showPassword.value = !showPassword.value
}

function setError(message: string) {
  error.value = message
}

async function onSubmit() {
  try {
    const loginRequestDto: LoginRequestDto = {
      holderName: holderName.value,
      password: password.value
    }

    await register(loginRequestDto)

    router.push(`/homepage/${holderName.value}`)
  } catch (error) {
    console.log(error)
    setError('Could not register. Please try again.')
  }
}
</script>

<style scoped>
.login {
  max-width: 400px;
  margin: auto;
  padding: 20px;
  text-align: center;
}

.login h1 {
  color: #ffffff;
}

.login-form {
  margin-top: 20px;
}

.form-group {
  margin-bottom: 15px;
}

input {
  width: 100%;
  padding: 10px;
  margin-bottom: 10px;
  box-sizing: border-box;
  color: #ffffff;
  border: 1px solid #ccc;
  border-radius: 4px;
  box-shadow: 0 1px 1px rgba(0, 0, 0, 0.075);
}

.password-input {
  position: relative;
}

input[type='checkbox'] {
  margin-right: 5px;
  cursor: pointer;
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
  margin-top: 0px;
  margin-bottom: 10px;
}
</style>
