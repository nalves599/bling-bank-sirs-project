import { describe, expect, test } from "@jest/globals";
import {
  ProtectProps,
  UnprotectProps,
  unprotect,
  createAESKey,
  protect,
} from "../src/utils/crypto";
import { TextEncoder, TextDecoder } from "util";

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
    const { iv, ciphertext, signature, nonce } = await protect(arrayBuf, props);

    expect(iv).toBeDefined();
    expect(ciphertext).toBeDefined();
    expect(signature).toBeNull();
    expect(nonce).toBeDefined();
  });
});

describe("unprotect module", () => {
  test("unprotect file", async () => {
    const aesKey = await createAESKey();
    const props: ProtectProps = {
      aesKey: aesKey,
      hmacKey: undefined,
      signingKey: undefined,
      nonce: undefined,
    };

    const file = "Hello World";
    const arrayBuf = new TextEncoder().encode(file).buffer;
    const { iv, ciphertext } = await protect(arrayBuf, props);

    const props2 : UnprotectProps = {
      iv: iv,
      aesKey: aesKey,
      hmacKey: undefined,
      verifyingKey: undefined,
    };
    const { payload } = await unprotect(ciphertext, props2);

    expect(file).toEqual(new TextDecoder().decode(payload));
  });
});
