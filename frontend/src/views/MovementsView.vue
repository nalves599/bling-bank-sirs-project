<template>
  <div class="movements">
    
    <BottomBar />
    <LogoutButton />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { getHolders } from '@/services/api'
import type { HolderDto } from '@/models/HolderDto'
import BottomBar from '@/components/BottomBar.vue'
import LogoutButton from '@/components/LogoutButton.vue'

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

.movements {
  max-width: 800px;
  margin: auto;
  padding: 20px;
  text-align: center;
}
</style>
