import {Component, EventEmitter, Output} from '@angular/core';
import {FormsModule, NgForm, NgModel, ReactiveFormsModule} from "@angular/forms";
import {AllergyDtoCreate} from "../../../dtos/allergy";
import {Observable} from "rxjs";
import {AllergyService} from "../../../services/allergy.service";
import {ToastrService} from "ngx-toastr";
import {ErrorFormatterService} from "../../../services/error-formatter.service";
import {Router} from "@angular/router";
import {CsvConverterService} from "../../../services/csv-converter.service";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";

@Component({
  selector: 'app-add-allergy-manual',
  templateUrl: './add-allergy-manual.component.html',
  styleUrl: './add-allergy-manual.component.scss'
})
export class AddAllergyManualComponent {
  @Output() creationSuccess = new EventEmitter<void>();
  allergyname: string = '';

  constructor(
    private allergyService: AllergyService,
    private notification: ToastrService,
    private errorFormatterService: ErrorFormatterService,
    private router: Router,
    private csvService: CsvConverterService,
    private modalService: NgbModal
  ) {
  }

  public dynamicCssClassesForInput(input: NgModel): any {
    return {
      'is-invalid': !input.valid && !input.pristine,
    };
  }

  public onSubmit(form: NgForm): void {
    if (form.valid) {
      const allergy: AllergyDtoCreate = {
        name: this.allergyname
      };
      let observable: Observable<AllergyDtoCreate> = this.allergyService.createAllergy(allergy);

      observable.subscribe({
        next: data => {
          this.notification.success('Successfully created ' + data.name + ' Allergy');
          this.onSuccessfulCreation();
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
              //this.router.navigate(['/']);
              break;
            case 409:
              this.notification.error('Allergy already exists', `Could not create Allergy`);
              break;
            default:
              this.notification.error(await error.error.text(), `Could not create Allergy`);
              break;
          }
        }
      });
    }
  }

  private onSuccessfulCreation(): void {
    this.creationSuccess.emit();
  }

}
