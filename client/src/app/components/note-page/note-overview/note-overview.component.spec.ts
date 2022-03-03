import {ComponentFixture, TestBed} from '@angular/core/testing';
import {NoteOverviewComponent} from './note-overview.component';
import {NoteService} from "../../../service/note/note.service";
import {UserService} from "../../../service/member/user.service";
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {JWT_OPTIONS, JwtHelperService} from "@auth0/angular-jwt";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {NoteDetailComponent} from "../note-detail/note-detail.component";
import {filterdNotesPipe} from "./filterdNotesPipe";
import {BrowserModule, By} from "@angular/platform-browser";
import {AppRoutingModule} from "../../../app-routing.module";
import {CookieService} from "ngx-cookie-service";
import {AuthInterceptor} from "../../../service/authorization/auth.interceptor";
import {GreenhouseService} from "../../../service/greenhouse-data-service/greenhouse.service";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {UserTeam} from "../../../models/userTeam";
import {User} from "../../../models/user.model";


fdescribe('NoteOverviewComponent', () => {
  let component: NoteOverviewComponent;
  let componentHtml: HTMLElement;
  let fixture: ComponentFixture<NoteOverviewComponent>;
  let noteService: NoteService


  beforeEach(async () => {

    await TestBed.configureTestingModule({
      declarations: [NoteOverviewComponent, filterdNotesPipe, NoteDetailComponent
      ],
      imports: [BrowserModule,
        AppRoutingModule,
        FormsModule,
        ReactiveFormsModule.withConfig(
          {
            warnOnNgModelWithFormControl:
              'never'
          }),
        HttpClientModule,
        HttpClientTestingModule,
        AppRoutingModule,
        ReactiveFormsModule,

      ],
      providers: [
        GreenhouseService,
        CookieService,
        {
          provide: HTTP_INTERCEPTORS,
          useClass: AuthInterceptor,
          multi: true
        },
        {
          provide: JWT_OPTIONS,
          useValue: JWT_OPTIONS
        },
        JwtHelperService,
        NoteService,
      ],
    })
      .compileComponents();

  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NoteOverviewComponent);
    component = fixture.componentInstance;
    componentHtml = fixture.debugElement.nativeElement;
    fixture.detectChanges();

  });

  it('Search input should update the component', function () {

    // Arrange (getting UI components)
    const searchInput: HTMLInputElement = componentHtml.querySelector('#searchInput');

    //act
    searchInput.value = 'test';

    searchInput.dispatchEvent(new Event('input'));

    //assert
    expect(component.searchTerm).toEqual(searchInput.value);


  });


  it('should show add note button if not logged in as guest', function () {

    //arrange
    let user = new User();
    noteService = fixture.debugElement.injector.get(NoteService);

    //act
    noteService.userTeam = new UserTeam(1, 2, 2, 1, user)
    fixture.detectChanges();

    //cross check: as researcher, add button should be present
    expect(fixture.debugElement.query(By.css('#create-new-note-btn'))).toBeTruthy();

    //act, change user level to guest
    noteService.userTeam.level = 3
    fixture.detectChanges();

    //assert as a guest the add button should not present
    expect(fixture.debugElement.query(By.css('#create-new-note-btn'))).toBeNull();

  });






});

