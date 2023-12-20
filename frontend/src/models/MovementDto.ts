export class MovementDto {
  movementId!: string

  movementDate!: Date
  movementValue!: number
  movementDescription!: string

  constructor(jsonObj?: MovementDto) {
    if (jsonObj) {
      this.movementId = jsonObj.id
      this.movementDate = jsonObj.date
      this.movementValue = jsonObj.value
      this.movementDescription = jsonObj.description
    }
  }
}
