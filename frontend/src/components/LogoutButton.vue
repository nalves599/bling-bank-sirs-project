<template>
    <button @click="logout" class="logout-button">Logout</button>
  </template>
  
  <script lang="ts">
  export default {
    methods: {
      logout() {
        // Implement your logout logic here
        // For example, clear user session, perform API logout, etc.
        // Redirect to the home page
        this.$router.push('/');
      },
    },
  };
  </script>
  
  <style scoped>
  button.logout-button {
    position: fixed;
    bottom: 10px; /* Adjust as needed */
    right: 10px; /* Adjust as needed */
    background-color: #ff3333; /* Red color for logout button */
    color: white;
    padding: 10px 15px;
    border: none;
    cursor: pointer;
    border-radius: 5px;
  }
  
  button.logout-button:hover {
    background-color: #ff0000; /* Darker red on hover */
  }
  </style>
  