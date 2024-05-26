import {Component} from '@angular/core';
import {OutpatientDepartmentDtoCreate} from "../../dtos/outpatient-department";
import {OutpatientDepartmentService} from "../../services/outpatient-department.service";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-outpatient-department-create-edit',
  templateUrl: './outpatient-department.component-create-edit.html',
  styleUrl: './outpatient-department.component-create-edit.scss'
})
export class OutpatientDepartmentComponent {

  constructor(
    private outpatientDepartmentService: OutpatientDepartmentService,
    private notification: ToastrService
  ) {
  }

  days: string[] = ['monday', 'tuesday', 'wednesday', 'thursday', 'friday', 'saturday', 'sunday'];
  outpatientDepartment: OutpatientDepartmentDtoCreate = {
    name: '',
    description: '',
    capacity: 0,
    openingHours: {
      monday: {
        open: new Date("12:00"),
        close: new Date("16:00")
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
    if (!this.outpatientDepartment.name || this.outpatientDepartment.name.length > 255 || this.outpatientDepartment.name.length < 1) {
      return false;
    }
    if (!this.outpatientDepartment.description || this.outpatientDepartment.description.length > 1000 || this.outpatientDepartment.description.length < 1) {
      return false;
    }
    if (this.outpatientDepartment.capacity < 0 || this.outpatientDepartment.capacity > 1000) {
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
          close: null
        };
      } else {
        this.outpatientDepartment.openingHours[day] = {
          open: hours.open,
          close: hours.close
        };
      }
    }
    this.outpatientDepartmentService.createOutpatientDepartment(this.outpatientDepartment).subscribe({
      next: data => {
        this.notification.success('Outpatient Department ' + data.name + ' created');
      },
      error: error => {
        this.notification.error('Outpatient Department could not be created');
        console.error('There was an error!', error);
      }
    })
  }

}
