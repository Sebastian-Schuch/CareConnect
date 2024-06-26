import {Component, OnInit} from '@angular/core';
import {Chart, registerables} from 'chart.js';
import {FormControl} from '@angular/forms';
import {addDays, getDate, getMonth, getYear} from "date-fns";
import {MatDialog} from "@angular/material/dialog";
import {
  OutpatientDepartmentCapacitiesOpeningHoursModalComponent
} from "../outpatient-department-capacities-opening-hours-modal/outpatient-department-capacities-opening-hours-modal.component";
import {AuthService} from "../../../services/auth.service";
import {OutpatientDepartmentService} from "../../../services/outpatient-department.service";
import {Role} from "../../../dtos/Role";

Chart.register(...registerables);

@Component({
  selector: 'app-outpatient-department-capacities',
  templateUrl: './outpatient-department-capacities.component.html',
  styleUrls: ['./outpatient-department-capacities.component.scss', '../../../../styles.scss']
})
export class OutpatientDepartmentCapacitiesComponent implements OnInit {
  departments: any[] = [];
  view = new FormControl('day');
  currentDate: Date;
  dateFormat: string = 'mediumDate';
  disableFocus: boolean = false;

  constructor(private outpatientDepartmentService: OutpatientDepartmentService, public dialog: MatDialog, private authService: AuthService) {
  }

  ngOnInit(): void {
    this.currentDate = new Date();
    this.loadCapacities();
  }

  loadCapacities(): void {
    const formattedDate = this.currentDate.toISOString().split('T')[0];

    if (this.view.value === 'day') {
      this.outpatientDepartmentService.getOutpatientDepartmentCapacitiesForDay(formattedDate).subscribe((data) => {
        this.departments = data;
      });
    } else if (this.view.value === 'week') {
      this.outpatientDepartmentService.getOutpatientDepartmentCapacitiesForWeek(formattedDate).subscribe((data) => {
        this.departments = data;
      });
    } else if (this.view.value === 'month') {
      this.outpatientDepartmentService.getOutpatientDepartmentCapacitiesForMonth(formattedDate).subscribe((data) => {
        this.departments = data;
      });
    }
  }

  resetDate(): void {
    this.currentDate = new Date();
    this.dateFormat = this.view.value === 'day' ? 'fullDate' : this.view.value === 'week' ? 'shortDate' : 'MMM yyyy';
    this.loadCapacities();
  }

  showDate(currentDate: Date) {
    return getDate(currentDate) + "/" + (getMonth(currentDate) + 1) + "/" + getYear(currentDate);
  }

  showDateWeek(currentDate: Date) {
    return this.showDate(currentDate) + " - " + this.showDate(addDays(currentDate, 6));
  }

  openOpeningHours(department: any): void {
    this.disableFocus = true;
    const open = this.dialog.open(OutpatientDepartmentCapacitiesOpeningHoursModalComponent,
      {width: '500px', data: {outpatient: department}});

    open.componentInstance.dialogRef.afterClosed().subscribe(() => {
      this.disableFocus = false;
    });
  }

  previous(): void {
    if (this.view.value === 'day') {
      this.currentDate = new Date(this.currentDate.setDate(this.currentDate.getDate() - 1));
    } else if (this.view.value === 'week') {
      this.currentDate = new Date(this.currentDate.setDate(this.currentDate.getDate() - 7));
    } else if (this.view.value === 'month') {
      this.currentDate = new Date(this.currentDate.setMonth(this.currentDate.getMonth() - 1));
    }
    this.loadCapacities();
  }

  next(): void {
    if (this.view.value === 'day') {
      this.currentDate = new Date(this.currentDate.setDate(this.currentDate.getDate() + 1));
    } else if (this.view.value === 'week') {
      this.currentDate = new Date(this.currentDate.setDate(this.currentDate.getDate() + 7));
    } else if (this.view.value === 'month') {
      this.currentDate = new Date(this.currentDate.setMonth(this.currentDate.getMonth() + 1));
    }
    this.loadCapacities();
  }

  get roleIsPatient(): boolean {
    return this.authService.getUserRole() === Role.patient;
  }
}
