import {Component, OnInit} from '@angular/core';
import {AllergyCreateDto} from "../../dtos/allergy";
import {AllergyService} from "../../services/allergy.service";
import {NgForm, NgModel} from "@angular/forms";
import {Observable} from "rxjs";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-allergy',
  templateUrl: './allergy.component.html',
  styleUrl: './allergy.component.scss'
})
export class AllergyComponent implements OnInit {

  constructor(
    private allergyService: AllergyService,
    private notification: ToastrService
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
        error: error => {
          this.notification.error('Error creating Allergy');
          console.error('Error creating User', error);
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
