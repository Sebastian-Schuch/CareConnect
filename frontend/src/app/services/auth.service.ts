import {Injectable} from '@angular/core';
import {AuthRequest} from '../dtos/auth-request';
import {BehaviorSubject, Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {tap} from 'rxjs/operators';
import {jwtDecode} from 'jwt-decode';
import {Globals} from '../global/globals';
import {Role} from "../dtos/Role";
import {ToastrService} from "ngx-toastr";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private authBaseUri: string = this.globals.backendUri + '/authentication';

  public isLogged = new BehaviorSubject<boolean>(false);
  cast = this.isLogged.asObservable();

  constructor(private httpClient: HttpClient, private globals: Globals, private notification: ToastrService) {
  }

  /**
   * Login in the user. If it was successful, a valid JWT token will be stored
   *
   * @param authRequest User data
   */
  loginUser(authRequest: AuthRequest): Observable<string> {
    return this.httpClient.post(this.authBaseUri, authRequest, {responseType: 'text'})
      .pipe(
        tap((authResponse: string) => {
          this.setToken(authResponse);
          this.isLogged.next(true);
        })
      );
  }


  /**
   * Check if a valid JWT token is saved in the localStorage
   */
  isLoggedIn() {
    return !!this.getToken() && (this.getTokenExpirationDate(this.getToken()).valueOf() > new Date().valueOf());
  }

  logoutUser() {
    this.notification.success('Successfully logged out');
    localStorage.removeItem('authToken');
    this.isLogged.next(false);
  }

  getToken() {
    return localStorage.getItem('authToken');
  }

  /**
   * Returns the user role based on the current token
   */
  getUserRole(): Role {
    if (this.getToken() != null) {
      const decoded: any = jwtDecode(this.getToken());
      const authInfo: string[] = decoded.rol;
      if (authInfo.includes('ADMIN')) {
        return Role.admin;
      } else if (authInfo.includes('DOCTOR')) {
        return Role.doctor;
      } else if (authInfo.includes('SECRETARY')) {
        return Role.secretary;
      } else if (authInfo.includes('PATIENT')) {
        return Role.patient;
      }
    }
    return undefined;
  }

  getUserEmail(): string {
    const token = this.getToken();
    if (token != null) {
      const decoded = jwtDecode(token);
      return decoded.sub;
    }
  }

  updateUserInformation() {
    this.isLogged.next(true);
  }

  setToken(authResponse: string) {
    localStorage.setItem('authToken', authResponse);
  }

  private getTokenExpirationDate(token: string): Date {

    const decoded: any = jwtDecode(token);
    if (decoded.exp === undefined) {
      return null;
    }

    const date = new Date(0);
    date.setUTCSeconds(decoded.exp);
    return date;
  }
}
