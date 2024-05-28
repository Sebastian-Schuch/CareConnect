import {Component, OnInit} from '@angular/core';
import {MedicationCreateDto} from "../../dtos/medication";
import {ActivatedRoute, Router} from "@angular/router";
import {MedicationService} from "../../services/medication.service";
import {NgForm, NgModel} from "@angular/forms";
import {Observable} from "rxjs";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-medication-create',
  templateUrl: './medication-create.component.html',
  styleUrl: './medication-create.component.scss'
})
export class MedicationCreateComponent implements OnInit {

  medication: MedicationCreateDto = {
    name: ''
  };

  constructor(
    private service: MedicationService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService
    //private errorFormatter: ErrorFormatterService
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
      let observable: Observable<MedicationCreateDto>;
      observable = this.service.createMedication(this.medication);
      observable.subscribe({
        next: () => {
          this.notification.success('Successfully created ' + this.medication.name + ' Medication');
        },
        error: error => {
          this.notification.error('Error creating Medication');
          console.error('Error creating Medication', error);
        }
      });
    }
  }
}
