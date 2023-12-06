export class MovementDto {
  movementId!: string

  movementDate!: Date
  movementValue!: number
  movementDescription!: string

  constructor(jsonObj?: MovementDto) {
    if (jsonObj) {
      this.movementId = jsonObj.movementId
      this.movementDate = jsonObj.movementDate
      this.movementValue = jsonObj.movementValue
      this.movementDescription = jsonObj.movementDescription
    }
  }
}
