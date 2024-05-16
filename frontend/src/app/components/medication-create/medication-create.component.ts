import {Component, OnInit} from '@angular/core';
import {MedicationCreateDto} from "../../dtos/medication";
import {ActivatedRoute, Router} from "@angular/router";
import {MedicationService} from "../../services/medication.service";
import {FormsModule, NgForm, NgModel} from "@angular/forms";
import {Observable} from "rxjs";

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
    private route: ActivatedRoute
    //private notification: ToastrService,
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
    console.log('is form valid?', form.valid, this.medication);
    if (form.valid) {
      let observable: Observable<MedicationCreateDto>;
      observable = this.service.createMedication(this.medication);
      observable.subscribe({
        next: () => {
          //this.notification.success(`Horse ${this.horse.name} successfully ${this.modeActionFinished}.`);
          //this.router.navigate(['/horses']);
        },
        error: error => {
          console.error('Error creating Medication', error);
          //this.notification.error(this.errorFormatter.format(error), "Could not create Horse", {
          //enableHtml: true,
          //timeOut: 10000,
          //})
        }
      });
    }
  }

}
