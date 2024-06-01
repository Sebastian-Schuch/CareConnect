import {Component, OnInit} from '@angular/core';
import {AllergyCreateDto} from "../../dtos/allergy";
import {AllergyService} from "../../services/allergy.service";
import {NgForm, NgModel} from "@angular/forms";
import {Observable} from "rxjs";
import {ToastrService} from "ngx-toastr";
import {Router} from "@angular/router";
import {ErrorFormatterService} from "../../services/error-formatter.service";

@Component({
  selector: 'app-allergy',
  templateUrl: './allergy.component.html',
  styleUrl: './allergy.component.scss'
})
export class AllergyComponent implements OnInit {

  constructor(
    private allergyService: AllergyService,
    private notification: ToastrService,
    private errorFormatterService: ErrorFormatterService,
    private router: Router
  ) {
  }

  ngOnInit(): void {
  }

  allergyname: string = '';

  public onSubmit(form: NgForm): void {
    if (form.valid) {
      const allergy: AllergyCreateDto = {
        name: this.allergyname
      };
      let observable: Observable<AllergyCreateDto> = this.allergyService.createAllergy(allergy);

      observable.subscribe({
        next: data => {
          this.notification.success('Successfully created ' + data.name + ' Allergy');
        },
        error: async error => {
          switch (error.status) {
            case 422:
              this.notification.error(this.errorFormatterService.format(JSON.parse(await error.error.text()).ValidationErrors), `Could not create Allergy`, {
                enableHtml: true,
                timeOut: 10000
              });
              break;
            case 401:
              this.notification.error(await error.error.text(), `Could not create Allergy`);
              this.router.navigate(['/']);
              break;
            default:
              this.notification.error(await error.error.text(), `Could not create Allergy`);
              break;
          }
        }
      });
    }
  }

  public dynamicCssClassesForInput(input: NgModel): any {
    return {
      'is-invalid': !input.valid && !input.pristine,
    };
  }
}
