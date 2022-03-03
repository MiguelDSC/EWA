import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoginComponent } from './login.component';
import {NoteOverviewComponent} from "../../note-page/note-overview/note-overview.component";
import {BrowserModule} from "@angular/platform-browser";
import {AppRoutingModule} from "../../../app-routing.module";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {CookieService} from "ngx-cookie-service";
import {AuthInterceptor} from "../../../service/authorization/auth.interceptor";
import {JWT_OPTIONS, JwtHelperService} from "@auth0/angular-jwt";
import {LoginService} from "../../../service/authentication/login.service";
import {AuthGuard} from "../../../guards/auth.guard";

fdescribe('LoginComponent', () => {
  let component: LoginComponent;
  let componentHtml: HTMLElement;
  let fixture: ComponentFixture<LoginComponent>;
  let userIsLoggedIn: boolean = false;


  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LoginComponent,
      ],
      imports: [BrowserModule,
        AppRoutingModule,
        FormsModule,
        ReactiveFormsModule.
        withConfig(
          {warnOnNgModelWithFormControl:
              'never'}),
        HttpClientModule,
        AppRoutingModule,
        ReactiveFormsModule,

      ],
      providers: [
        CookieService,
        AuthGuard,
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
      ],

    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    componentHtml = fixture.debugElement.nativeElement;
    fixture.detectChanges();
  });
  //
  // it('should create', () => {
  //   expect(component).toBeTruthy();
  // });


  //
  // it( 'should update the email value in formgroup',  () => {
  //
  //   // Arrange
  //   // getting UI components
  //   const emailInputField: any = component.form.controls['email'];
  //
  //   // Act: Performing inserting values
  //   emailInputField.setValue('test@hotmail.nl')
  //
  //   fixture.detectChanges(); // Angular should be updated
  //
  //   // Assert: Check if the property was updated in ts file
  //   expect(component.form.controls['email'].value).toEqual("test@hotmail.nl");
  // });

  it( 'should throw control error, not all validators passed',  () => {

    // Arrange
    // getting UI components
    const emailInputField: any = component.form.controls['email'];
    const PasswordInputField: any = component.form.controls['password'];


    // Act: Performing placing incorrct values into the
    emailInputField.setValue('test@hotmail.nl');
    PasswordInputField.setValue('');

    fixture.detectChanges(); // Angular should be updated

    // Assert: Check if the control validation catches wrong input
    expect(component.form.invalid).toBeTrue()

  });


  it( 'should log user into website',  (done) => {

    // Arrange
    // getting UI components
    const emailInputField: any = component.form.controls['email'];
    const PasswordInputField: any = component.form.controls['password'];


    // Act: Performing placing incorrct values into the
    emailInputField.setValue('bart@hva.nl');
    PasswordInputField.setValue('bart');

    const mockLoginService = fixture.debugElement.injector.get(LoginService);

    fixture.detectChanges(); // Angular should be updated

    //call login with correct data, and check if login is valid too
    mockLoginService.login(component.form.getRawValue()).subscribe(data => {
      userIsLoggedIn = mockLoginService.checkLoginValid(data)
    })

    // Assert: Check if the user has been properly logged in
    expect(userIsLoggedIn).toBeTrue()

  });
});
