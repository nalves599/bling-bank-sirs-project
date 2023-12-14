import { describe, expect, test } from "@jest/globals";
import { ProtectProps, createAESKey, protect } from "../src/utils/crypto";
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
    const { iv, ciphertext, signature, nonce } = await protect(arrayBuf, props);

    expect(iv).toBeDefined();
    expect(ciphertext).toBeDefined();
    expect(signature).toBeNull();
    expect(nonce).toBeDefined();
  });
});
