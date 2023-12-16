import { ProtectProps, UnprotectProps } from 'blingbank-lib/dist/utils/crypto';

export const createProtectProp = (
  aesKey: CryptoKey,
  hmacKey?: CryptoKey,
  signingKey?: CryptoKey,
  nonce?: ArrayBuffer,
) => {
  const protectProp: ProtectProps = {
    aesKey,
    hmacKey,
    signingKey,
    nonce,
  };
  return protectProp;
};

export const createUnprotectProp = (
  aesKey: CryptoKey,
  iv?: ArrayBuffer,
  hmacKey?: CryptoKey,
  verifyingKey?: CryptoKey,
  nonceVerification?: (nonce: ArrayBuffer) => boolean,
): UnprotectProps => {
  const unprotectProp: UnprotectProps = {
    iv,
    aesKey,
    hmacKey,
    verifyingKey,
    nonceVerification,
  };
  return unprotectProp;
};
