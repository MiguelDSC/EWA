import {ComponentFixture, TestBed} from '@angular/core/testing';

import {AuthenticateComponent} from './authenticate.component';
import {BrowserModule} from "@angular/platform-browser";
import {AppRoutingModule} from "../../../app-routing.module";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {CookieService} from "ngx-cookie-service";
import {AuthInterceptor} from "../../../service/authorization/auth.interceptor";
import {JWT_OPTIONS, JwtHelperService} from "@auth0/angular-jwt";

describe('AuthenticateComponent', () => {
  let component: AuthenticateComponent;
  let componentHtml: HTMLElement;
  let fixture: ComponentFixture<AuthenticateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AuthenticateComponent],
      imports: [BrowserModule,
        AppRoutingModule,
        FormsModule,
        ReactiveFormsModule.withConfig(
          {
            warnOnNgModelWithFormControl:
              'never'
          }),
        HttpClientModule,
        AppRoutingModule,
        ReactiveFormsModule,

      ],providers: [
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
      ],

    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AuthenticateComponent);
    component = fixture.componentInstance;
    componentHtml = fixture.debugElement.nativeElement;
    fixture.detectChanges();
  });

  // it('should create', () => {
  //   expect(component).toBeTruthy();
  // });

  it('should correctly update password value in control group, and both passwords should be the same', function () {

    // Arrange
    // getting UI components
    const passwordInputField1: any = component.formRegister.controls['password']
    const passwordInputField2: any = component.formRegister.controls['confirmPassword']

    // Act: Performing inserting values
    passwordInputField1.setValue('Test12345')
    passwordInputField2.setValue('Test12345')

    fixture.detectChanges(); // Angular should be updated

    // Assert: Check if the property was updated in ts file
    expect(component.formRegister.controls['password'].value).toEqual("Test12345");
    expect(component.formRegister.controls['password'].value).toEqual(passwordInputField2.value)

  });

});
