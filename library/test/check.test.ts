import { describe, expect, test } from "@jest/globals";
import {
  ProtectProps,
  UnprotectProps,
  check,
  createAESKey,
  protect,
} from "../src/utils/crypto";
import { TextEncoder, TextDecoder } from "util";

describe("check module", () => {
  test("check file sucessfully", async () => {
    const aesKey = await createAESKey();
    const props: ProtectProps = {
      aesKey: aesKey,
      hmacKey: undefined,
      signingKey: undefined,
      nonce: undefined,
    };

    const file = "Hello World";
    const arrayBuf = new TextEncoder().encode(file).buffer;
    const { iv, messageEncrypted } = await protect(arrayBuf, props);

    const nonceCheck = function (nonce: ArrayBuffer | undefined) {
      if (nonce === undefined) {
        return false;
      }

      const ts = parseInt(new TextDecoder().decode(nonce));
      const now = new Date().getTime();

      return ts > now - 3000; // 3 seconds of tolerance
    };

    const unprotectProps: UnprotectProps = {
      iv: iv,
      aesKey: aesKey,
      hmacKey: undefined,
      verifyingKey: undefined,
      nonceVerification: nonceCheck,
    };

    await new Promise((resolve) => setTimeout(resolve, 1000));

    const valid = await check(messageEncrypted, unprotectProps);

    expect(valid).toBe(true);
  });

  test("check file unsuccessfully", async () => {
    const aesKey = await createAESKey();
    const props: ProtectProps = {
      aesKey: aesKey,
      hmacKey: undefined,
      signingKey: undefined,
      nonce: undefined,
    };

    const file = "Hello World";
    const arrayBuf = new TextEncoder().encode(file).buffer;
    const { messageEncrypted } = await protect(arrayBuf, props);

    const nonceCheck = function (nonce: ArrayBuffer | undefined) {
      if (nonce === undefined) {
        return false;
      }

      const ts = parseInt(new TextDecoder().decode(nonce));
      const now = new Date().getTime();

      return ts > now - 500; // 0.5 seconds of tolerance
    };

    const unprotectProps: UnprotectProps = {
      aesKey: aesKey,
      hmacKey: undefined,
      verifyingKey: undefined,
      nonceVerification: nonceCheck,
    };

    await new Promise((resolve) => setTimeout(resolve, 1000));

    const valid = await check(messageEncrypted, unprotectProps);

    expect(valid).toBe(false);
  });
});
