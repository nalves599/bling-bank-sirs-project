import { Request, Response } from 'express';

import * as PaymentService from '../services/PaymentService';

export const signPayment = async (req: Request, res: Response ) => {
  const paymentId = req.params.id;
  const userId = req.authData?.userId!;
  const { signature } = req.body;

  try {
    await PaymentService.signPayment(paymentId, userId, signature);
  } catch(e) {
    res.status(400).json({
      message: "Error signing payment",
    });
  }
}

