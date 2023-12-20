import db from "../database";
import { crypto } from "blingbank-lib";
import * as SecurityService from "./SecurityService";
import * as KeyUtil from "../utils/KeyUtil";

export const accountSecrets = new Map<string, ArrayBuffer>(); // <accountId, accountSecret>

export type CreateAccount = {
  name: string;
  balance: number;
  currency: string;
  accountHolders: string[];
};

export const createAccount = async (
  account: CreateAccount,
  key: ArrayBuffer,
) => {
  const encryptedAccount = {
    balance: String(
      await crypto.paramProtect(crypto.toBytesInt32(account.balance), key),
    ),
  };

  const createdAccount = await db.account.create({
    data: {
      name: account.name,
      balance: encryptedAccount.balance,
      currency: account.currency,
      accountHolders: {
        connect: account.accountHolders.map((id) => ({ id })),
      },
    },
  });

  return createdAccount;
};

export const getAccount = async (accountId: string, key?: ArrayBuffer) => {
  const encryptedAccount = await db.account.findUnique({
    where: {
      id: accountId,
    },
    include: {
      accountHolders: true,
    },
  });

  if (!encryptedAccount) {
    return null;
  }

  const accountKey = key || accountSecrets.get(accountId);
  if (!accountKey) {
    throw new Error("Account secret not found");
  }

  const account = {
    ...encryptedAccount,
    balance: crypto.fromBytesInt32(
      await crypto.paramUnprotect(encryptedAccount.balance, accountKey),
    ),
  };

  return account;
};

export const uprotectAccount = async (accountId: string, userKey: string) => {
  if (accountSecrets.has(accountId)) {
    return null;
  }

  const serverKey = await SecurityService.getShamirSecret(accountId);
  if (!serverKey) {
    throw new Error("Shared secret not found");
  }

  const accountKey = KeyUtil.combineShamirSecrets([serverKey, userKey]);
};
