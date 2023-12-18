export class LoginResponseDto {
  token!: string

  constructor(json?: LoginResponseDto) {
    if (json) {
      this.token = json.token
    }
  }
}
