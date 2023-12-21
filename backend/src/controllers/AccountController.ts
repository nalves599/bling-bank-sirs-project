import { Request, Response } from "express";

import * as KeyUtil from "../utils/KeyUtil";
import * as UserService from "../services/UserService";
import * as AccountService from "../services/AccountService";
import * as SecurityService from "../services/SecurityService";
import * as MovementService from "../services/MovementService";
import * as EmailService from "../services/EmailService";
import * as PaymentService from "../services/PaymentService";

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

export const unlockAccount = async (req: Request, res: Response) => {
  const accountId = req.params.id;
  const { secret } = req.body;
  
  try {
    const serverSecret : any = await SecurityService.getShamirSecret(accountId);
  
    const key = await KeyUtil.combineShamirSecrets([serverSecret, secret]);

    SecurityService.saveAccountKey(accountId, key);

    console.log("Account unlocked", accountId, key);
    res.json({ message: "Account unlocked" });
  } catch (error) {
    console.error(error);
    res.status(400).json({ message: "Could not unlock account" });
  }
}

export const getAccountById = async (req: Request, res: Response) => {
  const accountId = req.params.id;
  const sessionId = req.authData?.sessionId!;

  try {
    const accountKey = SecurityService.getAccountKey(accountId);
    if (!accountKey) {
      res.status(400).json({ message: "Account not unlocked" });
      return;
    }

    const account = await AccountService.getAccountById(accountId, accountKey);
    if (!account) {
      res.status(404).json({ message: "Account not found" });
      return;
    }

    const movements = await MovementService.getMovementsByAccountId(accountId, accountKey);
    const payments = await PaymentService.getPaymentsByAccountId(accountId, accountKey);

    const sessionKey = SecurityService.getSessionKey(sessionId);
    if (!sessionKey) {
      res.status(400).json({ message: "Invalid session" });
      return;
    }

    const encryptedAccount = {
      ...account,
      balance: await crypto.paramProtect(crypto.toBytesInt32(account.balance), sessionKey),
      accountHolders: account.accountHolders.map(({id, email}) => ({ id, email})),
      movements: await Promise.all(movements.map(async (movement) => ({
        ...movement,
        value: await crypto.paramProtect(crypto.toBytesInt32(movement.value), sessionKey),
        date: await crypto.paramProtect(movement.date, sessionKey),
        description: await crypto.paramProtect(movement.description, sessionKey),
      }))),
      payments: await Promise.all(payments.map(async (payment) => ({
        ...payment,
        value: await crypto.paramProtect(crypto.toBytesInt32(payment.value), sessionKey),
        description: await crypto.paramProtect(payment.description, sessionKey),
        totp: await crypto.paramProtect(payment.totp, sessionKey),
        signatures: await Promise.all(payment.signatures.map(async (signature) => ({
          ...signature,
          date: await crypto.paramProtect(signature.date, sessionKey),
          content: await crypto.paramProtect(signature.content, sessionKey),
        }))),
      }))),
    };

    res.json(encryptedAccount);
  } catch (error) {
    console.error(error);
    res.status(400).json({ message: "Could not get account" });
  }
};

export const getAccountsByHolder = async (req: Request, res: Response) => {
  const { email } = req.params;

  try {
    const accounts = await AccountService.getAccountsByHolder(email);

    console.log(accounts);

    res.json(accounts);
  } catch (error) {
    console.error(error);
    res.status(400).json({ message: "Could not get accounts" });
  }
}

export const getAccountMovements = async (req: Request, res: Response) => {
  const { accountId } = req.params;

  try {
    const movements = await AccountService.getAccountMovements(accountId);

    res.json(movements);
  } catch (error) {
    console.error(error);
    res.status(400).json({ message: "Could not get movements" });
  }
};

export const getAccountPayments = async (req: Request, res: Response) => {
  const { accountId } = req.params;

  try {
    const payments = await AccountService.getAccountPayments(accountId);

    res.json(payments);
  } catch (error) {
    console.error(error);
    res.status(400).json({ message: "Could not get payments" });
  }
};

export const getPaymentById = async (req: Request, res: Response) => {
  const { id } = req.params;

  try {
    const payment = await AccountService.getPaymentById(id);

    res.json(payment);
  } catch (error) {
    console.error(error);
    res.status(400).json({ message: "Could not get payment" });
  }
}

export const submitPayment = async (req: Request, res: Response) => {
  const accountId = req.params.id;
  const sessionId = req.authData?.sessionId!;
  const { value, description, totp } = req.body;

  try {
    const sessionKey = SecurityService.getSessionKey(sessionId);
    if (!sessionKey) {
      res.status(400).json({ message: "Invalid session" });
      return;
    }
    const decrypted = {
      description: await SecurityService.decryptWithSessionKey(description, sessionId),
      value: parseInt( await SecurityService.decryptWithSessionKey(value, sessionId)),
      totp: await SecurityService.decryptWithSessionKey(totp, sessionId),
    };

    const accountKey = SecurityService.getAccountKey(accountId);
    if (!accountKey) {
      res.status(400).json({ message: "Account not unlocked" });
      return;
    }

    await PaymentService.createPayment(accountId, decrypted, accountKey);

    console.log("Payment submitted", accountId, decrypted);

    res.json({ message: "Payment submitted" });
  } catch (error) {
    console.error(error);
    res.status(400).json({ message: "Could not submit payment" });
  }
}
