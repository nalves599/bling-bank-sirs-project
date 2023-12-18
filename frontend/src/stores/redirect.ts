import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useRedirectStore = defineStore('redirect', () => {
  const url = ref<string | undefined>()

  function setUrl(newUrl: string | undefined) {
    url.value = newUrl
  }

  return { url, setUrl }
})
