// setup-env.tsx
import { Crypto } from '@peculiar/webcrypto';
const mockCrypto = new Crypto();

Object.defineProperty(window, 'crypto', {
  get(){
    return mockCrypto;
  }
})
