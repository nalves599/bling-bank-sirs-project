import { Request, Response } from "express";

import * as SecurityService from "../services/SecurityService";

export const addKey = async (req: Request, res: Response) => {
  const { signKey } = req.body;
  const userId = req.authData?.userId!;
  const sessionId = req.authData?.sessionId!;

  try {
    const decrypted = await SecurityService.decryptWithSessionKey(
      signKey,
      sessionId,
    );

    console.log("Add key", { signKey, userId, decrypted });
    res.json()
  } catch (error) {
    console.error(error);
    res.status(400).json({ message: "Could not add key" });
  }
};
