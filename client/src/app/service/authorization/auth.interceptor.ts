import {Injectable} from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor
} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {AuthorizationService} from './authorization.service';
import {catchError, switchMap, tap} from 'rxjs/operators';
import {Router} from '@angular/router';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(private authservice: AuthorizationService, private router: Router) {
  }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (request.url.includes("api/auth/login")) {
      //console.log("Handle request without changing: "+request.url);
      return next.handle(request);
    }
    //console.log("Continuing to auth login", request.url);

    if (this.authservice.getToken()) {
      request = AuthInterceptor.addToken(request, this.authservice.getToken())
      //console.log("Token: ", this.authservice.getToken());
    }

    return next.handle(request).pipe(
      tap(response => {
        //console.log("Got response: ", response);
      }),
      catchError((error: any) => {
          console.log("Error", error);
          if (error && error.status === 200) {
            //console.log("Got 200 response");
            return next.handle(request);
          }
          if (error && error.status === 401) {
            if (request.url.endsWith('/api/auth/login/refresh')) {
              this.forceLogoff();
              return throwError(error);
            } else {
              return this.authservice.refreshToken().pipe(
                switchMap((data) => {
                  request = AuthInterceptor.addToken(request, this.authservice.getToken());
                  return next.handle(request);
                })
              );
            }
          } else {
            return throwError("Could not intercept request")
          }
        }
      ));
  }

  private forceLogoff() {
    this.authservice.logOut();
    this.router.navigate(['login'], {queryParams: {msg: 'session expired'}});
  }

  /**
   * Add the token header to the request. Since HttpRequests are immutable a clone is created
   * @param request
   * @param token
   * @private
   */
  private static addToken(request: HttpRequest<any>, token: string): HttpRequest<any> {
    return request.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }
}
