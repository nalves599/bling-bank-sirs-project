import { ProtectProps, UnprotectProps, protect, unprotect } from "blingbank-lib/dist/utils/crypto";

import { TextEncoder } from "util";

export class Doc {
  account: Account;

  constructor(account: Account) {
    this.account = account;
  }

  async protect(props: ProtectProps) {
    await this.account.protect(props);
  }
}

export class Account {
  accountHolder: string[];
  balance: string;
  currency: string;
  movements: Movement[];

  constructor(
    accountHolder: string[],
    balance: string,
    currency: string,
    movements: Movement[],
  ) {
    this.accountHolder = accountHolder;
    this.balance = balance;
    this.currency = currency;
    this.movements = movements;
  }

  async protect(props: ProtectProps) {
    this.accountHolder = await Promise.all(
      this.accountHolder.map(async (accountHolder) => {
        return await protectValue(accountHolder, props);
      }),
    );

    this.balance = await protectValue(this.balance, props);
    this.currency = await protectValue(this.currency, props);

    // Protect movements
    await Promise.all(
      this.movements.map(async (movement) => {
        await movement.protect(props);
      }),
    );
  }
}

export class Movement {
  date: string;
  amount: string;
  description: string;

  constructor(date: string, amount: string, description: string) {
    this.date = date;
    this.amount = amount;
    this.description = description;
  }

  async protect(props: ProtectProps) {
    this.date = await protectValue(this.date, props);
    this.amount = await protectValue(this.amount, props);
    this.description = await protectValue(this.description, props);
  }

  async unprotect(props: UnprotectProps) {
    this.date = await unprotectValue(this.date, props);
    this.amount = await unprotectValue(this.amount, props);
    this.description = await unprotectValue(this.description, props);
  }
}

const protectValue = async (value: string, props: ProtectProps) => {
  const { messageEncrypted } = await protect(
    new TextEncoder().encode(value),
    props,
  );
  return Buffer.from(messageEncrypted).toString("base64");
};

const unprotectValue = async (value: string, props: UnprotectProps) => {
  const { payload } = await unprotect(
    Buffer.from(value, "base64"),
    props,
  );
  return new TextDecoder().decode(payload);
};
