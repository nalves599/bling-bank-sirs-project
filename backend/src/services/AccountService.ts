import db from '../database';

export const getAccountsByUser = async (email: string) => {
  const user = await db.user.findUnique({
    where: {
      email,
    },
  });

  if (!user) {
    throw new Error('User not found');
  }

  const accounts = await db.account.findMany({
    where: {
      id: {
        in: user.accountsIDs,
      },
    },
  });

  return accounts;
};
