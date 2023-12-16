import { crypto } from 'blingbank-lib';

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
