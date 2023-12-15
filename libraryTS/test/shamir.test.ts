import { describe, expect, test } from "@jest/globals";
import { split, combine } from "shamirs-secret-sharing-ts";

describe("shamir module", () => {
  test("threshold reached", async () => {
    const secret = Buffer.from("secret key");
    const shares = split(secret, { shares: 10, threshold: 4 });
    const recovered = combine(shares.slice(3, 7));
    expect(recovered.toString()).toBe(secret.toString());
  });

  test("threshold not reached", async () => {
    const secret = Buffer.from("secret key");
    const shares = split(secret, { shares: 10, threshold: 8 });
    const recovered = combine(shares.slice(3, 7));
    expect(recovered.toString()).not.toBe(secret.toString());
  });
});
