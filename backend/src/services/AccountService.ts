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

export const getAccountById = async (accountId: string, accountKey: ArrayBuffer) => {
  const encryptedAccount = await db.account.findUnique({
    where: {
      id: accountId,
    },
    include: {
      accountHolders: true,
    },
  });

  if (!encryptedAccount) { return null; }

  const balance = encryptedAccount.balance;
  const decryptedBalance = await crypto.paramUnprotect(balance, accountKey);
  const intBalance = crypto.fromBytesInt32( new Uint8Array(decryptedBalance).buffer );

  const account = {
    ...encryptedAccount,
    balance: intBalance,
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

export const getAccountsByHolder = async (email: string) => {

  const accounts = await db.account.findMany({
    where: {
      accountHolders: {
        some: {
          email: email,
        },
      },
    },
  });

  return accounts;
}


export const getAccountMovements = async (accountId: string) => {
  const movements = await db.movement.findMany({
    where: {
      accountId: accountId,
    },
  });

  return movements;
};

export const getAccountPayments = async (accountId: string) => {
  const payments = await db.payment.findMany({
    where: {
      accountId: accountId,
    },
  });

  return payments;
};

export const getPaymentById = async (paymentId: string) => {
  const payment = await db.payment.findUnique({
    where: {
      id: paymentId,
    },
  });

  return payment;
}