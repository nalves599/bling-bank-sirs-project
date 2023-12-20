import { v4 as uuid } from "uuid";
import { crypto } from "blingbank-lib";

import db from "../database";
import { MASTER_KEY } from "../config";

enum SecretType {
  SHARED_SECRET = "SHARED_SECRET",
  SESSION_KEY = "SESSION_KEY",
  SHAMIR_SECRET = "SHAMIR_SECRET",
}

const sharedSecrets = new Map<string, ArrayBuffer>(); // <userId, sharedSecret>
const sessionKeys = new Map<string, ArrayBuffer>(); // <sessionId, sessionKey>
const shamirSecrets = new Map<string, string>(); // <accountId, base64(shamirSecret)>
const lastNonces = new Map<string, ArrayBuffer>(); // <sessionId, lastNonce>

export const saveSharedSecret = async (
  userId: string,
  sharedSecret: ArrayBuffer,
) => {
  const { messageEncrypted } = await crypto.protect(sharedSecret, {
    aesKey: MASTER_KEY,
  });

  await db.secret.create({
    data: {
      type: SecretType.SHARED_SECRET,
      key: userId,
      value: crypto.bufferToHex(messageEncrypted as ArrayBuffer),
    },
  });

  sharedSecrets.set(userId, messageEncrypted as ArrayBuffer);
};

export const getSharedSecret = async (userId: string) => {
  if (sharedSecrets.has(userId)) {
    return sharedSecrets.get(userId);
  }

  const entry = await db.secret.findFirst({
    where: {
      type: SecretType.SHARED_SECRET,
      key: userId,
    },
  });

  if (!entry) {
    throw new Error("Shared secret not found");
  }

  const encryptedSharedSecret = crypto.hexToBuffer(entry.value);
  const { payload: sharedSecret } = await crypto.unprotect(
    encryptedSharedSecret,
    {
      aesKey: MASTER_KEY,
    },
  );

  return sharedSecret;
};

export const createSessionKey = async () => {
  const sessionId = uuid();
  const sessionKey = await crypto.generateKey();

  sessionKeys.set(sessionId, sessionKey);

  return { sessionId, sessionKey };
};

export const getSessionKey = (sessionId: string) => {
  if (sessionKeys.has(sessionId)) {
    return sessionKeys.get(sessionId);
  }

  throw new Error("Session key not found");
};

export const saveShamirSecret = async (
  accountId: string,
  secret: ArrayBuffer,
) => {
  const shamirSecret = Buffer.from(secret).toString("base64");
  const { messageEncrypted } = await crypto.protect(shamirSecret, {
    aesKey: MASTER_KEY,
  });

  await db.secret.create({
    data: {
      type: SecretType.SHAMIR_SECRET,
      key: accountId,
      value: crypto.bufferToHex(messageEncrypted as ArrayBuffer),
    },
  });
};

export const getShamirSecret = async (accountId: string) => {
  if (shamirSecrets.has(accountId)) {
    return shamirSecrets.get(accountId);
  }

  const entry = await db.secret.findFirst({
    where: {
      type: SecretType.SHAMIR_SECRET,
      key: accountId,
    },
  });

  if (!entry) {
    throw new Error("Shamir secret not found");
  }

  const encryptedShamirSecret = crypto.hexToBuffer(entry.value);
  const { payload: shamirSecret } = await crypto.unprotect(
    encryptedShamirSecret,
    {
      aesKey: MASTER_KEY,
    },
  );

  return crypto.decoder.decode(shamirSecret);
};

export const decryptWithSessionKey = async (
  encrypted: string,
  sessionId: string,
) => {
  const sessionKey = getSessionKey(sessionId);
  if (!sessionKey) {
    throw new Error("Session key not found");
  }

  const lastNonce = lastNonces.get(sessionId);
  const decrypted = await crypto.paramUnprotect(
    encrypted,
    sessionKey,
    crypto.nonceCheck(lastNonce),
  );
  return crypto.decoder.decode(decrypted);
};
