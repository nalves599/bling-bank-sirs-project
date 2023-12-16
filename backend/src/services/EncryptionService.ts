import { crypto } from 'blingbank-lib';
import { webcrypto } from 'crypto';

const keyCrypto = webcrypto.subtle;

export const createProtectProp = (
  aesKey: CryptoKey,
  hmacKey?: CryptoKey,
  signingKey?: CryptoKey,
  nonce?: ArrayBuffer,
) => {
  const protectProp = {
    aesKey,
    hmacKey,
    signingKey,
    nonce,
  };
  return protectProp;
};

export const createUnprotectProp = (
  aesKey: CryptoKey,
  vi?: ArrayBuffer,
  hmacKey?: CryptoKey,
  verifyingKey?: CryptoKey,
  nonceVerication?: (nonce: ArrayBuffer) => boolean,
) => {
  const unprotectProp = {
    vi,
    aesKey,
    hmacKey,
    verifyingKey,
    nonceVerication,
  };
  return unprotectProp;
};

export const createKeyEncryptionKey = async () => {
  try {
    const keyEncryptionKey = await crypto.createAESKey();
    return keyEncryptionKey;
  } catch (error) {
    console.error(error);
    throw error; // Re-throw the error to handle it elsewhere if needed
  }
};

export const stringToAESKey = async (aes: string) => {
  const data = Buffer.from(aes, 'utf8');
  try {
    return await keyCrypto.importKey('raw', data, 'AES-CBC', true, [
      'encrypt',
      'decrypt',
    ]);
  } catch (error: any) {
    console.log('Could not import key: sad ' + error.message);
    throw error;
  }
};
