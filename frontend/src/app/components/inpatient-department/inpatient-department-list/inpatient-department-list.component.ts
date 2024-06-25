import {Component, OnInit} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatIcon} from "@angular/material/icon";
import {NgForOf, NgIf} from "@angular/common";
import {InpatientDepartmentDto} from "../../../dtos/inpatient-department";
import {InpatientDepartmentService} from "../../../services/inpatient-department.service";
import {RouterLink} from "@angular/router";
import {MatPaginator, PageEvent} from "@angular/material/paginator";
import {MatDialog} from "@angular/material/dialog";
import {InpatientDepartmentDeleteComponent} from "../inpatient-department-delete/inpatient-department-delete.component";
import {ToastrService} from "ngx-toastr";
import {AuthService} from "../../../services/auth.service";
import {Role} from "../../../dtos/Role";


@Component({
  selector: 'app-inpatient-department-list',
  standalone: true,
  imports: [
    FormsModule,
    MatIcon,
    NgForOf,
    NgIf,
    ReactiveFormsModule,
    RouterLink,
    MatPaginator
  ],
  templateUrl: './inpatient-department-list.component.html',
  styleUrl: './inpatient-department-list.component.scss'
})
export class InpatientDepartmentListComponent implements OnInit {
  inpatientDepartments: InpatientDepartmentDto[];
  pageProperties: { pageIndex: number, pageSize: number } = {pageIndex: 0, pageSize: 10};
  totalItems: number = 0;
  searchedName = "";
  role: Role;


  constructor(
    private inpatientDepartmentService: InpatientDepartmentService,
    private notification: ToastrService,
    private authService: AuthService,
    public dialog: MatDialog) {
  }

  ngOnInit() {
    this.role = this.authService.getUserRole();
    this.inpatientDepartmentService.getInpatientDepartments("", 0, 10).subscribe({
      next: value => {
        this.inpatientDepartments = value.inpatientDepartments;
        this.totalItems = value.totalItems;
      },
      error: err => console.error(err)
    })
  }

  reloadInpatientDepartments() {
    this.inpatientDepartmentService.getInpatientDepartments(this.searchedName, this.pageProperties.pageIndex, this.pageProperties.pageSize).subscribe({
      next: value => {
        this.inpatientDepartments = value.inpatientDepartments;
        console.log(this.inpatientDepartments);
        this.totalItems = value.totalItems;
      },
      error: err => console.error(err)
    })
  }

  onPageChange($event: PageEvent) {
    this.pageProperties = {pageIndex: $event.pageIndex, pageSize: $event.pageSize};
    this.reloadInpatientDepartments();
  }

  openDeleteDialog(inpatientDepartment: InpatientDepartmentDto) {
    const dialogRef = this.dialog.open(InpatientDepartmentDeleteComponent,
      {data: {name: inpatientDepartment.name}});

    dialogRef.afterClosed().subscribe({
      next: value => {
        if (value) {
          this.deleteInpatientDepartment(inpatientDepartment.id);
          if (this.inpatientDepartments.length === 1) {
            this.pageProperties = {
              pageIndex: this.pageProperties.pageIndex,
              pageSize: this.pageProperties.pageSize
            };
            this.reloadInpatientDepartments();
          }
        }
      },
      error: err => console.error(err)
    })
  }

  deleteInpatientDepartment(id: number) {
    this.inpatientDepartmentService.deleteInpatientDepartment(id).subscribe({
      next: value => {
        this.notification.success(`Successfully deleted inpatient department ${value.name}`);
        this.reloadInpatientDepartments();
      },
      error: err => console.error(err)
    })
  }

  protected readonly Role = Role;
}
