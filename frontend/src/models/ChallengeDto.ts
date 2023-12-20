export class ChallengeDto {
  challenge!: string

  constructor(json?: ChallengeDto) {
    if (json) {
      this.challenge = json.challenge
    }
  }
}
