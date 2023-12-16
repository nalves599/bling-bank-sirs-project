class Payment {
  id: string;

  date: Date;
  amount: number;
  description: string;

  signatures?: Signature[];

  constructor(props: Payment) {
    this.id = props.id;
    this.date = props.date;
    this.amount = props.amount;
    this.description = props.description;
    this.signatures = props.signatures;
  }
}

export default Payment;
