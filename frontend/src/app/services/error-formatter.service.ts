import {Injectable, SecurityContext} from '@angular/core';
import {DomSanitizer} from '@angular/platform-browser';
import {ToastrService} from "ngx-toastr";
import {Router} from "@angular/router";

@Injectable({
  providedIn: 'root'
})
export class ErrorFormatterService {

  validationErrors: string;
  constructor(
    private domSanitizer: DomSanitizer, private router: Router
  ) {
  }

  async printErrorToNotification(error: any, title: string, toastrService: ToastrService): Promise<void>;
  async printErrorToNotification(error: any, title: string, toastrService: ToastrService, messageBody: string): Promise<void>;

  async printErrorToNotification(error: any, title: string, toastrService: ToastrService, messageBody?: string): Promise<void> {
    switch (error.status) {
      case 400:
        toastrService.error(messageBody || title, title);
        break;
      case 422:
        this.validationErrors = "<ul>";
        for (const key in error?.error?.ValidationErrors) {
          this.validationErrors += `<li>${error?.error?.ValidationErrors[key]}</li>`;
        }
        this.validationErrors += "</ul>";

        toastrService.error(this.validationErrors, title, {
          enableHtml: true,
          timeOut: 10000
        });
        break;
      case 401:
        this.router.navigate(['/']).then(async r => {
          toastrService.error(await error.error.text(), title);
        });
        break;
      default:
        toastrService.error(await error.error.text(), title);
        break;
    }
  }


  format(errors: any): string {
    let message = '';
    if (!!errors) {
      message += '<ul>';
      for (const e of errors) {
        const sanE = this.domSanitizer.sanitize(SecurityContext.HTML, e);
        message += `<li>${sanE}</li>`;
      }
      message += '</ul>';
    } else {
      message += '.';
    }
    return message;
  }
}
