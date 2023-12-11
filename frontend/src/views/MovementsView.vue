<template>
  <div class="movements">
    <h1>This is a holders page</h1>
    <div v-for="(holder, index) in holders" :key="index">
      <h2>{{ holder.holderName }}</h2>
      <!-- Replace 'name' with actual property names -->
      <!-- Add more properties here -->
    </div>
    <BottomBar />
    <LogoutButton />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { getHolders } from '@/services/api'
import type { HolderDto } from '@/models/HolderDto'
import BottomBar from '@/components/BottomBar.vue'
import LogoutButton from '@/components/LogoutButton.vue';

const holders = ref<HolderDto[]>([])

// TODO - CHANGE INFO TO MOVEMENTS INSTEAD OF HOLDERS
async function fetchHolders() {
  holders.value = await getHolders()
}

fetchHolders()
</script>

<style>
@media (min-width: 1024px) {
  .about {
    min-height: 100vh;
    display: flex;
    align-items: center;
  }
}

.movements {
  max-width: 800px;
  margin: auto;
  padding: 20px;
  text-align: center;
}
</style>
