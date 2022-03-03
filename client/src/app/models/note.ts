import { User } from './user.model';
import {UserTeam} from "./userTeam";

export class Note {
  id: number;
  noteCategory: NoteCategory;
  title: String;
  createdBy: UserTeam;
  content: String;
  shared: boolean;
  createdDate!: string;
  updatedDate!: string;

  constructor(
    noteCategory: NoteCategory,
    title: String,
    createdBy: UserTeam,
    content: String,
    shared: boolean
  ) {
    this.noteCategory = noteCategory;
    this.title = title;
    this.createdBy = createdBy;
    this.content = content;
    this.shared = shared;
  }


}

export enum NoteCategory {
  Nan,
  "HYDROLOGY",
  "BOTANY",
  "GEOLOGY",
  "AGRONOMY",
  "CLIMATE-SCIENCE",
}


