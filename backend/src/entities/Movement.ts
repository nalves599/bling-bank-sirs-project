import Payment from './Payment';

class Movement {
  id: string;

  date: Date;
  value: number;
  description: string;

  payment?: Payment;

  constructor(props: Movement) {
    this.id = props.id;
    this.date = props.date;
    this.value = props.value;
    this.description = props.description;
    this.payment = props.payment;
  }
}

export default Movement;
