import {User} from "./user.model";

export class UserTeam {
  id!: number;
  level!:number;
  role!: any;
  team!: any
  user!: User;


  constructor(id: number, level: number, role: any, team: any, user: User) {
    this.id = id;
    this.level = level;
    this.role = role;
    this.team = team;
    this.user = user;
  }
}
