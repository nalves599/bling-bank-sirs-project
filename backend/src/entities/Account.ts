class Account {
  id?: string;

  name?: string;
  accountHolders: string[];
  balance: string;
  currency: string;

  constructor(props: Account) {
    this.id = props.id;
    this.name = props.name;
    this.accountHolders = props.accountHolders;
    this.balance = props.balance;
    this.currency = props.currency;
  }
}
