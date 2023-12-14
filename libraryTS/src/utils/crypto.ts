let webcrypto: Crypto;
if (window) { webcrypto = window.crypto; }
else { webcrypto = require('crypto').webcrypto; }
const crypto = webcrypto.subtle;

export const createAESKey = async (length?: 256) => {
  const key = await crypto.generateKey(
    { name: "AES-CBC", length: length || 256 },
    true,
    ['encrypt', 'decrypt']
  );
  return key;
};

// Create ECDSA key pair to sign and verify
export const createECDSAKey = async (namedCurve?: 'P-521') => {
  const {publicKey, privateKey} = await crypto.generateKey(
    { name: "ECDSA", namedCurve: namedCurve || 'P-521' },
    true,
    ['sign', 'verify']
  );
  return {publicKey, privateKey};
}

// Create HMAC key to sign and verify
export const createHMACKey = async () => {
  const key = await crypto.generateKey(
    { name: "HMAC", hash: "SHA-256"},
    true,
    ['sign', 'verify']
  );
  return key;
};

// Sign a message with a private key
export const signMessage = async (message: ArrayBuffer, privateKey: CryptoKey) => {
  const signature = await crypto.sign(
    { name: "ECDSA", hash: "SHA-256" },
    privateKey,
    message
  );
  return signature;
}

// Verify a message with a public key
export const verifySignature = async (message: ArrayBuffer, signature: ArrayBuffer, publicKey: CryptoKey) => {
  const verified = await crypto.verify(
    { name: "ECDSA", hash: "SHA-256" },
    publicKey,
    signature,
    message
  );
  return verified;
}

// Generate HMAC
export const generateHMAC = async (message: ArrayBuffer, key: CryptoKey) => {
  const hmac = await crypto.sign(
    { name: "HMAC" },
    key,
    message
  );
  return hmac;
}

// Verify HMAC
export const verifyHMAC = async (message: ArrayBuffer, hmac: ArrayBuffer, key: CryptoKey) => {
  const verified = await crypto.verify(
    { name: "HMAC" },
    key,
    hmac,
    message
  );
  return verified;
}

// Encrypt a message with a key
export const encryptMessage = async (message: ArrayBuffer, key: CryptoKey) => {
  const iv = webcrypto.getRandomValues(new Uint8Array(16));
  const ciphertext = await crypto.encrypt(
    { name: "AES-CBC", iv },
    key,
    message
  );
  return {iv, ciphertext: new Uint8Array(ciphertext)};
}

// Decrypt a message with a key
export const decryptMessage = async (ciphertext: ArrayBuffer, key: CryptoKey, iv: ArrayBuffer) => {
  const plaintext = await crypto.decrypt(
    { name: "AES-CBC", iv },
    key,
    ciphertext
  );
  return new Uint8Array(plaintext);
}

export const generateNonce = async () => {
  const nonce = new BigUint64Array([BigInt(Date.now())]);
  return nonce
}

function concatBuffers(...buffers: ArrayBuffer[]) {
  const totalLength = buffers.reduce((acc, buffer) => acc + buffer.byteLength, 0);
  const result = new Uint8Array(totalLength);
  let offset = 0;
  for (const buffer of buffers) {
    result.set(new Uint8Array(buffer), offset);
    offset += buffer.byteLength;
  }
  return result.buffer;
}

export type ProtectProps = {
  aesKey: CryptoKey;
  hmacKey?: CryptoKey;
  signingKey?: CryptoKey;
  nonce?: ArrayBuffer;
}

// Protect data
// Structure of the protected data:
// [payloadLength: 4 bytes][payload: payloadLength bytes][nonceLength: 4 bytes][nonce: nonceLength bytes][signatureLength: 4 bytes][signature: signatureLength bytes]
export const protect = async (data: ArrayBuffer, props: ProtectProps) => {
  let { aesKey, hmacKey, signingKey, nonce } = props;
  let signature: ArrayBuffer | null = null;

  const payloadLength = new Uint32Array([data.byteLength]).buffer;

  if (!nonce) { nonce = await generateNonce(); }
  const nonceLength = new Uint32Array([nonce.byteLength]).buffer;

  let message = concatBuffers(data, nonce);

  if (signingKey) {
    signature = await signMessage(message, signingKey);
    const signatureLength = new Uint32Array([signature.byteLength]).buffer;
    data = concatBuffers(payloadLength, data, nonceLength, nonce, signatureLength, signature);
  } else if (hmacKey) {
    const hmac = await generateHMAC(message, hmacKey);
    const hmacLength = new Uint32Array([hmac.byteLength]).buffer;
    data = concatBuffers(payloadLength, data, nonceLength, nonce, hmacLength, hmac);
  }

  const { iv, ciphertext } = await encryptMessage(data, aesKey);
  return { iv, ciphertext, signature, nonce };
}

export type UnprotectProps = {
  iv?: ArrayBuffer;
  aesKey: CryptoKey;
  hmacKey?: CryptoKey;
  verifyingKey?: CryptoKey;
}

// Unprotect data
export const unprotect = async (data: ArrayBuffer, props: UnprotectProps) => {
  let { iv, aesKey, hmacKey, verifyingKey } = props;

  if (!iv) {
    iv = data.slice(0, 16);
    data = data.slice(16);
  }

  let message = await decryptMessage(data, aesKey, iv);

  const payloadLength = new Uint32Array(message.slice(0, 4))[0];
  const payload = message.slice(4, 4 + payloadLength);
  message = message.slice(4 + payloadLength);

  const nonceLength = new Uint32Array(message.slice(0, 4))[0];
  const nonce = message.slice(4, 4 + nonceLength);
  message = message.slice(4 + nonceLength);


  if (verifyingKey) {
    const signatureLength = new Uint32Array(message.slice(0, 4))[0];
    const signature = message.slice(4, 4 + signatureLength);

    if (!await verifySignature(message, signature, verifyingKey)) {
      throw new Error('Invalid signature');
    }
  } else if (hmacKey) {
    const hmacLength = new Uint32Array(message.slice(0, 4))[0];
    const hmac = message.slice(4, 4 + hmacLength);

    if (!await verifyHMAC(message, hmac, hmacKey)) {
      throw new Error('Invalid HMAC');
    }
  }

  return {
    payload: new Uint8Array(payload),
    nonce: new Uint8Array(nonce)
  };
}

export const check = async (data: ArrayBuffer, props: UnprotectProps, nonceCheck: (nonce: ArrayBuffer) => boolean) => {
  try {
    const { nonce } = await unprotect(data, props);
    return nonceCheck(nonce);
  } catch (e) {
    return false;
  }
}
