import { Request, Response } from "express";

import * as KeyUtil from "../utils/KeyUtil";
import * as UserService from "../services/UserService";
import * as AccountService from "../services/AccountService";
import * as SecurityService from "../services/SecurityService";
import * as EmailService from "../services/EmailService";

import { crypto } from "blingbank-lib";

export const createAccount = async (req: Request, res: Response) => {
  const { name, currency, accountHolders } = req.body;

  try {
    // TODO: Check if user is authorized to create account
    // TODO: Check if account holders are valid users
    const users = await Promise.all(
      accountHolders.map((email: string) => UserService.getUserByEmail(email)),
    );

    const { key, secrets } = await KeyUtil.generateShamirSecrets(
      users.length + 1,
      2,
    );

    const account = await AccountService.createAccount(
      {
        name,
        currency,
        balance: 0,
        accountHolders: users.map((user) => user.id),
      },
      key,
    );

    await SecurityService.saveShamirSecret(account.id, secrets[0]);

    // Send emails assynchronously
    users.map((user, index) =>
      EmailService.sendShamirSecret(
        user.email,
        account.id,
        account.name,
        Buffer.from(secrets[index + 1]).toString("base64"),
      ),
    );

    res.json(account);
  } catch (error) {
    console.error(error);
    res.status(400).json({ message: "Could not create account" });
  }
};

export const getAccountById = async (req: Request, res: Response) => {
  const { accountId } = req.params;
  const sessionId = req.authData?.sessionId!;
  const accountSecret = String(req.get("X-ACCOUNT-SECRET"));

  if (!accountSecret) {
    return res.status(400).json({ message: "Missing account secret" });
  }
  try {
    // const sessionKey = await SecurityService.getSessionKey(sessionId);
    // const userSecret = Buffer.from(await crypto.paramUnprotect(accountSecret));

    // const serverSecret = await SecurityService.getShamirSecret(accountId);
    // const key = KeyUtil.combineShamirSecrets([shamirSecret]);

    // const account = await AccountService.getAccount(accountId);

    // if (!account) {
    //   return res.status(404).json({ message: "Account not found" });
    // }

    res.json({});
  } catch (error) {
    console.error(error);
    res.status(400).json({ message: "Could not get account" });
  }
};
