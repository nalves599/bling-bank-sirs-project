import { MovementDto } from './MovementDto'

export class AccountDto {
  holders!: string[]
  accountId!: string

  balance!: number
  currency!: string

  movement!: MovementDto[]
  numberOfMovements!: number

  constructor(jsonObj?: AccountDto) {
    if (jsonObj) {
      this.accountId = jsonObj.id
      this.holders = jsonObj.accountHolder.map((holder) => holder.holderName)
      this.balance = jsonObj.balance
      this.currency = jsonObj.currency
      this.movement = jsonObj.movements.map((movement) => new MovementDto(movement))
      this.numberOfMovements = this.movement.length
    }
  }
}
