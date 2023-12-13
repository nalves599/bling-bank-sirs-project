export class PaymentDto {
  paymentId!: string

  paymentDate!: Date
  paymentAmount!: number
  paymentDescription!: string
  paymentCurrencyType!: string

  paymentRequiredApprovals!: number
  paymentApprovedApprovals!: number

  paymentApproved!: boolean

  constructor(jsonObj?: PaymentDto) {
    if (jsonObj) {
      this.paymentId = jsonObj.id
      this.paymentDate = jsonObj.date
      this.paymentAmount = jsonObj.amount
      this.paymentDescription = jsonObj.description
      this.paymentCurrencyType = jsonObj.currencyType
      this.paymentRequiredApprovals = jsonObj.requiredApprovals
      this.paymentApprovedApprovals = jsonObj.approvedApprovals
      this.paymentApproved = jsonObj.approved
    }
  }
}
