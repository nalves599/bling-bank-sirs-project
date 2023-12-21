import db from '../database';

import { crypto } from 'blingbank-lib';

export type CreateMovement = {
  date: string;
  value: string;
  description: string;
  paymentID?: string;
}

export const createMovement = async (accountId: string, movement: CreateMovement) => {
  const createdMovement = await db.movement.create({
    data: {
      date: movement.date,
      value: movement.value,
      description: movement.description,
      accountId: accountId,
      paymentId: movement.paymentID,
    },
  });

  return createdMovement;
};

export const getMovementsByAccountId = async (accountId: string, key: ArrayBuffer) => {
  const movements = await db.movement.findMany({
    where: {
      accountId: accountId,
    },
  });

  const decryptedMovements = await Promise.all(movements.map(async (movement) => {
    return {
      date: await crypto.paramUnprotect(movement.date, key),
      value: crypto.fromBytesInt32(await crypto.paramUnprotect(movement.value, key)),
      description: crypto.decoder.decode(await crypto.paramUnprotect(movement.description, key)),
      paymentID: movement.paymentId,
    };
  }));

  return decryptedMovements;
}
