class Movement {
  date: Date;
  value: number;
  description: string;

  constructor({ date, value, description }: Movement) {
    this.date = date;
    this.value = value;
    this.description = description;
  }
}

/*
 * User -----> Accounts
 * Account -----> Movements
 * Movement -----> Payment
 * Payment -----> User
 * ----------------
 */

export default Movement;
