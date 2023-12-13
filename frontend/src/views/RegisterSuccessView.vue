<template>
  <v-app class="main-page">
    <div class="main-page">
      <h1>Successfully registered!</h1>
      <div>
        <router-link to="/login" class="login-button">Login</router-link>
      </div>
    </div>
  </v-app>
</template>

<script setup lang="ts"></script>

<style scoped>
.main-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  color: #ffffff;
  background-color: transparent;
}

h1 {
  font-size: 55px;
  margin-bottom: 20px;
}

.login-button {
  background-color: #4caf50;
  color: white;
  padding: 15px 30px;
  text-decoration: none;
  font-size: 20px;
  border-radius: 5px;
  margin-top: 20px; /* Adjusted to match the margin of h1 */
}

.login-button:hover {
  background-color: #45a049;
}
</style>
