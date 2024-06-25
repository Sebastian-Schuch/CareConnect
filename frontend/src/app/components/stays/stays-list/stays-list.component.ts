import {Component, OnInit, ViewChild} from '@angular/core';
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef,
  MatHeaderRow,
  MatHeaderRowDef,
  MatRow,
  MatRowDef,
  MatTable
} from "@angular/material/table";
import {MatPaginator, PageEvent} from "@angular/material/paginator";
import {StaysService} from "../../../services/stays.service";
import {StayDto} from "../../../dtos/stays";
import {ActivatedRoute} from "@angular/router";
import {DatePipe, NgIf} from "@angular/common";
import {StaysManageComponent} from "../stays-manage/stays-manage.component";
import {MatButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {MatDialog} from "@angular/material/dialog";
import {StaysEditComponent} from "../stays-edit/stays-edit.component";
import {UserService} from "../../../services/user.service";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-stays-manage-list',
  standalone: true,
  imports: [
    MatTable,
    MatHeaderCell,
    MatCell,
    MatColumnDef,
    MatHeaderRow,
    MatRow,
    MatPaginator,
    MatHeaderCellDef,
    MatCellDef,
    MatHeaderRowDef,
    MatRowDef,
    DatePipe,
    StaysManageComponent,
    MatButton,
    MatIcon,
    NgIf
  ],
  templateUrl: './stays-list.component.html',
  styleUrl: './stays-list.component.scss'
})
export class StaysListComponent implements OnInit {
  @ViewChild(MatPaginator) paginator: MatPaginator;

  displayedColumns: string[] = ['inpatient_department', 'check_in_time', 'discharge_time', 'edit'];

  constructor(private staysService: StaysService, private route: ActivatedRoute, private dialog: MatDialog, private userService: UserService, private notificationService: ToastrService) {
  }

  dataSource: StayDto[];
  patientId: number;
  length: number = 0;
  firstname: string;
  lastname: string;

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.patientId = params['id'];
      let observable = this.userService.getPatientById(this.patientId);
      observable.subscribe({
        next: user => {
          this.firstname = user.firstname;
          this.lastname = user.lastname;
          this.loadStays();
        },
        error: () => {
          this.notificationService.error("Error loading patient");
        }
      });
    });
  }

  formatDate(date: string): Date {
    return new Date(date);
  }

  openEditStayDialog(stay: StayDto) {
    const dialogRef = this.dialog.open(StaysEditComponent, {
      data: stay
    });

    dialogRef.afterClosed().subscribe(result => {
      this.loadStays();
    });
  }

  loadStays(event?: PageEvent) {
    const pageIndex = event ? event.pageIndex : 0;
    const pageSize = event ? event.pageSize : 5;

    this.staysService.getAllStays(this.patientId, pageIndex, pageSize).subscribe(stays => {
      this.dataSource = stays.content;
      this.length = stays.totalElements;
    });

    return event;
  }
}
