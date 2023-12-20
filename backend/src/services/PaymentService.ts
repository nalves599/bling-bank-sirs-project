import db from '../database';

export type CreatePayment = {
  value: string;
  accountId: string;
  description: string;
  totp: string;
  hash: string;
}

export const createPayment = async (payment: CreatePayment) => {
  const createdPayment = await db.payment.create({
    data: {
      value: payment.value,
      description: payment.description,
      accountId: payment.accountId,
      totp: payment.totp,
      hash: payment.hash,
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

export const getPaymentsByAccount = async (accountId: string) => {
  const payments = await db.payment.findMany({
    where: {
      accountId: accountId,
    },
  });

  return payments;
}
