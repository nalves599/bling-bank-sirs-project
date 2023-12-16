import { crypto } from 'blingbank-lib';

import db from '../database';
import { createProtectProp, createUnprotectProp } from './PropCreator';
import {
  stringToAESKey,
  AESKeytoBuffer,
  bufferToAESKey,
  bufferToString,
  stringToBuffer,
} from './KeyConverter';
import {
  createHMACKey,
  createKeyEncryptionKey,
  createMasterKey,
} from './KeyGenerator';

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

export const saveSharedSecret = async (
  userID: string,
  sharedSecret: string,
) => {
  const protectProp = createProtectProp(keyEncryptionKey, hmacKey);
  const { messageEncrypted } = await crypto.protect(
    await stringToBuffer(sharedSecret),
    protectProp,
  );
  const encryptedSharedSecret = await bufferToString(
    Buffer.from(messageEncrypted),
  );

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

  return await bufferToString(Buffer.from(payload));
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
    await stringToBuffer(data),
    protectProp,
  );

  return await bufferToString(Buffer.from(messageEncrypted));
};

export const saveSessionKey = async (userID: string, sessionKey: CryptoKey) => {
  const protectProp = createProtectProp(keyEncryptionKey, hmacKey);
  const { messageEncrypted } = await crypto.protect(
    await AESKeytoBuffer(sessionKey),
    protectProp,
  );
  const encryptedSessionKey = await bufferToString(
    Buffer.from(messageEncrypted),
  );
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
    await stringToBuffer(encryptedSessionKey),
    unprotectProp,
  );

  return await bufferToAESKey(Buffer.from(payload));
};

async function startSecurity() {
  masterKey = await createMasterKey();
  keyEncryptionKey = await createKeyEncryptionKey();
  hmacKey = await createHMACKey();
}

startSecurity();
