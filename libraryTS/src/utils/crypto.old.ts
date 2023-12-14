import * as crypto from 'node:crypto';

export type SymmetricAlgorithm = 'aes-256-cbc';

// Create a symmetric AES key
export const createSymmetricKey = (length?: 256) => {
  const key = crypto.generateKeySync('aes', { length: length || 256 });
  return key;
}

// Create a key pair
export const createKeyPair = (length?: 2048|4096) => {
  const keys = crypto.generateKeyPairSync('rsa', {
    modulusLength: length||2048,
    publicKeyEncoding: {
      type: 'spki',
      format: 'pem'
    },
    privateKeyEncoding: {
      type: 'pkcs8',
      format: 'pem'
    }
  });

  const publicKey = crypto.createPublicKey(keys.privateKey);
  const privateKey = crypto.createPrivateKey(keys.privateKey);

  return { publicKey, privateKey };
}

// Generate a public key from a private key
export const getPublicKey = (privateKey: string) => {
  const publicKey = crypto.createPublicKey(privateKey);
  return publicKey;
}

// Sign a message with a private key
export const signMessage = (message: Buffer, privateKey: crypto.KeyObject) => {
  const signature = crypto.sign(null, message, privateKey);
  return signature;
}

// Generate HMAC
export const generateHMAC = (message: Buffer, key: crypto.KeyObject) => {
  const hmac = crypto.createHmac('sha256', key);
  hmac.update(message);
  const hmacDigest = hmac.digest();
  return hmacDigest;
}

// Verify signature with public key
export const verifySignature = (message: Buffer, signature: Buffer, publicKey: crypto.KeyObject) => {
  const verified = crypto.verify(null, message, publicKey, signature);
  return verified;
}

// Encrypt a message with a symmetric key
export const encryptMessage = (message: Buffer, key: crypto.KeyObject, algorithm?: SymmetricAlgorithm) => {
  const iv = crypto.randomBytes(16);
  const cipher = crypto.createCipheriv(algorithm || 'aes-256-cbc', key, iv);
  const ciphertext = Buffer.concat([cipher.update(message), cipher.final()]);
  return { iv, ciphertext };
}

// Decrypt a message with a symmetric key
export const decryptMessage = (encrypted: Buffer, key: crypto.KeyObject, iv: Buffer, algorithm?: SymmetricAlgorithm) => {
  const decipher = crypto.createDecipheriv(algorithm || 'aes-256-cbc', key, iv);
  const message = Buffer.concat([decipher.update(encrypted), decipher.final()]);
  return message;
}

// Hash a message
export const hashMessage = (message: Buffer, algorithm?: string) => {
  const hash = crypto.createHash(algorithm || 'sha256');
  hash.update(message);
  const digest = hash.digest();
  return digest;
}

// Generate timestamp nonce
export const generateNonce = () => {
    const nonce = Buffer.alloc(8);
    nonce.writeBigInt64BE(BigInt(Date.now()));
    return nonce;
}


export type ProtectProps = {
  key: crypto.KeyObject,
  signingKey?: crypto.KeyObject,
  nonce?: Buffer,
}

// Protect data
// Structure of the protected data:
// [payloadLength: 4 bytes][payload: payloadLength bytes][nonceLength: 4 bytes][nonce: 8 bytes][signatureLength: 4 bytes][signature: signatureLength bytes]
export const protect = (data: Buffer, props: ProtectProps) => {
  let { key, signingKey, nonce } = props;
  let signature: Buffer | null = null;

  const payloadLength = Buffer.alloc(4);
  payloadLength.writeUInt32BE(data.length);
  data = Buffer.concat([payloadLength, data]);

  if (!nonce) { nonce = generateNonce(); }
  const nonceLength = Buffer.alloc(4);
  nonceLength.writeUInt32BE(nonce.length);
  data = Buffer.concat([data, nonceLength, nonce]);

  if (signingKey) {
    signature = signMessage(hashMessage(data), signingKey);
    const signatureLength = Buffer.alloc(4);
    signatureLength.writeUInt32BE(signature.length);
    data = Buffer.concat([signatureLength, signature, data]);
  } else {
    const hmac = generateHMAC(data, key);
    const hmacLength = Buffer.alloc(4);
    hmacLength.writeUInt32BE(hmac.length);
    data = Buffer.concat([hmacLength, hmac, data]);
  }

  const { iv, ciphertext } = encryptMessage(data, key);
  return { iv, ciphertext, signature, nonce };
}

export type UnprotectProps = {
  key: crypto.KeyObject,
  verifyingKey?: crypto.KeyObject
}

// Unprotect data
export const unprotect = (data: Buffer, props: UnprotectProps) => {
  let { key, verifyingKey } = props;

  const iv = data.subarray(0, 16);
  const ciphertext = data.subarray(16);

  let message = decryptMessage(ciphertext, key, iv);

  const payloadLength = message.readUInt32BE(0);
  const payload = message.subarray(4, 4 + payloadLength);
  message = message.subarray(4 + payloadLength);

  const nonceLength = message.readUInt32BE(0);
  const nonce = message.subarray(4, 4 + nonceLength);
  message = message.subarray(4 + nonceLength);


  if (verifyingKey) {
    const signatureLength = message.readUInt32BE(0);
    const signature = message.subarray(4, signatureLength + 4);

    const payloadHash = hashMessage(Buffer.concat([payload, nonce]));
    if (!verifySignature(payloadHash, signature, verifyingKey)) {
      throw new Error('Invalid signature');
    }
  } else {
    const hmacLength = message.readUInt32BE(0);
    const hmac = message.subarray(4, hmacLength + 4);

    const hmac2 = generateHMAC(Buffer.concat([payload, nonce]), key);
    if (!hmac.equals(hmac2)) {
      throw new Error('Invalid HMAC');
    }
  }

  return { payload, nonce };
}
