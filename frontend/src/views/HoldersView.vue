<template>
  <div class="holders">
    <h1>This is a holders page</h1>
    <div v-for="(holder, index) in holders" :key="index">
      <h2>{{ holder.holderName }}</h2>
      <!-- Replace 'name' with actual property names -->
      <!-- Add more properties here -->
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { getHolders } from '@/services/api'
import type { HolderDto } from '@/models/HolderDto'

const holders = ref<HolderDto[]>([])

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
</style>
