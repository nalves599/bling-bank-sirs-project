import { Request, Response } from "express";

import * as SecurityService from "../services/SecurityService";
import * as SignKeyService from "../services/SignKeyService";

export const addKey = async (req: Request, res: Response) => {
  const { signKey } = req.body;
  const userId = req.authData?.userId!;
  const sessionId = req.authData?.sessionId!;

  try {
    const decrypted = await SecurityService.decryptWithSessionKey(
      signKey,
      sessionId,
    );
    const sharedSecret = await SecurityService.getSharedSecret(userId);
    if (!sharedSecret) {
      res.status(400).json({ message: "Shared secret not found" });
      return;
    }

    await SignKeyService.addKey(decrypted, userId, sharedSecret);

    res.json({ message: "Key added" });
  } catch (error) {
    console.error(error);
    res.status(400).json({ message: "Could not add key" });
  }
};
