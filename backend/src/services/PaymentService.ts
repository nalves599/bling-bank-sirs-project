import db from '../database';

import * as SecurityService from './SecurityService';

import { crypto } from 'blingbank-lib';

export type CreatePayment = {
  value: number;
  description: string;
  totp: string;
}

export const createPayment = async (accountId: string, payment: CreatePayment, key: ArrayBuffer) => {
  const hash = await crypto.sha256(payment.toString());
  const encryptedPayment = {
    value: String(await crypto.paramProtect(crypto.toBytesInt32(payment.value), key)),
    description: String(await crypto.paramProtect(payment.description, key)),
    totp: String(await crypto.paramProtect(crypto.encoder.encode(payment.totp), key)),
    hash: String(await crypto.paramProtect(hash, key)),
  };

  const createdPayment = await db.payment.create({
    data: {
      value: encryptedPayment.value,
      description: encryptedPayment.description,
      accountId,
      totp: encryptedPayment.totp,
      hash: encryptedPayment.hash,
    },
  });

  return createdPayment;
};

export const getPayment = async (paymentId: string) => {
  const payment = await db.payment.findUnique({
    where: {
      id: paymentId,
    },
  });

  return payment;
}

export const getPaymentsByAccountId = async (accountId: string, key: ArrayBuffer) => {
  const payments = await db.payment.findMany({
    where: {
      accountId: accountId,
    },
    include: {
      signatures: true,
    },
  });

  const decryptedPayments = await Promise.all(payments.map(async (payment) => {
    return {
      value: crypto.fromBytesInt32(await crypto.paramUnprotect(payment.value, key)),
      description: crypto.decoder.decode(await crypto.paramUnprotect(payment.description, key)),
      totp: crypto.decoder.decode(await crypto.paramUnprotect(payment.totp, key)),
      hash: payment.hash,
      signatures: await Promise.all(payment.signatures.map(async (signature) => ({
        date: await crypto.paramUnprotect(signature.date, key),
        content: crypto.decoder.decode(await crypto.paramUnprotect(signature.content, key)),
        signKeyId: signature.signKeyId,
      }))),
    };
  }));

  return decryptedPayments;
}
