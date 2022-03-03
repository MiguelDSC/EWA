export class Role {
  id!: number;
  name: String;

  // setMemberValues(values: any) {
  setMemberValues(values: Omit<Role, 'setMemberValues'>) {
    this.name = values.name;
    return this;
  }
}
