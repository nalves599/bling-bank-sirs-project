import { Request, Response } from 'express';

import * as AccountService from '../services/AccountService';

export const getAccountsByUser = async (req: Request, res: Response) => {
  const { email } = req.params;

  try {
    const accounts = await AccountService.getAccountsByUser(email);

    res.json({ accounts });
  } catch (error) {
    console.error(error);
    res.status(400).json({ message: 'Could not get accounts' });
  }
};
