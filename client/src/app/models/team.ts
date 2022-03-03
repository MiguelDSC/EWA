import {GreenhouseInterface} from "../interfaces/greenhouse";

export class Team {
  name: string;
  id: number;
  greenhouse: GreenhouseInterface;
  greenhouseId: number


  constructor(name: string, id: number = 0, greenhouse: GreenhouseInterface) {
    this.name = name;
    this.greenhouse = greenhouse;
    this.greenhouseId = greenhouse.greenhouseId;
    this.id = id;
  }
}
