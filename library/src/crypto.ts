let webcrypto: Crypto;
let UTF8Encoder: TextEncoder;
let UTF8Decoder: TextDecoder;
if (require("is-node")) {
  //console.log("Using Node.js WebCrypto");
  webcrypto = require("crypto").webcrypto;
  UTF8Encoder = new (require("util").TextEncoder)();
  UTF8Decoder = new (require("util").TextDecoder)("utf-8");
} else {
  //console.log("Using WebCrypto API");
  webcrypto = window.crypto;
  UTF8Encoder = new TextEncoder();
  UTF8Decoder = new TextDecoder("utf-8");
}
const crypto = webcrypto.subtle;

export const encoder = UTF8Encoder;
export const decoder = UTF8Decoder;

export const generateKey = async (length?: number) => {
  const key = new Uint8Array(length || 32);
  webcrypto.getRandomValues(key);
  return key;
};

export const createAESKey = async (length?: 256) => {
  const key = await crypto.generateKey(
    { name: "AES-CBC", length: length ?? 256 },
    true,
    ["encrypt", "decrypt"],
  );
  return key;
};

// Import AES key from raw bytes
export const importAESKey = async (key: string | ArrayBuffer) => {
  const keyBuffer = typeof key === "string" ? hexToBuffer(key) : key;
  const importedKey = await crypto.importKey(
    "raw",
    keyBuffer,
    { name: "AES-CBC" },
    true,
    ["encrypt", "decrypt"],
  );
  return importedKey;
};

// Create ECDSA key pair to sign and verify
export const createECDSAKey = async (namedCurve?: "P-521") => {
  const { publicKey, privateKey } = await crypto.generateKey(
    { name: "ECDSA", namedCurve: namedCurve ?? "P-521" },
    true,
    ["sign", "verify"],
  );
  return { publicKey, privateKey };
};

// Import ECDSA key from raw bytes
export const importECDSAKey = async (key: string | ArrayBuffer) => {
  const keyBuffer = typeof key === "string" ? hexToBuffer(key) : key;
  const importedKey = await crypto.importKey(
    "raw",
    keyBuffer,
    { name: "ECDSA", namedCurve: "P-256" },
    true,
    ["sign", "verify"],
  );
  return importedKey;
};

// Create HMAC key to sign and verify
export const createHMACKey = async () => {
  const key = await crypto.generateKey(
    { name: "HMAC", hash: "SHA-256" },
    true,
    ["sign", "verify"],
  );
  return key;
};

// Import HMAC key
export const importHMACKey = async (key: string | ArrayBuffer) => {
  const keyBuffer = typeof key === "string" ? hexToBuffer(key) : key;
  const importedKey = await crypto.importKey(
    "raw",
    keyBuffer,
    { name: "HMAC", hash: "SHA-256" },
    true,
    ["sign", "verify"],
  );
  return importedKey;
};

// Hash a message with SHA-256
export const sha256 = async (message: string | ArrayBuffer) => {
  const messageBuffer =
    typeof message === "string" ? UTF8Encoder.encode(message) : message;
  const hashBuffer = await crypto.digest("SHA-256", messageBuffer);
  return hashBuffer;
};

// Sign a message with a private key
export const signMessage = async (
  message: ArrayBuffer,
  privateKey: CryptoKey,
) => {
  const signature = await crypto.sign(
    { name: "ECDSA", hash: "SHA-256" },
    privateKey,
    message,
  );
  return signature;
};

// Verify a message with a public key
export const verifySignature = async (
  message: ArrayBuffer,
  signature: ArrayBuffer,
  publicKey: CryptoKey,
) => {
  const verified = await crypto.verify(
    { name: "ECDSA", hash: "SHA-256" },
    publicKey,
    signature,
    message,
  );
  return verified;
};

// Generate HMAC
export const generateHMAC = async (message: ArrayBuffer, key: CryptoKey) => {
  const hmac = await crypto.sign({ name: "HMAC" }, key, message);
  return hmac;
};

// Verify HMAC
export const verifyHMAC = async (
  message: ArrayBuffer,
  hmac: ArrayBuffer,
  key: CryptoKey,
) => {
  const verified = await crypto.verify({ name: "HMAC" }, key, hmac, message);
  return verified; // true if HMAC is valid
};

// Encrypt a message with a key
export const encryptMessage = async (
  message: string | ArrayBuffer,
  key: ArrayBuffer | CryptoKey | string,
) => {
  message = typeof message === "string" ? UTF8Encoder.encode(message) : message;
  key = typeof key === "string" ? hexToBuffer(key) : key;
  if (key instanceof ArrayBuffer || key instanceof Uint8Array) {
    key = await importAESKey(key);
  }
  const iv = webcrypto.getRandomValues(new Uint8Array(16));
  const ciphertext = await crypto.encrypt(
    { name: "AES-CBC", iv },
    key,
    message,
  );
  return { iv, encrypted: new Uint8Array(ciphertext) };
};

// Decrypt a message with a key
export const decryptMessage = async (
  ciphertext: ArrayBuffer,
  key: string | ArrayBuffer | CryptoKey,
  iv: ArrayBuffer,
) => {
  key = typeof key === "string" ? hexToBuffer(key) : key;
  if (key instanceof ArrayBuffer || key instanceof Uint8Array) {
    key = await importAESKey(key);
  }
  const plaintext = await crypto.decrypt(
    { name: "AES-CBC", iv },
    key,
    ciphertext,
  );
  return new Uint8Array(plaintext);
};

// Generate nonce
export const generateNonce = async () => {
  const timestamp = Date.now().toString();
  const nonce = UTF8Encoder.encode(timestamp);
  return nonce;
};

function concatBuffers(...buffers: ArrayBuffer[]) {
  const totalLength = buffers.reduce(
    (acc, buffer) => acc + buffer.byteLength,
    0,
  );
  const result = new Uint8Array(totalLength);
  let offset = 0;
  for (const buffer of buffers) {
    result.set(new Uint8Array(buffer), offset);
    offset += buffer.byteLength;
  }
  return result;
}

export type ProtectProps = {
  aesKey: string | ArrayBuffer | CryptoKey;
  hmacKey?: ArrayBuffer | CryptoKey;
  signingKey?: CryptoKey;
  nonce?: ArrayBuffer;
  hex?: boolean;
};

// Protect data
// Structure of the protected data:
// [payloadLength: 4 bytes][payload: payloadLength bytes][nonceLength: 4 bytes][nonce: nonceLength bytes][signatureLength: 4 bytes][signature: signatureLength bytes]
export const protect = async (
  data: string | ArrayBuffer,
  props: ProtectProps,
) => {
  let { aesKey, hmacKey, signingKey, nonce, hex } = props;
  let signature: ArrayBuffer | null = null;
  let hmac: ArrayBuffer | null = null;

  data = typeof data === "string" ? UTF8Encoder.encode(data) : data;

  const payloadLength = toBytesInt32(data.byteLength);

  if (!nonce) nonce = await generateNonce();
  const nonceLength = toBytesInt32(nonce.byteLength);

  let message = concatBuffers(data, nonce);

  if (signingKey) {
    signature = await signMessage(message, signingKey);
    const signatureLength = toBytesInt32(signature.byteLength);
    data = concatBuffers(
      payloadLength,
      data,
      nonceLength,
      nonce,
      signatureLength,
      signature,
    );
  } else if (hmacKey) {
    if (hmacKey instanceof ArrayBuffer || hmacKey instanceof Uint8Array) {
      hmacKey = await importHMACKey(hmacKey);
    }
    hmac = await generateHMAC(message, hmacKey);
    const hmacLength = toBytesInt32(hmac.byteLength);
    data = concatBuffers(
      payloadLength,
      data,
      nonceLength,
      nonce,
      hmacLength,
      hmac,
    );
  } else {
    data = concatBuffers(payloadLength, data, nonceLength, nonce);
  }

  const { iv, encrypted } = await encryptMessage(data, aesKey);
  const messageEncrypted = concatBuffers(iv, encrypted);
  const messageEncryptedHex = bufferToHex(messageEncrypted);

  return {
    iv,
    messageEncrypted: hex ? messageEncryptedHex : messageEncrypted,
    signature,
    hmac,
    nonce,
  };
};

export type UnprotectProps = {
  iv?: ArrayBuffer;
  aesKey: string | ArrayBuffer | CryptoKey;
  hmacKey?: ArrayBuffer | CryptoKey;
  verifyingKey?: CryptoKey;
  nonceVerification?: (nonce: ArrayBuffer) => boolean;
};

// Unprotect data
export const unprotect = async (
  data: string | ArrayBuffer,
  props: UnprotectProps,
) => {
  let { iv, aesKey, hmacKey, verifyingKey, nonceVerification } = props;

  data = typeof data === "string" ? hexToBuffer(data) : data;

  if (!iv) {
    iv = data.slice(0, 16);
  }
  data = data.slice(16);

  let message = await decryptMessage(data, aesKey, iv);

  const payloadLength = fromBytesInt32(message.slice(0, 4).buffer);
  const payload = message.slice(4, 4 + payloadLength);
  message = message.slice(4 + payloadLength);

  const nonceLength = fromBytesInt32(message.slice(0, 4).buffer);
  const nonce = message.slice(4, 4 + nonceLength);
  message = message.slice(4 + nonceLength);

  if (verifyingKey) {
    const signatureLength = fromBytesInt32(message.slice(0, 4).buffer);
    const signature = message.slice(4, 4 + signatureLength);
    const verify = concatBuffers(payload, nonce);

    if (!(await verifySignature(verify, signature, verifyingKey))) {
      throw new Error("Invalid signature");
    }
  } else if (hmacKey) {
    const hmacLength = fromBytesInt32(message.slice(0, 4).buffer);
    const hmac = message.slice(4, 4 + hmacLength);
    const verify = concatBuffers(payload, nonce);

    if (hmacKey instanceof ArrayBuffer || hmacKey instanceof Uint8Array) {
      hmacKey = await importHMACKey(hmacKey);
    }
    if (!(await verifyHMAC(verify, hmac, hmacKey))) {
      throw new Error("Invalid HMAC");
    }
  }

  if (nonceVerification)
    if (!nonceVerification(nonce.buffer)) throw new Error("Invalid nonce");

  return {
    payload: new Uint8Array(payload),
    nonce: new Uint8Array(nonce),
  };
};

export const paramProtect = async (
  data: string | ArrayBuffer,
  key: ArrayBuffer,
) => {
  data = typeof data === "string" ? UTF8Encoder.encode(data) : data;
  const { messageEncrypted } = await protect(data, {
    aesKey: key,
    hmacKey: key,
    hex: true,
  });
  return messageEncrypted;
};

export const paramUnprotect = async (
  data: string | ArrayBuffer,
  key: ArrayBuffer,
  nonceVerification?: (nonce: ArrayBuffer) => boolean,
) => {
  data = typeof data === "string" ? hexToBuffer(data) : data;
  const { payload } = await unprotect(data, {
    aesKey: key,
    hmacKey: key,
    nonceVerification,
  });
  return payload;
};

export const check = async (data: ArrayBuffer, props: UnprotectProps) => {
  try {
    await unprotect(data, props);
    return true;
  } catch (e) {
    return false;
  }
};

export const hexToBuffer = (hex: string) => {
  return new Uint8Array(hex.match(/.{1,2}/g)!.map((byte) => parseInt(byte, 16)))
    .buffer;
};

export const bufferToHex = (buffer: ArrayBuffer) => {
  return Array.prototype.map
    .call(new Uint8Array(buffer), (x) => ("00" + x.toString(16)).slice(-2))
    .join("");
};

export function toBytesInt32(num: number) {
  const arr = new ArrayBuffer(4); // an Int32 takes 4 bytes
  const view = new DataView(arr);
  view.setUint32(0, num, false); // byteOffset = 0; litteEndian = false
  return arr;
}

export function fromBytesInt32(buffer: ArrayBuffer) {
  const view = new DataView(buffer);
  return view.getInt32(0, false); // byteOffset = 0; litteEndian = false
}

export function fromBytesInt64(buffer: ArrayBuffer) {
  const view = new DataView(buffer);
  return view.getBigInt64(0, false); // byteOffset = 0; litteEndian = false
}

export function toBytesInt64(num: bigint) {
  const arr = new ArrayBuffer(8); // an Int64 takes 8 bytes
  const view = new DataView(arr);
  view.setBigInt64(0, num, false); // byteOffset = 0; litteEndian = false
  return arr;
}

export const generatePOWChallenge = () => {
  const challenge = (Math.random() * 1000 + 100).toFixed(0); // TODO: Improve challenge
  return String(challenge);
};

export const solvePOWChallenge = (challenge: string) => {
  const solution = parseInt(challenge) * 39; // TODO: Improve challenge
  return String(solution);
};

export const nonceCheck = (
  lastNonce: ArrayBuffer | undefined,
  nonceInterval?: number,
) => {
  return (nonce: ArrayBuffer) => {
    const currentDate = new Date();
    const nonceDate = new Date(fromBytesInt32(nonce));

    if (
      Math.abs(currentDate.getTime() - nonceDate.getTime()) >
      (nonceInterval || 1000 * 60)
    ) {
      return false;
    }

    if (lastNonce) {
      const lastNonceDate = new Date(fromBytesInt32(lastNonce));
      if (nonceDate.getTime() <= lastNonceDate.getTime()) {
        return false;
      }
    }

    return true;
  };
};
