import {TestBed} from '@angular/core/testing';
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {JWT_OPTIONS, JwtHelperService} from "@auth0/angular-jwt";
import {User} from "../../models/user.model";
import {UserTeam} from "../../models/userTeam";
import {environment} from "../../../environments/environment";
import {NoteService} from './note.service';
import {Note, NoteCategory} from "../../models/note";

fdescribe('NoteService', () => {
  let noteService: NoteService;
  let httpMock: HttpTestingController
  let _url: string = `${environment.api}/note-page`;

  //filters
  type Filters =
    | 'team-notes'
    | 'agronomy'
    | 'botany'
    | 'hydrology'
    | 'geology'
    | 'climate-science';

  beforeEach((() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [NoteService, JwtHelperService, {provide: JWT_OPTIONS, useValue: JWT_OPTIONS}]

    })
    //inject needed service
    noteService = TestBed.inject(NoteService)
    httpMock = TestBed.inject(HttpTestingController);

  }));


  it('should a post a note through rest', () => {
    //Arrange, create needed info for note
    let user = new User();
    let userTeam = new UserTeam(1, 2, 2, 1, user)
    let note = new Note(NoteCategory.BOTANY, "Test note", userTeam, "Test content", true)

    //act. make call to post a newly created note
    noteService.restPostNote(note).subscribe((data: Note) => {
     note = data

    });

    expect(note.shared).toBeTrue();

    //assert, that post call was made with right url
    const req = httpMock.expectOne(_url);
    expect(req.request.method).toBe('POST');

  });


  it('should add filter to active filter list if not included, and remove if already in it', () => {

    //Arrange empty filter list
    noteService.activeFilter = []

    //act
    const botanyFilter = NoteCategory[2].toLocaleLowerCase() as Filters;
    noteService.toggleFilter(botanyFilter)

    //assert botany was pushed to the active filter list
    expect(noteService.activeFilter.includes("botany")).toBeTrue();

    //act add botany again
    noteService.toggleFilter(botanyFilter)

    //assert that botany isn't in the active filter list
    expect(noteService.activeFilter.includes("botany")).toBeFalse();

  });


});
