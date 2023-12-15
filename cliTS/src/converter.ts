import { webcrypto } from "crypto";

const crypto = webcrypto.subtle;

export async function bytesToAesKey(data: Buffer): Promise<CryptoKey>{
    try {
    return await crypto.importKey("raw", data, "AES-CBC", true, ["encrypt", "decrypt"]);
    }   
    catch (error: any) {
        console.log("Could not import key: sad " + error.message);
        throw error;
    }
}

export async function bytesToHmacKey(data: ArrayBuffer): Promise<CryptoKey>{
    try {
    return await crypto.importKey("raw", data, {name: "HMAC", hash: "SHA-256"}, true, ["sign", "verify"]);
    }   
    catch (error: any) {
        console.log("Could not import key: " + error.message);
        throw error;
    }
}

export async function bytesToSigningKey(data: Buffer): Promise<CryptoKey>{
    try {
    return await crypto.importKey("pkcs8", data, {name: "ECDSA", namedCurve: "P-521"}, true, ["sign"]);
    }   
    catch (error: any) {
        console.log("Could not import key: " + error.message);
        throw error;
    }
}

export async function bytesToVerificationKey(data: Buffer): Promise<CryptoKey>{
    try {
    return await crypto.importKey("raw", data, {name: "ECDSA", namedCurve: "P-521"}, true, ["verify"]);
    }   
    catch (error: any) {
        console.log("Could not import key: " + error.message);
        throw error;
    }
}
