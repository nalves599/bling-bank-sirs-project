<template>
  <v-app class="main-page">
    <div class="main-page" height="100vh" width="100vw">
      <h1>BlingBank</h1>
      <h2>The safest bank on the planet</h2>
      <router-link to="/login" class="login-button">Login</router-link>
    </div>
  </v-app>
</template>

<script setup lang="ts">
</script>

<style scoped>
.main-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh; /* Set to 100% of the viewport height */
  color: #ffffff; /* Light text color */
  background-color: transparent; /* Fallback color */
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
}

.login-button:hover {
  background-color: #45a049;
}
</style>
