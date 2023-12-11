<template>
  <div class="d-flex fill-height align-center justify-center">
    <v-card width="400" class="mx-auto">
      <v-card-title>Login</v-card-title>
      <v-card-text>
        <v-form v-model="form" @submit.prevent="onSubmit">
          <v-text-field v-model="holderName" label="Holder Name" required></v-text-field>
          <v-text-field
            v-model="password"
            :type="showPassword ? 'text' : 'password'"
            name="password"
            label="Password"
            :append-inner-icon="showPassword ? 'mdi-eye' : 'mdi-eye-off'"
            @click:append-inner="showPassword = !showPassword"
            required
          >
          </v-text-field>

          <v-btn type="submit" color="primary" class="mr-4">Login</v-btn>
        </v-form>
      </v-card-text>
    </v-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { login } from '@/services/api'
import { LoginRequestDto } from '@/models/LoginRequestDto'
import router from '@/router'
import { useRedirectStore } from '@/stores/redirect'

const { url, setUrl } = useRedirectStore()
const holderName = ref('')
const password = ref('')
const showPassword = ref(false)
const form = ref(null)

async function onSubmit() {
  try {
    const loginRequestDto: LoginRequestDto = {
      holderName: holderName.value,
      password: password.value
    }

    await login(loginRequestDto)

    if (url) {
      await router.push(url) // redirect to url
      setUrl(undefined)
    } else {
      await router.push('/') // redirect to home page
    }
  } catch (error) {
    console.log(error)
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
</style>
