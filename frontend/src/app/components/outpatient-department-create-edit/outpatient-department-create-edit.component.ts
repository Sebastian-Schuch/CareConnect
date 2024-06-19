import {Component, OnInit} from '@angular/core';
import {OutpatientDepartmentDto, OutpatientDepartmentDtoCreate} from "../../dtos/outpatient-department";
import {OutpatientDepartmentService} from "../../services/outpatient-department.service";
import {ToastrService} from "ngx-toastr";
import {ActivatedRoute, Router} from "@angular/router";
import {ErrorFormatterService} from "../../services/error-formatter.service";
import {
  OutpatientDepartmentDeleteComponent
} from "../outpatient-department-delete/outpatient-department-delete.component";
import {InpatientDepartmentCreateEditMode} from "../inpatient-department/inpatient-department.component";
import {OpeningHoursDayDto, OpeningHoursDto, OpeningHoursDtoCreate} from "../../dtos/opening-hours";
import {observable, Observable} from "rxjs";

export enum OutpatientDepartmentCreateEditMode {
  create,
  edit
}

@Component({
  selector: 'app-outpatient-department-create-edit',
  templateUrl: './outpatient-department.component-create-edit.html',
  styleUrls: ['./outpatient-department.component-create-edit.scss', '../../../styles.scss']
})
export class OutpatientDepartmentComponent implements OnInit {

  constructor(
    private outpatientDepartmentService: OutpatientDepartmentService,
    private notification: ToastrService,
    private errorFormatterService: ErrorFormatterService,
    private route: ActivatedRoute,
    private router: Router
  ) {
  }

  mode: OutpatientDepartmentCreateEditMode = OutpatientDepartmentCreateEditMode.create;
  id: number;
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

  ngOnInit() {
    this.route.data.subscribe(data => {
      this.route.params.subscribe(params => {
        this.mode = data.mode;
        //mode is not edit
        if (this.mode != OutpatientDepartmentCreateEditMode.edit) {
          return;
        }
        //id to edit is not valid
        if (isNaN(params['id'])) {
          this.router.navigate(['/home/admin/outpatient-department']);
        }
        this.outpatientDepartmentService.getOutpatientDepartmentById(params['id']).subscribe({
          next: data => {
            this.outpatientDepartment = this.mapExitingOutpatientDepartment(data);
            this.id = data.id;
            console.log(this.id);
            console.log(this.outpatientDepartment);
          },
          error: error => console.error(error)
        })
      })
    });
  }

  mapExitingOutpatientDepartment(existingOutpatientDepartment: OutpatientDepartmentDto): OutpatientDepartmentDtoCreate {
    return {
      name: existingOutpatientDepartment.name,
      description: existingOutpatientDepartment.description,
      capacity: existingOutpatientDepartment.capacity,
      openingHours: {
        monday: this.mapExistingOpeningHour(existingOutpatientDepartment.openingHours.monday),
        tuesday: this.mapExistingOpeningHour(existingOutpatientDepartment.openingHours.tuesday),
        wednesday: this.mapExistingOpeningHour(existingOutpatientDepartment.openingHours.wednesday),
        thursday: this.mapExistingOpeningHour(existingOutpatientDepartment.openingHours.thursday),
        friday: this.mapExistingOpeningHour(existingOutpatientDepartment.openingHours.friday),
        saturday: this.mapExistingOpeningHour(existingOutpatientDepartment.openingHours.saturday),
        sunday: this.mapExistingOpeningHour(existingOutpatientDepartment.openingHours.sunday),
      }
    }
  }

  mapExistingOpeningHour(existingOpeningHour: OpeningHoursDayDto): OpeningHoursDayDto {
    return {isClosed: existingOpeningHour === null ? true : existingOpeningHour.isClosed,
      open: existingOpeningHour === null ? undefined : existingOpeningHour.open,
      close: existingOpeningHour === null ? undefined : existingOpeningHour.close}
  }

  mapIdAndCreateDtoToDto(id: number, outpatientDtoCreate: OutpatientDepartmentDtoCreate): OutpatientDepartmentDto {
    return {
      id: id,
      name: outpatientDtoCreate.name,
      description: outpatientDtoCreate.description,
      capacity: outpatientDtoCreate.capacity,
      active: true,
      openingHours: this.mapIdAndOpeningHoursDtoCreate(id, outpatientDtoCreate.openingHours),
    }
  }

  mapIdAndOpeningHoursDtoCreate(id: number, openingHoursDtoCreate: OpeningHoursDtoCreate): OpeningHoursDto{
    return {
      id: id,
      monday: openingHoursDtoCreate.monday,
      tuesday: openingHoursDtoCreate.tuesday,
      wednesday: openingHoursDtoCreate.wednesday,
      thursday: openingHoursDtoCreate.thursday,
      friday: openingHoursDtoCreate.friday,
      saturday: openingHoursDtoCreate.saturday,
      sunday: openingHoursDtoCreate.sunday
    }
  }

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
      if (!hours.isClosed && (!hours.open || !hours.close)) {
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
    let observable: Observable<OutpatientDepartmentDto>;
    if(this.mode === OutpatientDepartmentCreateEditMode.create) {
      observable = this.outpatientDepartmentService.createOutpatientDepartment(this.outpatientDepartment);
    } else if(this.mode === OutpatientDepartmentCreateEditMode.edit) {
      observable = this.outpatientDepartmentService.editOutpatientDepartment(this.mapIdAndCreateDtoToDto(this.id, this.outpatientDepartment));
    }
    observable.subscribe({
      next: data => {
        this.notification.success('Outpatient Department ' + data.name + (this.mode === OutpatientDepartmentCreateEditMode.create ? " registered" : " edited"));
        this.router.navigate(['/home/admin/outpatient-department']);
      },
      error: async error => {
        await this.errorFormatterService.printErrorToNotification(error, `Could not ${this.mode === OutpatientDepartmentCreateEditMode.create ? " registered" : " edited"} Outpatient Department`, this.notification);
      }
    })
  }

  protected readonly OutpatientDepartmentCreateEditMode = OutpatientDepartmentCreateEditMode;
}
