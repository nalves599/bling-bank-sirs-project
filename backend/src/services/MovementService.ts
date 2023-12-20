import db from '../database';

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
