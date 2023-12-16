import { webcrypto } from 'crypto';

const keyCrypto = webcrypto.subtle;

export const stringToAESKey = async (aes: string) => {
  const data = Buffer.from(aes, 'base64');
  try {
    return await keyCrypto.importKey('raw', data, 'AES-CBC', true, [
      'encrypt',
      'decrypt',
    ]);
  } catch (error: any) {
    console.log('Could not import key: ' + error.message);
    throw error;
  }
};

export const AESKeyToString = (aes: CryptoKey) =>
  AESKeytoBuffer(aes).then((buffer) => buffer.toString('base64'));

export const AESKeytoBuffer = async (aes: CryptoKey) => {
  const data = await keyCrypto.exportKey('raw', aes);
  return Buffer.from(data);
};

export const bufferToAESKey = async (buffer: Buffer) => {
  return await keyCrypto.importKey('raw', buffer, 'AES-CBC', true, [
    'encrypt',
    'decrypt',
  ]);
};

export const stringToBuffer = async (string: string) => {
  return Buffer.from(string, 'base64');
};

export const bufferToString = async (buffer: Buffer) => {
  return buffer.toString('base64');
};
