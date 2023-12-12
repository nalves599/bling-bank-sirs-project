<template>
  <div class="menu">
    <router-link :to="'/homepage/' + user.name">Homepage</router-link>
    <span class="separator"></span>
    <router-link :to="'/accounts/' + user.name">Accounts</router-link>
    <span class="separator"></span>
    <router-link :to="'/movements/' + user.name">Movements</router-link>
    <span class="separator"></span>
    <router-link :to="'/payments/' + user.name">Payments</router-link>
  </div>
</template>

<script lang="ts">
export default {
  name: 'BottomBar',
  data() {
    return {
      user: {
        name: this.$route.params.name
      }
    }
  }
}
</script>

<style scoped>
.menu {
  margin-top: 20px;
  font-size: 25px;
  text-align: center;
}
.menu-option {
  font-size: 30px;
  margin-right: 20px;
  text-decoration: none;
  color: #333;
  display: inline-block;
}
.separator {
  margin-right: 10px;
  margin-left: 10px;
  border-left: 1px solid #ccc;
  height: 20px;
  display: inline-block;
}
</style>
