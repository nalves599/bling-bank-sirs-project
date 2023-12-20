import { describe, expect, test } from "@jest/globals";
import {
  ProtectProps,
  createAESKey,
  createECDSAKey,
  createHMACKey,
  protect,
  toBytesInt64,
} from "../src/crypto";
import { TextEncoder } from "util";

describe("protect module", () => {
  test("protect file", async () => {
    const aesKey = await createAESKey();
    const props: ProtectProps = {
      aesKey: aesKey,
      hmacKey: undefined,
      signingKey: undefined,
      nonce: undefined,
    };

    const file = "Hello World";
    const arrayBuf = new TextEncoder().encode(file).buffer;
    const { iv, messageEncrypted, signature, hmac, nonce } = await protect(
      arrayBuf,
      props,
    );

    expect(iv).toBeDefined();
    expect(messageEncrypted).toBeDefined();
    expect(signature).toBeNull();
    expect(hmac).toBeNull();
    expect(nonce).toBeDefined();
  });

  test("protect file with HMAC", async () => {
    const aesKey = await createAESKey();
    const hmacKey = await createHMACKey();
    const props: ProtectProps = {
      aesKey: aesKey,
      hmacKey: hmacKey,
      signingKey: undefined,
      nonce: undefined,
    };

    const file = "Hello World";
    const arrayBuf = new TextEncoder().encode(file).buffer;
    const { iv, messageEncrypted, signature, hmac, nonce } = await protect(
      arrayBuf,
      props,
    );

    expect(iv).toBeDefined();
    expect(messageEncrypted).toBeDefined();
    expect(signature).toBeNull();
    expect(hmac).toBeDefined();
    expect(nonce).toBeDefined();
  });

  test("protect file with signature", async () => {
    const aesKey = await createAESKey();
    const signingKey = await createECDSAKey();
    const props: ProtectProps = {
      aesKey: aesKey,
      hmacKey: undefined,
      signingKey: signingKey.privateKey,
      nonce: undefined,
    };

    const file = "Hello World";
    const arrayBuf = new TextEncoder().encode(file).buffer;
    const { iv, messageEncrypted, signature, hmac, nonce } = await protect(
      arrayBuf,
      props,
    );

    expect(iv).toBeDefined();
    expect(messageEncrypted).toBeDefined();
    expect(signature).toBeDefined();
    expect(hmac).toBeNull();
    expect(nonce).toBeDefined();
  });

  test("protect file with custom nonce", async () => {
    const aesKey = await createAESKey();
    const timestamp = toBytesInt64(BigInt(Date.now()));
    const props: ProtectProps = {
      aesKey: aesKey,
      hmacKey: undefined,
      signingKey: undefined,
      nonce: timestamp,
    };

    const file = "Hello World";
    const arrayBuf = new TextEncoder().encode(file).buffer;
    const { iv, messageEncrypted, signature, hmac, nonce } = await protect(
      arrayBuf,
      props,
    );

    expect(iv).toBeDefined();
    expect(messageEncrypted).toBeDefined();
    expect(signature).toBeNull();
    expect(hmac).toBeNull();
    expect(nonce).toBeDefined();
  });
});
