class SignKey {
  createdAt: Date;
  deletedAt?: Date;

  hash: string;
  content: string;

  constructor(props: SignKey) {
    this.createdAt = props.createdAt;
    this.deletedAt = props.deletedAt;
    this.hash = props.hash;
    this.content = props.content;
  }
}

export default SignKey;
