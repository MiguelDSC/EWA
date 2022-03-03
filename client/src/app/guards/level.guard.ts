import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import {AuthorizationService} from "../service/authorization/authorization.service";

@Injectable({
  providedIn: 'root'
})
export class LevelGuard implements CanActivate {

  constructor(private authService: AuthorizationService) { }
  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {

    // if (this.authService.isAdmin() < 2) {
    //   return true;
    // }

    return true;
  }

}
