export class HolderDto {
  accountHolderId!: string
  holderName!: string

  constructor(jsonObj?: HolderDto) {
    if (jsonObj) {
      this.accountHolderId = jsonObj.accountHolderId
      this.holderName = jsonObj.holderName
    }
  }
}
