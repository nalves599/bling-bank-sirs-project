import { generate as randomWords } from 'random-words';
import nodeCrypto from 'node:crypto';
import { crypto } from 'blingbank-lib';

import { MASTER_PASSWORD } from '../config';
import db from '../database';

enum SecretType {
  SHARED_SECRET = 'SHARED_SECRET',
  MASTER_PASSWORD = 'MASTER_PASSWORD',
}

const sharedSecrets = new Map<string, string>();

const masterKey = nodeCrypto.createSecretKey(MASTER_PASSWORD, "utf8"); // FIXME: Use blingbank-lib

export const generateSharedSecret = () => {
  const friendlySharedSecret = "cao-gato-aviao"; // FIXME: Generate 3 random words
  const sharedSecret = nodeCrypto.createHash('sha256').update(friendlySharedSecret).digest('hex'); // FIXME: Use blingbank-lib

  return {
    friendlySharedSecret,
    sharedSecret,
  };
}

export const saveSharedSecret = async (userID: string, sharedSecret: string) => {
  const encryptedSharedSecret = sharedSecret; // FIXME: Encrypt shared secret

  await db.secret.create({
    data: {
      type: SecretType.SHARED_SECRET,
      key: userID,
      value: encryptedSharedSecret,
    },
  });

  sharedSecrets.set(userID, sharedSecret);
}

export const getSharedSecret = async (userID: string) => {
  if (sharedSecrets.has(userID)) {
    return sharedSecrets.get(userID);
  }

  const encryptedSharedSecret = await db.secret.findFirst({
    where: {
      type: SecretType.SHARED_SECRET,
      key: userID,
    },
  });

  if (!encryptedSharedSecret) {
    throw new Error('Shared secret not found');
  }

  const sharedSecret = encryptedSharedSecret.value; // FIXME: Decrypt shared secret
  return sharedSecret;
}

export const generatePOWChallenge = () => {
  const challenge = (Math.random() * 1000).toFixed(0); // TODO: Improve challenge
  return String(challenge);
}

export const solvePOWChallenge = (challenge: string) => {
  const solution = parseInt(challenge) * 39; // TODO: Improve challenge
  return String(solution);
}

export const encryptWithSharedSecret = (data: string, sharedSecret: string) => {
  // FIXME: Encrypt data with shared secret using blingbank-lib
  const encryptedData = data;
  return encryptedData;
}
