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
import {MatDialog} from "@angular/material/dialog";
import {
  InpatientDepartmentDeleteComponent
} from "../inpatient-department/inpatient-department-delete/inpatient-department-delete.component";
import {
  OutpatientDepartmentComponent
} from "../outpatient-department-create-edit/outpatient-department-create-edit.component";
import {
  OutpatientDepartmentDeleteComponent
} from "../outpatient-department-delete/outpatient-department-delete.component";
import {ToastrService} from "ngx-toastr";
import {RouterLink} from "@angular/router";

@Component({
  selector: 'app-outpatient-department-list',
  standalone: true,
  imports: [
    FormsModule,
    MatIcon,
    MatPaginator,
    NgForOf,
    NgIf,
    RouterLink
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
    private notification: ToastrService,
    public dialog: MatDialog
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
    const dialogRef = this.dialog.open(OutpatientDepartmentDeleteComponent,
      {data: {name: outpatientDepartment.name}});

    dialogRef.afterClosed().subscribe({
      next: value => {
        if (value) {
          this.deleteInpatientDepartment(outpatientDepartment.id);
          if (this.outpatientDepartments.length === 1) {
            this.pageProperties = {
              pageIndex: this.pageProperties.pageIndex - 1,
              pageSize: this.pageProperties.pageSize
            };
            this.reloadOutpatientDepartments();
          }
        }
      },
      error: err => console.error(err)
    })
  }

  onPageChange($event: PageEvent) {
    this.pageProperties = {pageIndex: $event.pageIndex, pageSize: $event.pageSize};
    this.reloadOutpatientDepartments();
  }

  private deleteInpatientDepartment(id: number) {
    this.outpatientDepartmentService.deleteInpatientDepartment(id).subscribe({
      next: value => {
        this.notification.success(`Successfully deleted inpatient department ${value.name}`);
        this.reloadOutpatientDepartments();
      },
      error: err => console.error(err)
    })
  }
}
