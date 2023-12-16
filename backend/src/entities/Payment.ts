class Payment {
  id: string;

  date: Date;
  amount: number;
  description: string;

  signatures?: Signature[];
}

export default Payment;
