import {Component, EventEmitter, input, Input, OnInit, Output} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {NgForm, NgModel} from "@angular/forms";
import {Observable} from "rxjs";
import {ToastrService} from "ngx-toastr";
import {ErrorFormatterService} from "../../../services/error-formatter.service";
import {MedicationDto, MedicationDtoCreate} from "../../../dtos/medication";
import {MedicationService} from "../../../services/medication.service";

export enum MedicationCreateEditMode {
  CREATE,
  EDIT
}

@Component({
  selector: 'app-medication-create',
  templateUrl: './medication-create.component.html',
  styleUrls: ['./medication-create.component.scss', '../../../../styles.scss']
})
export class MedicationCreateComponent implements OnInit {
  @Output() creationSuccess = new EventEmitter<void>();

  medication: MedicationDto = {
    id: 0,
    name: '',
    unitOfMeasurement: ''
  };

  @Input() mode: MedicationCreateEditMode = null;
  @Input() isEmbedded: boolean = false;

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
      if(data.mode) {
        this.mode = data.mode;
      }

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
            this.onSuccessfulCreation();
            if(!this.isEmbedded) {
              this.router.navigate(['/home/admin/medications'])
            }
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

  private onSuccessfulCreation(): void {
    this.creationSuccess.emit();
  }
}
