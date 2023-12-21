export class LoginRequestDto {
  email: string
  password: string

  constructor(holderEmail: string, password: string) {
    this.email = holderEmail
    this.password = password
  }
}
