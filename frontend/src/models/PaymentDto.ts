export class PaymentDto {
  id?: string

  totp!: string
  value!: string
  description!: string
  currencyType?: string
  accountId!: number
  hash?: string

  requiredApprovals?: number
  approvedApprovals?: number

  approved?: boolean

  constructor(jsonObj?: PaymentDto) {
    if (jsonObj) {
      this.id = jsonObj.id
      this.totp = jsonObj.totp
      this.value = jsonObj.value
      this.description = jsonObj.description
      this.currencyType = jsonObj.currencyType
      this.hash = jsonObj.hash
      this.requiredApprovals = jsonObj.requiredApprovals
      this.approvedApprovals = jsonObj.approvedApprovals
      this.approved = jsonObj.approved
    }
  }
}
