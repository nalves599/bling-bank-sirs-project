export class LoginRequestDto {
  holderName: string
  password: string

  constructor(holderName: string, password: string) {
    this.holderName = holderName
    this.password = password
  }
}
