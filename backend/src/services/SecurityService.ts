import { generate as randomWords } from 'random-words';
import nodeCrypto from 'node:crypto';
import { crypto } from 'blingbank-lib';

import { MASTER_PASSWORD } from '../config';
import db from '../database';
import {
  createKeyEncryptionKey,
  createProtectProp,
  createUnprotectProp,
} from './EncryptionService';

enum SecretType {
  SHARED_SECRET = 'SHARED_SECRET',
  MASTER_PASSWORD = 'MASTER_PASSWORD',
  SESSION_KEY = 'SESSION_KEY',
}

const sharedSecrets = new Map<string, string>(); // <userID, sharedSecret>

const sessionKeys = new Map<string, string>(); // <userID, sessionKey>

const masterKey = nodeCrypto.createSecretKey(MASTER_PASSWORD, 'utf8'); // FIXME: Use blingbank-lib

let keyEncryptionKey: CryptoKey;

let hmacKey: CryptoKey;

export const generateSharedSecret = () => {
  const friendlySharedSecret = 'cao-gato-aviao'; // FIXME: Generate 3 random words
  const sharedSecret = friendlySharedSecret; // FIXME: Encrypt shared secret

  return {
    friendlySharedSecret,
    sharedSecret,
  };
};

export const saveSharedSecret = async (
  userID: string,
  sharedSecret: string,
) => {
  if (!keyEncryptionKey) {
    // TODO: Improve this
    keyEncryptionKey = await createKeyEncryptionKey();
    hmacKey = await crypto.createHMACKey();
  }
  const protectProp = createProtectProp(keyEncryptionKey, hmacKey);
  const { messageEncrypted } = await crypto.protect(
    Buffer.from(sharedSecret, 'utf8'),
    protectProp,
  );
  const encryptedSharedSecret =
    Buffer.from(messageEncrypted).toString('base64');

  await db.secret.create({
    data: {
      type: SecretType.SHARED_SECRET,
      key: userID,
      value: encryptedSharedSecret,
    },
  });

  sharedSecrets.set(userID, encryptedSharedSecret);
};

export const getSharedSecret = async (userID: string) => {
  let encryptedSharedSecret;
  if (sharedSecrets.has(userID)) {
    encryptedSharedSecret = sharedSecrets.get(userID)!;
  } else {
    const entry = await db.secret.findFirst({
      where: {
        type: SecretType.SHARED_SECRET,
        key: userID,
      },
    });

    if (!entry) {
      throw new Error('Shared secret not found');
    }
    encryptedSharedSecret = entry.value;
  }

  const unprotectProp = createUnprotectProp(
    keyEncryptionKey,
    undefined,
    hmacKey,
  );
  const { payload } = await crypto.unprotect(
    Buffer.from(encryptedSharedSecret, 'base64'),
    unprotectProp,
  );
  const sharedSecret = Buffer.from(payload).toString('utf8');

  return sharedSecret;
};

export const generatePOWChallenge = () => {
  const challenge = (Math.random() * 1000).toFixed(0); // TODO: Improve challenge
  return String(challenge);
};

export const solvePOWChallenge = (challenge: string) => {
  const solution = parseInt(challenge) * 39; // TODO: Improve challenge
  return String(solution);
};

export const encryptWithSharedSecret = (data: string, sharedSecret: string) => {
  // FIXME: Encrypt data with shared secret using blingbank-lib
  const encryptedData = data;
  return encryptedData;
};

export const generateSessionKey = () => {
  const sessionKey = crypto.createAESKey();

  return sessionKey;
};

export const saveSessionKey = async (userID: string, sessionKey: string) => {
  const encryptedSessionKey = sessionKey; // FIXME: Encrypt session key

  await db.secret.create({
    data: {
      type: SecretType.SESSION_KEY,
      key: userID,
      value: encryptedSessionKey,
    },
  });

  sessionKeys.set(userID, encryptedSessionKey); // FIXME: Encrypt session key
};

export const getSessionKey = async (userID: string) => {
  if (sessionKeys.has(userID)) {
    return sessionKeys.get(userID); // FIXME: Decrypt session key
  }

  const encryptedSessionKey = await db.secret.findFirst({
    where: {
      type: SecretType.SESSION_KEY,
      key: userID,
    },
  });

  if (!encryptedSessionKey) {
    throw new Error('Session key not found');
  }

  const sessionKey = encryptedSessionKey.value; // FIXME: Decrypt session key
  return sessionKey;
};
