import {Injectable, SecurityContext} from '@angular/core';
import {DomSanitizer} from '@angular/platform-browser';

@Injectable({
  providedIn: 'root'
})
export class ErrorFormatterService {

  constructor(
    private domSanitizer: DomSanitizer
  ) {
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
