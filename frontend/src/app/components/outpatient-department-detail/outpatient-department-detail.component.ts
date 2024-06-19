import {Component, Input, OnInit} from '@angular/core';
import {OutpatientDepartmentDto} from "../../dtos/outpatient-department";
import {OutpatientDepartmentService} from "../../services/outpatient-department.service";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-outpatient-department-detail',
  templateUrl: './outpatient-department-detail.component.html',
  styleUrl: './outpatient-department-detail.component.scss'
})
export class OutpatientDepartmentDetailComponent implements OnInit {

  constructor(private outpatientDepartmentService: OutpatientDepartmentService,
              private route: ActivatedRoute,
              private router: Router) {
  }


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
    this.route.params.subscribe(params => {
      if (isNaN(params['id'])) {
        this.router.navigate(['/home/admin/outpatient-department']);
      }
      this.outpatientDepartmentService.getOutpatientDepartmentById(params['id']).subscribe((outpatientDepartment: OutpatientDepartmentDto) => {
        this.outpatientDepartment = outpatientDepartment;
      });
    });
  }
}
