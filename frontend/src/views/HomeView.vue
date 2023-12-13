<template>
  <v-app class="main-page">
    <div class="main-page">
      <h1>BlingBank</h1>
      <h2>The safest bank on the planet</h2>
      <div>
        <router-link to="/register" class="login-button">Register</router-link>
        <router-link to="/login" class="login-button login-button-margin">Login</router-link>
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
  font-size: 148px;
  margin-bottom: 20px;
}

h2 {
  font-size: 48px;
  margin-bottom: 40px;
}

.login-button {
  background-color: #4caf50;
  color: white;
  padding: 15px 30px;
  text-decoration: none;
  font-size: 20px;
  border-radius: 5px;
  margin-right: 30px; /* Margin between buttons */
}

.login-button:hover {
  background-color: #45a049;
}

.login-button-margin {
  margin-right: 10px; /* Adjust the margin as needed */
}
</style>
