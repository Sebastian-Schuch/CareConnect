import {Component, EventEmitter, Input, OnInit, Output, TemplateRef, ViewChild} from '@angular/core';
import {AllergyDto, AllergyDtoCreate} from "../../../dtos/allergy";
import {AllergyService} from "../../../services/allergy.service";
import {NgForm, NgModel} from "@angular/forms";
import {Observable} from "rxjs";
import {ToastrService} from "ngx-toastr";
import {ActivatedRoute, Router} from "@angular/router";
import {ErrorFormatterService} from "../../../services/error-formatter.service";

export enum AllergyCreatEditMode {
  CREATE,
  EDIT
}

@Component({
  selector: 'app-allergy',
  templateUrl: './allergy-create-edit.component.html',
  styleUrls: ['./allergy-create-edit.component.scss', '../../../../styles.scss']
})
export class AllergyCreateEditComponent implements OnInit {

  @Output() creationSuccess = new EventEmitter<void>();

  @Input() mode: AllergyCreatEditMode = null;
  @Input() isEmbedded: boolean = false;

  @ViewChild('content', {static: true}) content!: TemplateRef<any>;

  public error: string | null = null;

  constructor(
    private allergyService: AllergyService,
    private notification: ToastrService,
    private errorFormatterService: ErrorFormatterService,
    private router: Router,
    private route: ActivatedRoute
  ) {
  }

  ngOnInit(): void {
    this.route.data.subscribe(data => {
      if (data.mode) {
        this.mode = data.mode;
      }
      if (this.mode === AllergyCreatEditMode.EDIT) {
        this.route.params.subscribe(params => {
          this.allergyService.getAllergyById(params.id).subscribe(allergy => {
            this.allergy = allergy;
          });
        });
      }
    });
  }

  allergy: AllergyDto = {
    id: 0,
    name: ''
  };

  public onSubmit(form: NgForm): void {
    if (form.valid) {
      if (this.mode === AllergyCreatEditMode.CREATE) {
        let observable: Observable<AllergyDtoCreate> = this.allergyService.createAllergy(this.allergy);

        observable.subscribe({
          next: data => {
            this.notification.success('Successfully created ' + data.name + ' Allergy');
            if (!this.isEmbedded) {
              this.router.navigate(['/home/admin/allergies'])
            }
            this.onSuccessfulCreation();
          },
          error: async error => {
            await this.errorFormatterService.printErrorToNotification(error, `Could not create Allergy`, this.notification);
          }
        });
      } else if (this.mode === AllergyCreatEditMode.EDIT) {
        let observable: Observable<AllergyDtoCreate> = this.allergyService.updateAllergy(this.allergy);

        observable.subscribe({
          next: data => {
            this.notification.success('Successfully updated ' + data.name + ' Allergy');
          },
          error: async error => {
            await this.errorFormatterService.printErrorToNotification(error, `Could not update Allergy`, this.notification);
          }
        });
      }
    }
  }

  public dynamicCssClassesForInput(input: NgModel): any {
    return {
      'is-invalid': !input.valid && !input.pristine,
    };
  }

  private onSuccessfulCreation() {
    this.creationSuccess.emit();
  }

  protected readonly AllergyCreatEditMode = AllergyCreatEditMode;
}
