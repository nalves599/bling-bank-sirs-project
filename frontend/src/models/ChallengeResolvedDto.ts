export class ChallengeResolvedDto {
  email!: string
  solution!: string | Uint8Array

  constructor(json?: ChallengeResolvedDto) {
    if (json) {
      this.email = json.email
      this.solution = json.solution
    }
  }
}
