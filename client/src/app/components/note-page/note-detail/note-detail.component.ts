import {Component, OnInit} from '@angular/core';
import {Note} from 'src/app/models/note';
import {NoteService} from '../../../service/note/note.service';

@Component({
  selector: 'app-note-detail',
  templateUrl: './note-detail.component.html',
  styleUrls: ['./note-detail.component.scss'],
})
export class NoteDetailComponent implements OnInit {

  //current selected note
  selectedNote: Note;

  constructor(public noteService: NoteService) {
    //subscribe to observable
    this.noteService.selectedNoteSubject.subscribe((note) => {
      this.selectedNote = note;
    });

  }

  ngOnInit(): void {
  }

}
