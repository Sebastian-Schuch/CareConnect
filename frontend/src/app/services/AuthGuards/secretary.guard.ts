import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree} from '@angular/router';
import {Observable} from 'rxjs';
import {AuthService} from '../auth.service';
import {Role} from "../../dtos/Role";

@Injectable({
  providedIn: 'root'
})
export class SecretaryGuard implements CanActivate {

  constructor(private authService: AuthService, private router: Router) {
  }

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    if (this.authService.isLoggedIn() && this.authService.getUserRole() === Role.secretary) {
      return true;
    } else {
      this.router.navigate(['/']);
      return false;
    }
  }
}
