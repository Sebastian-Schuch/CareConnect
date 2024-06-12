import {Component, OnInit} from '@angular/core';
import {MedicationDtoCreate} from "../../dtos/medication";
import {Router} from "@angular/router";
import {MedicationService} from "../../services/medication.service";
import {NgForm, NgModel} from "@angular/forms";
import {Observable} from "rxjs";
import {ToastrService} from "ngx-toastr";
import {ErrorFormatterService} from "../../services/error-formatter.service";

@Component({
  selector: 'app-medication-create',
  templateUrl: './medication-create.component.html',
  styleUrls: ['./medication-create.component.scss', '../../../styles.scss']
})
export class MedicationCreateComponent implements OnInit {

  medication: MedicationDtoCreate = {
    name: ''
  };

  constructor(
    private service: MedicationService,
    private router: Router,
    private notification: ToastrService,
    private errorFormatterService: ErrorFormatterService,
  ) {
  }

  ngOnInit(): void {
  };

  public dynamicCssClassesForInput(input: NgModel): any {
    return {
      'is-invalid': !input.valid && !input.pristine,
    };
  }

  public onSubmit(form: NgForm): void {
    if (form.valid) {
      let observable: Observable<MedicationDtoCreate>;
      observable = this.service.createMedication(this.medication);
      observable.subscribe({
        next: () => {
          this.notification.success('Successfully created ' + this.medication.name + ' Medication');
        },
        error: async error => {
          switch (error.status) {
            case 422:
              this.notification.error(this.errorFormatterService.format(JSON.parse(await error.error.text()).ValidationErrors), `Could not create Medication`, {
                enableHtml: true,
                timeOut: 10000
              });
              break;
            case 401:
              this.notification.error(await error.error.text(), `Could not create Medication`);
              this.router.navigate(['/']);
              break;
            default:
              this.notification.error(await error.error.text(), `Could not create Medication`);
              break;
          }
        }
      });
    }
  }
}
