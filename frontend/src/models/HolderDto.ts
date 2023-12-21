export class HolderDto {
  id!: string
  name!: string
  email!: string

  constructor(jsonObj?: HolderDto) {
    if (jsonObj) {
      this.id = jsonObj.id
      this.name = jsonObj.name
      this.email = jsonObj.email
    }
  }
}
