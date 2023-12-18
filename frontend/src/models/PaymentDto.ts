export class PaymentDto {
  id!: string

  date!: Date
  amount!: number
  description!: string
  currencyType!: string
  accountId!: number

  requiredApprovals!: number
  approvedApprovals!: number

  approved!: boolean

  constructor(jsonObj?: PaymentDto) {
    if (jsonObj) {
      this.id = jsonObj.id
      this.date = jsonObj.date
      this.amount = jsonObj.amount
      this.description = jsonObj.description
      this.currencyType = jsonObj.currencyType
      this.requiredApprovals = jsonObj.requiredApprovals
      this.approvedApprovals = jsonObj.approvedApprovals
      this.approved = jsonObj.approved
    }
  }
}
