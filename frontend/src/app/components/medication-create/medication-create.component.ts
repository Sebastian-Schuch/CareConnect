import {Component, OnInit} from '@angular/core';
import {MedicationDto, MedicationDtoCreate} from "../../dtos/medication";
import {ActivatedRoute, Router} from "@angular/router";
import {MedicationService} from "../../services/medication.service";
import {NgForm, NgModel} from "@angular/forms";
import {Observable} from "rxjs";
import {ToastrService} from "ngx-toastr";
import {ErrorFormatterService} from "../../services/error-formatter.service";

export enum MedicationCreateEditMode {
  CREATE,
  EDIT
}

@Component({
  selector: 'app-medication-create',
  templateUrl: './medication-create.component.html',
  styleUrls: ['./medication-create.component.scss', '../../../styles.scss']
})
export class MedicationCreateComponent implements OnInit {

  medication: MedicationDto = {
    id: 0,
    name: '',
    unitOfMeasurement: ''
  };

  mode: MedicationCreateEditMode = null;

  constructor(
    private service: MedicationService,
    private router: Router,
    private notification: ToastrService,
    private errorFormatterService: ErrorFormatterService,
    private route: ActivatedRoute
  ) {
  }

  ngOnInit(): void {
    this.route.data.subscribe(data => {
      this.mode = data.mode;
      if (this.mode === MedicationCreateEditMode.EDIT) {
        this.route.params.subscribe(params => {
          this.service.getMedicationById(params.id).subscribe(medication => {
            this.medication = medication;
          });
        });
      }
    });
  };

  public dynamicCssClassesForInput(input: NgModel): any {
    return {
      'is-invalid': !input.valid && !input.pristine,
    };
  }

  public onSubmit(form: NgForm): void {
    if (form.valid) {
      if (this.mode === MedicationCreateEditMode.CREATE) {
        let observable: Observable<MedicationDtoCreate>;
        observable = this.service.createMedication(this.medication);
        observable.subscribe({
          next: () => {
            this.notification.success('Successfully created ' + this.medication.name + ' Medication');
            this.router.navigate(['/home/admin/medications'])
          },
          error: async error => {
            await this.errorFormatterService.printErrorToNotification(error, `Could not create Medication`, this.notification);
          }
        });
      } else if (this.mode === MedicationCreateEditMode.EDIT) {
        let observable: Observable<MedicationDto>;
        observable = this.service.updateMedication(this.medication);
        observable.subscribe({
          next: () => {
            this.notification.success('Successfully updated ' + this.medication.name + ' Medication');
          },
          error: async error => {
            await this.errorFormatterService.printErrorToNotification(error, `Could not update Medication`, this.notification);
          }
        })
      }
    }
  }

  protected readonly MedicationCreateEditMode = MedicationCreateEditMode;
}
