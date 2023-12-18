import { describe, expect, test } from "@jest/globals";
import {
  ProtectProps,
  UnprotectProps,
  unprotect,
  createAESKey,
  protect,
  createHMACKey,
  createECDSAKey,
} from "../src/crypto";
import { TextEncoder, TextDecoder } from "util";

describe("unprotect module", () => {
  test("unprotect word", async () => {
    const aesKey = await createAESKey();
    const props: ProtectProps = {
      aesKey: aesKey,
      hmacKey: undefined,
      signingKey: undefined,
      nonce: undefined,
    };

    const word = "Hello World";
    const arrayBuf = new TextEncoder().encode(word).buffer;
    const { messageEncrypted } = await protect(arrayBuf, props);

    const props2: UnprotectProps = {
      aesKey: aesKey,
      hmacKey: undefined,
      verifyingKey: undefined,
    };
    const { payload } = await unprotect(messageEncrypted, props2);

    expect(word).toEqual(new TextDecoder().decode(payload));
  });

  test("unprotect word with HMAC", async () => {
    const aesKey = await createAESKey();
    const hmacKey = await createHMACKey();
    const props: ProtectProps = {
      aesKey: aesKey,
      hmacKey: hmacKey,
      signingKey: undefined,
      nonce: undefined,
    };

    const word = "Hello World";
    const arrayBuf = new TextEncoder().encode(word).buffer;
    const { messageEncrypted } = await protect(arrayBuf, props);

    const props2: UnprotectProps = {
      aesKey: aesKey,
      hmacKey: hmacKey,
      verifyingKey: undefined,
    };
    const { payload } = await unprotect(messageEncrypted, props2);

    expect(word).toEqual(new TextDecoder().decode(payload));
  });

  test("unprotect word with signature", async () => {
    const aesKey = await createAESKey();
    const { publicKey, privateKey } = await createECDSAKey();
    const props: ProtectProps = {
      aesKey: aesKey,
      hmacKey: undefined,
      signingKey: privateKey,
      nonce: undefined,
    };

    const word = "Hello World";
    const arrayBuf = new TextEncoder().encode(word).buffer;
    const { messageEncrypted } = await protect(arrayBuf, props);

    const props2: UnprotectProps = {
      aesKey: aesKey,
      hmacKey: undefined,
      verifyingKey: publicKey,
    };
    const { payload } = await unprotect(messageEncrypted, props2);

    expect(word).toEqual(new TextDecoder().decode(payload));
  });
});
