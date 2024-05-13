import {Component, OnInit} from '@angular/core';
import {AllergyCreateDto} from "../../dtos/allergy";
import {AllergyService} from "../../services/allergy.service";
import {FormControl, NgForm, NgModel, Validators} from "@angular/forms";
import {Observable} from "rxjs";

@Component({
  selector: 'app-allergy',
  templateUrl: './allergy.component.html',
  styleUrl: './allergy.component.scss'
})
export class AllergyComponent implements OnInit {

  constructor(
    private allergyService: AllergyService
  ) { }

  ngOnInit(): void {
  }

  allergyname: string = '';

  public onSubmit(form: NgForm): void {
    console.log('is form valid?', form.valid, this.allergyname);
    if (form.valid) {
      const allergy: AllergyCreateDto = {
        name: this.allergyname
      };
      let observable: Observable<AllergyCreateDto> = this.allergyService.createAllergy(allergy);

      observable.subscribe({
        next: () => {
          //this.router.navigate(['/dashboard']);
        },
        error: error => {
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
