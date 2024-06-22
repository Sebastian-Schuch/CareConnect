import {Component, OnInit} from '@angular/core';
import {AllergyDto, AllergyDtoCreate} from "../../dtos/allergy";
import {AllergyService} from "../../services/allergy.service";
import {NgForm, NgModel} from "@angular/forms";
import {Observable} from "rxjs";
import {ToastrService} from "ngx-toastr";
import {ActivatedRoute, Router} from "@angular/router";
import {ErrorFormatterService} from "../../services/error-formatter.service";

export enum AllergyCreatEditMode {
  create,
  edit

}

@Component({
  selector: 'app-allergy',
  templateUrl: './allergy.component.html',
  styleUrls: ['./allergy.component.scss', '../../../styles.scss']
})
export class AllergyComponent implements OnInit {

  constructor(
    private allergyService: AllergyService,
    private notification: ToastrService,
    private errorFormatterService: ErrorFormatterService,
    private router: Router,
    private route: ActivatedRoute
  ) {
  }

  mode: AllergyCreatEditMode = null;

  ngOnInit(): void {
    this.route.data.subscribe(data => {
      this.mode = data.mode;
      if (this.mode === AllergyCreatEditMode.edit) {
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
      if (this.mode === AllergyCreatEditMode.create) {
        let observable: Observable<AllergyDtoCreate> = this.allergyService.createAllergy(this.allergy);

        observable.subscribe({
          next: data => {
            this.notification.success('Successfully created ' + data.name + ' Allergy');
          },
          error: async error => {
            await this.errorFormatterService.printErrorToNotification(error, `Could not create Allergy`, this.notification);
          }
        });
      } else if (this.mode === AllergyCreatEditMode.edit) {
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

  protected readonly AllergyCreatEditMode = AllergyCreatEditMode;
}
