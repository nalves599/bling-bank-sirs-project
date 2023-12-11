<template>
  <div class="homepage">
    <h2 class="greeting">Hello {{ user.name }}!</h2>
    <h3>What would you like to do today?</h3>
    <div class="menu">
      <router-link :to="'/accounts/' + user.id">Accounts</router-link>
      <span class="separator"></span>
      <router-link :to="'/movements/' + user.id">Movements</router-link>
      <span class="separator"></span>
      <router-link :to="'/payments/' + user.id">Payments</router-link>
    </div>
    <router-view></router-view>
    <LogoutButton />
  </div>
</template>

<script lang="ts">
import LogoutButton from '@/components/LogoutButton.vue'

export default {
  data() {
    return {
      user: {
        id: 1, // Replace with the actual user's ID
        name: 'John Doe' // Replace with the actual user's name
      }
    }
  },
  components: {
    LogoutButton
  }
}
</script>

<style scoped>
.homepage {
  max-width: 800px;
  margin: auto;
  padding: 20px;
  text-align: center;
}

.greeting {
  font-size: 70px;
  color: #ffffff;
}

.menu {
  margin-top: 20px;
  font-size: 30px;
}

.separator {
  margin-right: 10px;
  margin-left: 10px;
  border-left: 1px solid #ccc;
  height: 20px;
  display: inline-block;
}
</style>
