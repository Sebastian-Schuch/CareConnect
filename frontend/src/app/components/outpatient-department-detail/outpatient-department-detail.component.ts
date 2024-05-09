import {Component, Input, OnInit} from '@angular/core';
import {OutpatientDepartmentDto} from "../../dtos/outpatient-department";
import {NgForOf, NgIf} from "@angular/common";
import {OutpatientDepartmentService} from "../../services/outpatient-department.service";

@Component({
  selector: 'app-outpatient-department-detail',
  standalone: true,
  imports: [
    NgForOf,
    NgIf
  ],
  templateUrl: './outpatient-department-detail.component.html',
  styleUrl: './outpatient-department-detail.component.scss'
})
export class OutpatientDepartmentDetailComponent implements OnInit {
    @Input() id: number;
    constructor(private outpatientDepartmentService: OutpatientDepartmentService) {
    }


    days: string[] = ['monday', 'tuesday', 'wednesday', 'thursday', 'friday', 'saturday', 'sunday'];
    outpatientDepartment: OutpatientDepartmentDto = {
      id:0,
      name: '',
      description: '',
      capacity: 0,
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
      this.outpatientDepartmentService.getOutpatientDepartmentById(this.id).subscribe((outpatientDepartment: OutpatientDepartmentDto) => {
        this.outpatientDepartment = outpatientDepartment;
      });
    }
}
