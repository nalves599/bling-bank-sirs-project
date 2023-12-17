import {
  ProtectProps,
  UnprotectProps,
  protect,
  unprotect,
  check,
} from "blingbank-lib/dist/utils/crypto";

import { TextEncoder } from "util";

export class Doc {
  account: Account;

  constructor(account: Account) {
    this.account = account;
  }

  async protect(props: ProtectProps) {
    await this.account.protect(props);
  }

  async unprotect(props: UnprotectProps) {
    await this.account.unprotect(props);
  }

  async check(props: UnprotectProps) {
    return await this.account.check(props);
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

  async unprotect(props: UnprotectProps) {
    this.accountHolder = await Promise.all(
      this.accountHolder.map(async (accountHolder) => {
        return await unprotectValue(accountHolder, props);
      }),
    );

    this.balance = await unprotectValue(this.balance, props);
    this.currency = await unprotectValue(this.currency, props);

    // Unprotect movements
    await Promise.all(
      this.movements.map(async (movement) => {
        await movement.unprotect(props);
      }),
    );
  }

  async check(props: UnprotectProps) {
    const accountHolderValid = await Promise.all(
      this.accountHolder.map(async (accountHolder) => {
        return await checkValue(accountHolder, props);
      }),
    );

    const balanceValid = await checkValue(this.balance, props);
    const currencyValid = await checkValue(this.currency, props);

    const movementsValid = await Promise.all(
      this.movements.map(async (movement) => {
        return await movement.check(props);
      }),
    );

    return (
      accountHolderValid.every((valid) => valid) &&
      balanceValid &&
      currencyValid &&
      movementsValid.every((valid) => valid)
    );
  }
}

export class Movement {
  date: string;
  value: string;
  description: string;

  constructor(date: string, value: string, description: string) {
    this.date = date;
    this.value = value;
    this.description = description;
  }

  async protect(props: ProtectProps) {
    this.date = await protectValue(this.date, props);
    this.value = await protectValue(this.value, props);
    this.description = await protectValue(this.description, props);
  }

  async unprotect(props: UnprotectProps) {
    this.date = await unprotectValue(this.date, props);
    this.value = await unprotectValue(this.value, props);
    this.description = await unprotectValue(this.description, props);
  }

  async check(props: UnprotectProps) {
    return (
      (await checkValue(this.date, props)) &&
      (await checkValue(this.value, props)) &&
      (await checkValue(this.description, props))
    );
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
  const { payload } = await unprotect(Buffer.from(value, "base64"), props);
  return new TextDecoder().decode(payload);
};

const checkValue = async (value: string, props: UnprotectProps) => {
  const valid = await check(Buffer.from(value, "base64"), props);
  return valid;
};
