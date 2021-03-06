import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NotePageComponent } from './note-page.component';
import {NoteService} from "../../service/note/note.service";

describe('NotePageComponent', () => {
  let component: NotePageComponent;
  let fixture: ComponentFixture<NotePageComponent>;
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ NotePageComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NotePageComponent);
    component = fixture.componentInstance;

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
