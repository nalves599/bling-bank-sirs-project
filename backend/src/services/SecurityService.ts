import { generate, generate as randomWords } from 'random-words';
import { crypto } from 'blingbank-lib';

import db from '../database';
import {
  createKeyEncryptionKey,
  createProtectProp,
  createUnprotectProp,
  stringToAESKey,
} from './EncryptionService';

enum SecretType {
  SHARED_SECRET = 'SHARED_SECRET',
  MASTER_PASSWORD = 'MASTER_PASSWORD',
  SESSION_KEY = 'SESSION_KEY',
}

const sharedSecrets = new Map<string, string>(); // <userID, sharedSecret>

const sessionKeys = new Map<string, string>(); // <userID, sessionKey>

let masterKey: CryptoKey;
let keyEncryptionKey: CryptoKey;
let hmacKey: CryptoKey;

export const generateSharedSecret = () => {
  const friendlySharedSecret = 'cao-gato-aviao'; // FIXME: Generate 3 random words
  const sharedSecret = friendlySharedSecret;

  return {
    friendlySharedSecret,
    sharedSecret,
  };
};

export const saveSharedSecret = async (
  userID: string,
  sharedSecret: string,
) => {
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

export const encryptWithSharedSecret = async (
  data: string,
  sharedSecret: string,
) => {
  const sharedKey = await stringToAESKey(sharedSecret);
  const protectProp = createProtectProp(sharedKey);

  const { messageEncrypted } = await crypto.protect(
    Buffer.from(data, 'utf8'),
    protectProp,
  );

  return Buffer.from(messageEncrypted).toString('base64');
};

export const generateSessionKey = async () => {
  const sessionKey = await crypto.createAESKey();

  return sessionKey;
};

export const saveSessionKey = async (userID: string, sessionKey: string) => {
  const protectProp = createProtectProp(keyEncryptionKey, hmacKey);
  const { messageEncrypted } = await crypto.protect(
    Buffer.from(sessionKey, 'utf8'),
    protectProp,
  );
  const encryptedSessionKey = Buffer.from(messageEncrypted).toString('base64');

  await db.secret.create({
    data: {
      type: SecretType.SESSION_KEY,
      key: userID,
      value: encryptedSessionKey,
    },
  });

  sessionKeys.set(userID, encryptedSessionKey);
};

export const getSessionKey = async (userID: string) => {
  let encryptedSessionKey;
  if (sessionKeys.has(userID)) {
    encryptedSessionKey = sessionKeys.get(userID)!;
  } else {
    const entry = await db.secret.findFirst({
      where: {
        type: SecretType.SESSION_KEY,
        key: userID,
      },
    });

    if (!entry) {
      throw new Error('Session secret not found');
    }
    encryptedSessionKey = entry.value;
  }

  const unprotectProp = createUnprotectProp(
    keyEncryptionKey,
    undefined,
    hmacKey,
  );
  const { payload } = await crypto.unprotect(
    Buffer.from(encryptedSessionKey, 'base64'),
    unprotectProp,
  );
  const sessionKey = Buffer.from(payload).toString('utf8');

  return sessionKey;
};

async function startSecurity() {
  masterKey = await crypto.createAESKey();
  keyEncryptionKey = await createKeyEncryptionKey();
  hmacKey = await crypto.createHMACKey();
}

startSecurity();
