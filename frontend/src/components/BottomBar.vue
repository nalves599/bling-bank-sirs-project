<template>
  <v-menu>
    <template #activator="{ props }">
      <v-btn v-bind="props">
        <v-avatar icon="mdi-account" size="32" />
        {{ token ? `Welcome ${username} ` : "You're not logged in" }}
      </v-btn>
    </template>

    <v-list>
      <v-list-item
        v-if="!token"
        prepend-icon="mdi-login"
        :to="{ name: 'login' }"
        title="Login"
      ></v-list-item>

      <!-- TODO: fix this-->
      <v-list-item
        v-if="token"
        :to="{ name: `/accounts/${username}` }"
        title="Accounts"
      ></v-list-item>

      <v-list-item
        v-if="token"
        :to="{ name: `movements/${username}` }"
        title="Movements"
      ></v-list-item>

      <v-list-item
        v-if="token"
        prepend-icon="mdi-logout"
        :to="{ name: `payments/${username}` }"
        title="Payments"
      ></v-list-item>
    </v-list>
  </v-menu>
</template>

<script setup lang="ts">
import { useAuthStore } from '@/stores/auth'
import { storeToRefs } from 'pinia'

const authStore = useAuthStore()
const { token, username } = storeToRefs(authStore)
</script>
