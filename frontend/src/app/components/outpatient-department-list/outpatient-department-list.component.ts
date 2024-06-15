import {Component, OnInit} from '@angular/core';
import {FormsModule} from "@angular/forms";
import {MatIcon} from "@angular/material/icon";
import {MatPaginator, PageEvent} from "@angular/material/paginator";
import {NgForOf, NgIf} from "@angular/common";
import {AuthService} from "../../services/auth.service";
import {Role} from "../../dtos/Role";
import {OutpatientDepartmentDto} from "../../dtos/outpatient-department";
import {OpeningHoursDayDto, OpeningHoursDto} from "../../dtos/opening-hours";
import {OutpatientDepartmentService} from "../../services/outpatient-department.service";

@Component({
  selector: 'app-outpatient-department-list',
  standalone: true,
    imports: [
        FormsModule,
        MatIcon,
        MatPaginator,
        NgForOf,
        NgIf
    ],
  templateUrl: './outpatient-department-list.component.html',
  styleUrl: './outpatient-department-list.component.scss'
})
export class OutpatientDepartmentListComponent implements OnInit{
  role: Role;
  searchedName: string = "";
  outpatientDepartments: OutpatientDepartmentDto[];
  pageProperties: { pageIndex: number, pageSize: number } = {pageIndex: 0, pageSize: 10};
  totalItems: number = 0;

  constructor(
    private authService: AuthService,
    private outpatientDepartmentService: OutpatientDepartmentService,
  ) {
  }

  ngOnInit() {
    this.role = this.authService.getUserRole();
    this.reloadOutpatientDepartments();
  }

  reloadOutpatientDepartments() {
    this.outpatientDepartmentService.getOutpatientDepartmentPage(this.searchedName, this.pageProperties.pageIndex, this.pageProperties.pageSize).subscribe({
      next: result => {
        this.outpatientDepartments = result.outpatientDepartments;
        this.totalItems = result.totalItems;
      },
      error: err => console.error(err)
    })
  }

  protected readonly Role = Role;

  openDeleteDialog(outpatientDepartment: OutpatientDepartmentDto) {

  }

  onPageChange($event: PageEvent) {
    this.pageProperties = {pageIndex: $event.pageIndex, pageSize: $event.pageSize};
    this.reloadOutpatientDepartments();
  }
}
