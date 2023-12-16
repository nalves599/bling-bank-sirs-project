class User {
  id?: string;

  email: string;
  passwordHash?: string;
  name: string;

  constructor(props: User) {
    this.id = props.id;
    this.email = props.email;
    this.passwordHash = props.passwordHash;
    this.name = props.name;
  }

}
