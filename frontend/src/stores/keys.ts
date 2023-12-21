import { ref } from 'vue'
import { defineStore } from 'pinia'

export const useKeyStore = defineStore('keys', () => {
  const secretShared = ref('')
  const sessionKey = ref()
  const privateKey = ref()
  const publicKey = ref('')

  function setSecretShared(secret: string) {
    secretShared.value = secret
  }

  function setSessionKey(key: Uint8Array) {
    sessionKey.value = key
  }

  function setPrivateKey(key: CryptoKey) {
    privateKey.value = key
  }

  function setPublicKey(key: string) {
    publicKey.value = key
  }

  return {
    secretShared,
    sessionKey,
    privateKey,
    publicKey,
    setSecretShared,
    setSessionKey,
    setPrivateKey,
    setPublicKey
  }
})
