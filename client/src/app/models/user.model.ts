export class User {
  id!: number;
  role!: number;
  firstname!: string;
  surname!: string;
  password!: string;
  email!: string;
  imagePath?: string;
  activated?: boolean;
  team?: number;
  last_login?: Date;
  level: number;
  role_name: string;

  // setMemberValues(values: any) {
  setMemberValues(values: Omit<User, 'setMemberValues'>) {
    this.id = values.id;
    this.firstname = values.firstname;
    this.surname = values.surname;
    this.password = values.password;
    this.email = values.email;
    this.imagePath = values.imagePath;
    this.team = values.team;
    this.activated = values.activated;
    this.level = values.level;

    return this;
  }
}
