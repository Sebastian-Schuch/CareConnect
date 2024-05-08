import { Component } from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {OutpatientDepartmentDtoCreate} from "../../dtos/outpatient-department";
import {NgForOf} from "@angular/common";
import {OutpatientDepartmentService} from "../../services/outpatient-department.service";

@Component({
  selector: 'app-outpatient-department',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    FormsModule,
    NgForOf
  ],
  templateUrl: './outpatient-department.component.html',
  styleUrl: './outpatient-department.component.scss'
})
export class OutpatientDepartmentComponent {

  constructor(
    private outpatientDepartmentService: OutpatientDepartmentService,
  ) {
  }

  days: string[] = ['monday', 'tuesday', 'wednesday', 'thursday', 'friday', 'saturday', 'sunday'];
  outpatientDepartment: OutpatientDepartmentDtoCreate = {
    name: '',
    description: '',
    capacity: 0,
    openingHours: {
      monday: {
        open: undefined,
        close: undefined
      },
      tuesday: {
        open: undefined,
        close: undefined
      },
      wednesday: {
        open: undefined,
        close: undefined
      },
      thursday: {
        open: undefined,
        close: undefined
      },
      friday: {
        open: undefined,
        close: undefined
      },
      saturday: {
        open: undefined,
        close: undefined
      },
      sunday: {
        open: undefined,
        close: undefined
      }
    }
  };

  isFormValid(): boolean {
    if (!this.outpatientDepartment.name || this.outpatientDepartment.name.length > 255 || this.outpatientDepartment.name.length<1) {
      return false;
    }
    if(!this.outpatientDepartment.description || this.outpatientDepartment.description.length > 1000 || this.outpatientDepartment.description.length<1){
      return false;
    }
    if(this.outpatientDepartment.capacity < 0){
      return false;
    }
    if(this.outpatientDepartment.capacity > 0 || this.outpatientDepartment.capacity <= 100){
      return false;
    }
    for (let day of this.days) {
      let hours = this.outpatientDepartment.openingHours[day];
      if (!hours.closed && (!hours.open || !hours.close)) {
        return false;
      }
    }
    return true;
  }
  onSubmit(): void {
    for (let day of this.days) {
      let hours = this.outpatientDepartment.openingHours[day];

      if (hours.closed) {
        this.outpatientDepartment.openingHours[day] = {
          open: null,
          close: null};
      } else{
        this.outpatientDepartment.openingHours[day] = {
          open: hours.open,
          close: hours.close};
      }
    }
    console.log(this.outpatientDepartment);
    this.outpatientDepartmentService.createOutpatientDepartment(this.outpatientDepartment).subscribe({
      next: data => {
        console.log(data);
      },
      error: error => {
        console.error('There was an error!', error);
      }
    })
  }

}
