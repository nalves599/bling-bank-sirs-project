import {
  uniqueNamesGenerator,
  animals,
  colors,
  names,
} from "unique-names-generator";
import { split, combine } from "shamirs-secret-sharing-ts";

import { crypto } from "blingbank-lib";

const dictionaries = [animals, colors, names];

export const generateSharedSecret = async () => {
  const friendlySharedSecret = uniqueNamesGenerator({
    separator: "-",
    dictionaries,
  });
  const sharedSecret = await crypto.sha256(friendlySharedSecret);

  return { friendlySharedSecret, sharedSecret };
};

export const generateShamirSecrets = async (
  shares: number,
  threshold: number,
) => {
  const key = Buffer.from(await crypto.generateKey());
  const secrets = split(key, { shares, threshold });
  return { key, secrets };
};

export const combineShamirSecrets = async (secrets: string[]) => {
  const keys = secrets.map((secret) => Buffer.from(secret, "base64"));
  const secret = combine(keys);
  return secret;
};
