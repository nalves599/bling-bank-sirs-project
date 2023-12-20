export class AccountDto {
  id?: string
  accountHolders?: string[]

  name!: string
  currency!: string

  constructor(jsonObj?: AccountDto) {
    if (jsonObj) {
      this.id = jsonObj.id
      this.accountHolders = jsonObj.accountHolders
      this.name = jsonObj.name
      this.currency = jsonObj.currency
    }
  }
}
