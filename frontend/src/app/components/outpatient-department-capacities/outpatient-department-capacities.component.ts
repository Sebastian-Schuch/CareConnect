import { Component, OnInit } from '@angular/core';
import { OutpatientDepartmentService } from '../../services/outpatient-department.service';
import { Chart, registerables } from 'chart.js';
import { FormControl } from '@angular/forms';
import { A11yModule } from '@angular/cdk/a11y';

Chart.register(...registerables);

@Component({
  selector: 'app-outpatient-department-capacities',
  templateUrl: './outpatient-department-capacities.component.html',
  styleUrls: ['./outpatient-department-capacities.component.scss']
})
export class OutpatientDepartmentCapacitiesComponent implements OnInit {
  departments: any[] = [];
  view = new FormControl('day');
  currentDate: Date;
  dateFormat: string = 'mediumDate';

  constructor(private outpatientDepartmentService: OutpatientDepartmentService) {}

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
}
