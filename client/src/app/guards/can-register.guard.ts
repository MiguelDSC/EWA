import { Injectable } from '@angular/core';
import {
  ActivatedRoute,
  ActivatedRouteSnapshot,
  CanActivate,
  CanActivateChild,
  Router,
  RouterStateSnapshot,
  UrlTree
} from '@angular/router';
import {Observable, throwError} from 'rxjs';
import { RegisterService } from '../service/member/register.service';
import {catchError, tap} from "rxjs/operators";
import {error} from "@angular/compiler/src/util";

@Injectable({
  providedIn: 'root'
})
export class CanRegisterGuard implements CanActivate {
  constructor(private registerService: RegisterService, private router: Router, private activatedRoute: ActivatedRoute) { }

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {

    let hash = route.params.hash

    // this.registerService.getUser(hash).pipe((err) => {
    //   console.log(data);
    //   return true;
    // }, (() => {
    //   this.registerService.getInvite(hash).subscribe((data) => {
    //     console.log(data);
    //   })
    //   // this.router.navigate(['login']);
    //   // return false;
    // }));
    console.log(hash);
    this.registerService.getUser(hash).pipe(
      tap(data => console.log('server data', data)),
      catchError(error => {
        // this.router.navigate(['dashboard'])
        return error;
      })
    ).subscribe((data) => {
      console.log(data);
    })

    // return false;
    return true;
  }

}
