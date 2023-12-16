import 'dotenv/config';
import crypto from 'node:crypto';

export const PORT = process.env.PORT || 3000;

export const MASTER_PASSWORD =
  process.env.MASTER_PASSWORD || 'SuperSecretPassword';

export const BCRYPT_SALT_ROUNDS = 10;

const JWT_KEYS = crypto.generateKeyPairSync('rsa', {
  modulusLength: 2048,
  publicKeyEncoding: {
    type: 'spki',
    format: 'pem',
  },
  privateKeyEncoding: {
    type: 'pkcs8',
    format: 'pem',
  },
});
export const JWT_PRIVATE_KEY =
  process.env.JWT_PRIVATE_KEY || JWT_KEYS.privateKey;
export const JWT_PUBLIC_KEY = process.env.JWT_PUBLIC_KEY || JWT_KEYS.publicKey;
export const JWT_OPTIONS = {
  expiresIn: '1h',
  issuer: 'DNT',
  algorithm: 'rs256',
};

export const EMAIL_FROM = process.env.EMAIL_FROM || 'blingbank@dnt-sirs.com';
export const EMAIL_HOST = process.env.EMAIL_HOST || 'sandbox.smtp.mailtrap.io';
export const EMAIL_PORT = Number(process.env.EMAIL_PORT) || 2525;
export const EMAIL_USER = process.env.EMAIL_USER;
export const EMAIL_PASSWORD = process.env.EMAIL_PASSWORD;
