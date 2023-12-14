import { describe, expect, test } from "@jest/globals";
import {
  ProtectProps,
  UnprotectProps,
  unprotect,
  createAESKey,
  protect,
} from "../src/utils/crypto";
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
    const { iv, ciphertext } = await protect(arrayBuf, props);

    const props2: UnprotectProps = {
      iv: iv,
      aesKey: aesKey,
      hmacKey: undefined,
      verifyingKey: undefined,
    };
    const { payload } = await unprotect(ciphertext, props2);

    expect(word).toEqual(new TextDecoder().decode(payload));
  });
});
