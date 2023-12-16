import { crypto } from 'blingbank-lib';

export const generateSessionKey = () => crypto.createAESKey();

export const generateSharedSecret = () => {
  const friendlySharedSecret = 'cao-gato-aviao'; // FIXME: Generate 3 random words
  const sharedSecret = friendlySharedSecret;

  return {
    friendlySharedSecret,
    sharedSecret,
  };
};

export const createMasterKey = () => crypto.createAESKey();
export const createHMACKey = () => crypto.createHMACKey();
export const createKeyEncryptionKey = () => crypto.createAESKey();
