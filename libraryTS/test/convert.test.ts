import { describe, expect, test } from "@jest/globals";
import { toBytesInt64, fromBytesInt64 } from "../src/utils/crypto";

describe("convert module", () => {
  test("Test simple int", () => {
    const number: bigint = BigInt(1);
    const buffer = toBytesInt64(number);

    const output = fromBytesInt64(buffer);
    expect(output).toEqual(number);
  });

  test("Test negative int", () => {
    const number: bigint = BigInt(-1);
    const buffer = toBytesInt64(number);

    const output = fromBytesInt64(buffer);
    expect(output).toEqual(number);
  });

  test("Test date int", () => {
    // current time
    const number: bigint = BigInt(Date.now());
    const buffer = toBytesInt64(number);

    const output = fromBytesInt64(buffer);
    expect(output).toEqual(number);
  });

  test("Test big int", () => {
    const number: bigint = BigInt("9223372036854775807");
    const buffer = toBytesInt64(number);

    const output = fromBytesInt64(buffer);
    expect(output).toEqual(number);
  });
});
