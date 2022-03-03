import {Injectable} from '@angular/core';
import {BehaviorSubject, Observable} from 'rxjs';
import {Note, NoteCategory} from '../../models/note';
import {HttpClient} from '@angular/common/http';
import {UserService} from '../member/user.service';
import {UserTeam} from '../../models/userTeam';
import jwt_decode from 'jwt-decode';
import {environment} from 'src/environments/environment';

//filters for overview-list
type Filters =
  | 'team-notes'
  | 'agronomy'
  | 'botany'
  | 'hydrology'
  | 'geology'
  | 'climate-science';

@Injectable({
  providedIn: 'root',
})
export class NoteService {
  public notes: Note[] = [];
  public userTeam: UserTeam;
  //start active filters with team-notes pre selected
  public activeFilter: Filters[] = ['team-notes'];
  //get info from decoded token
  public decodedToken: any = jwt_decode(localStorage.getItem('token'));
  private url: string = `${environment.api}/note-page`;

  //create observable subject, so we can subscribe in other components
  public selectedNoteSubject: BehaviorSubject<Note> = new BehaviorSubject<Note>(
    null
  );

  constructor(private http: HttpClient, private userService: UserService) {
    //get userteam info from
    this.userService
      .getUserTeamInfo(this.decodedToken.jti)
      .subscribe((data) => {
        this.userTeam = data;
        //push role category into active filters
        this.activeFilter.push(this.getCurrentUserCategoryAsFilter());
        //get notes from backend
        this.restGetNotes();
      });
  }

  //return all notes from list and subscribe
  restGetNotes() {
    return this.http
      .get<Note[]>(`${this.url}`, {
        params: {
          'active-filters': this.activeFilter.toString() // team-notes
        },
      })
      .subscribe((data) => {
        //sort notes based on created date
        this.notes = data.sort(
          (a, b) => +new Date(b.createdDate) - +new Date(a.createdDate)
        );
      });
  }

  //if logged in as a research scientist, get the role category.
  public getCurrentUserCategoryAsFilter(): any {
    if (this.userTeam.role.id != 6) {
      return NoteCategory[this.userTeam.role.id].toLocaleLowerCase() as Filters;
    }

  }

  //switch between active filters
  toggleFilter(value: Filters) {
    //whilst filtering selected note should be null
    this.selectedNoteSubject.next(null);
    //check if incoming filter is already in active filter list
    const isActive = this.activeFilter.includes(value);
    if (isActive) {
      //remove active item
      this.activeFilter = this.activeFilter.filter(
        (filter) => filter !== value
      );

      this.restGetNotes();

      return;
    }
    //if incoming filter not in list push
    this.activeFilter.push(value);
    this.restGetNotes();
  }

  //api call to post a new note
  restPostNote(note: Note): Observable<Note> {
    return this.http.post<Note>(`${this.url}`, note);
  }

  //api call to put a edited note
  restPutNote(note: Note): Observable<Note> {
    return this.http.put<Note>(`${this.url}/${note.id}`, note);
  }

  //api call to delete a note
  restDeleteNote(noteId: number): void {
    this.http.delete<Note>(`${this.url}/${noteId}`).subscribe();
  }

  //save function
  public save(note: Note) {
    const exists = this.notes.findIndex((x) => x.id === note.id);
  //if note is found in list, use put else use post
    if (exists !== -1) {
      return this.restPutNote(note);
    } else {
      this.activeFilter.push('team-notes');
      this.restGetNotes();
      return this.restPostNote(note);
    }
  }

  //delete note with incoming id
  public deleteById(id: number): void {
    this.restDeleteNote(id);
    this.notes.splice(
      this.notes.findIndex((x) => x.id === id),
      1
    );
    //make selected note null
    this.selectedNoteSubject.next(null);
  }
}
