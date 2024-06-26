import {Component, OnInit} from '@angular/core';

import {ActivatedRoute, Router} from "@angular/router";
import {AuthService} from "../../../services/auth.service";
import {OutpatientDepartmentService} from "../../../services/outpatient-department.service";
import {OutpatientDepartmentDto} from "../../../dtos/outpatient-department";
import {Role} from "../../../dtos/Role";
import {OpeningHoursDayDto} from "../../../dtos/opening-hours";


@Component({
  selector: 'app-outpatient-department-detail',
  templateUrl: './outpatient-department-detail.component.html',
  styleUrls: ['./outpatient-department-detail.component.scss', '../../../../styles.scss']
})
export class OutpatientDepartmentDetailComponent implements OnInit {

  constructor(private outpatientDepartmentService: OutpatientDepartmentService,
              private route: ActivatedRoute,
              private authService: AuthService,
              private router: Router) {
  }

  role: Role;
  days: string[] = ['monday', 'tuesday', 'wednesday', 'thursday', 'friday', 'saturday', 'sunday'];
  outpatientDepartment: OutpatientDepartmentDto = {
    id: 0,
    name: '',
    description: '',
    capacity: 0,
    active: true,
    openingHours: {
      id: 0,
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

  ngOnInit() {
    this.role = this.authService.getUserRole();
    this.route.params.subscribe(params => {
      if (isNaN(params['id'])) {
        this.router.navigate(['/home/admin/outpatient-department']);
      }
      this.outpatientDepartmentService.getOutpatientDepartmentById(params['id']).subscribe((outpatientDepartment: OutpatientDepartmentDto) => {
        this.outpatientDepartment = this.mapOutpatientDepartment(outpatientDepartment);
      });
    });
  }

  mapOutpatientDepartment(outpatientDepartment: OutpatientDepartmentDto): OutpatientDepartmentDto {
    return {
      id: outpatientDepartment.id,
      active: true,
      name: outpatientDepartment.name,
      description: outpatientDepartment.description,
      capacity: outpatientDepartment.capacity,
      openingHours: {
        id: outpatientDepartment.openingHours.id,
        monday: this.mapOpeningHour(outpatientDepartment.openingHours.monday),
        tuesday: this.mapOpeningHour(outpatientDepartment.openingHours.tuesday),
        wednesday: this.mapOpeningHour(outpatientDepartment.openingHours.wednesday),
        thursday: this.mapOpeningHour(outpatientDepartment.openingHours.thursday),
        friday: this.mapOpeningHour(outpatientDepartment.openingHours.friday),
        saturday: this.mapOpeningHour(outpatientDepartment.openingHours.saturday),
        sunday: this.mapOpeningHour(outpatientDepartment.openingHours.sunday)
      }
    }
  }

  mapOpeningHour(existingOpeningHour: OpeningHoursDayDto): OpeningHoursDayDto {
    return {
      isClosed: existingOpeningHour === null ? true : existingOpeningHour.isClosed,
      open: existingOpeningHour === null ? undefined : existingOpeningHour.open,
      close: existingOpeningHour === null ? undefined : existingOpeningHour.close
    }
  }

  protected readonly Role = Role;
}
