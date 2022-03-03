import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import * as d3 from 'd3';
import {Note} from 'src/app/models/note';
import {NoteService} from '../../../service/note/note.service';
import {UserService} from '../../../service/member/user.service';


//form interface
interface FormValues {
  noteTitle: string;
  noteContent: string;
  shared: boolean;
}

@Component({
  selector: 'app-note-overview',
  templateUrl: './note-overview.component.html',
  styleUrls: ['./note-overview.component.scss'],
})
export class NoteOverviewComponent implements OnInit {
  submitted: boolean = false;
  selectedNote!: Note;
  isModalEdit: boolean;
  searchTerm: string;

  constructor(
    public noteService: NoteService,
  ) {

    //subscribe to observable
    noteService.selectedNoteSubject.subscribe((note) => {
      this.selectedNote = note;
    });
  }

  //note forms
  newNoteForm = new FormGroup({
    noteTitle: new FormControl('', [
      Validators.required,
      Validators.maxLength(40),
      Validators.minLength(1),
    ]),
    noteContent: new FormControl('', [
      Validators.required,
      Validators.maxLength(1200),
    ]),
    shared: new FormControl(false, Validators.required),
  });

  searchBar = new FormGroup({
    searchTerm: new FormControl('', []),
  });

  // submit form, for edit and create note
  onSubmit(): void {
    this.submitted = true;
    if (this.newNoteForm.invalid) {
      return;
    }

    //make note var with inserted values
    let note: FormValues = this.newNoteForm.value;

    //check if note is being edited or newly created
    if (this.isModalEdit) {
      this.editNote(note);
    } else {
      this.create(note);
    }

    //reset form after submit and generate alert
    this.onReset();
    this.generateAlert(
      this.isModalEdit ? 'Note Edited!' : 'Note Created!',
      'alert-success'
    );
    this.closeModal('closeNewNoteModal');
    this.searchBar.controls.searchTerm.setValue('');
  }

  // create new note from modal and pass new note to selected note
  create(note: FormValues) {
    this.noteService
      .save(
        new Note(
          this.noteService.userTeam.role['id'],
          note.noteTitle,
          this.noteService.userTeam,
          note.noteContent,
          note.shared
        )
      )
      .subscribe((newNote) => {
        this.noteService.selectedNoteSubject.next(newNote);
      });

    this.noteService.restGetNotes();
  }

  // save edited note from modal and pass new note to selected note
  editNote(val: FormValues) {
    const note = new Note(
      this.selectedNote.noteCategory,
      val.noteTitle,
      this.selectedNote.createdBy,
      val.noteContent,
      val.shared
    );

    //add id after form submit, since we get the id from the server
    note.id = this.selectedNote.id;
    note.createdDate = this.selectedNote.createdDate;

    this.noteService.save(note).subscribe((note) => {
      const selectedNoteIndex = this.noteService.notes.indexOf(
        this.selectedNote
      );
      this.noteService.notes[selectedNoteIndex] = note;
    });
    //pass new selected note to subject
    this.noteService.selectedNoteSubject.next(note);
    this.noteService.restGetNotes();
  }

  //delete selected note
  deleteNote(id: number) {
    this.noteService.deleteById(id);
    //close modal and reset searchterm after delete
    this.closeModal('closeDeleteModal');
    this.searchBar.controls.searchTerm.setValue('');
  }

  //get selected note from list
  onClickNote(note: Note) {
    this.noteService.selectedNoteSubject.next(note);
  }

  //manual modal close
  closeModal(idSelector: string) {
    document.getElementById(idSelector).click();
  }

  //update edit modal with current selected note values
  updateForm() {
    this.newNoteForm.setValue({
      noteTitle: this.selectedNote.title,
      noteContent: this.selectedNote.content,
      shared: this.selectedNote.shared,
    });
  }

  //reset form
  onReset(): void {
    this.submitted = false;
    this.newNoteForm.clearValidators;
    this.newNoteForm.setErrors(null);
    this.newNoteForm.controls.noteTitle.setValue('');
    this.newNoteForm.controls.noteContent.setValue('');
    this.newNoteForm.controls.shared.setValue(false);
  }

  generateAlert(text: string, type: string) {
    let alertBox = d3.selectAll('.alert-box');
    alertBox.selectAll('*').remove();
    alertBox
      .append('div')
      .attr('class', 'alert show fade alert-dismissible ' + type)
      .text(text)
      .append('button')
      .attr('type', 'button')
      .attr('data-bs-dismiss', 'alert')
      .attr('class', 'btn-close');
    setTimeout(() => {
      alertBox.selectAll('*').remove();
    }, 5000);
  }

  //format date into specfic format
  formatDate(date: string) {
    return new Intl.DateTimeFormat('nl', {
      // @ts-ignore
      dateStyle: 'short',
      // @ts-ignore
      timeStyle: 'short',
    }).format(new Date(date));
  }

  ngOnInit(): void {
  }
}
