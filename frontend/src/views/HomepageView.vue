<template>
  <div class="homepage">
    <h2 class="greeting">hello {{ user.name }}!</h2>
    <h3>What would you like to do today?</h3>
    
    <BottomBar />
    <LogoutButton />
  </div>
</template>

<script lang="ts">
import LogoutButton from '@/components/LogoutButton.vue'
import BottomBar from '@/components/BottomBar.vue'

export default {
  data() {
    return {
      user: {
        name: this.$route.params.name
      }
    }
  },
  components: {
    LogoutButton,
    BottomBar
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
</style>
