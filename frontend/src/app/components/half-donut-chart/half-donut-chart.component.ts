import {Component, Input, OnChanges} from '@angular/core';
import {ChartConfiguration, ChartData, ChartType} from 'chart.js';
import {BaseChartDirective} from "ng2-charts";


@Component({
  selector: 'app-half-donut-chart',
  templateUrl: './half-donut-chart.component.html',
  standalone: true,
  imports: [
    BaseChartDirective
  ],
  styleUrls: ['./half-donut-chart.component.scss']
})
export class HalfDonutChartComponent implements OnChanges {
  @Input() occupied!: number;
  @Input() capacity!: number;

  public doughnutChartLabels: string[] = ['Occupied', 'Available'];
  public doughnutChartData: ChartData<'doughnut', number[], string> = {
    labels: this.doughnutChartLabels,
    datasets: []
  };
  public doughnutChartType: ChartType = 'doughnut';
  public doughnutChartOptions: ChartConfiguration<'doughnut'>['options'] = {
    cutout: '50%',
    rotation: -90,
    circumference: 180,
    plugins: {
      legend: {
        display: false
      }
    }
  };

  ngOnChanges() {
    if (this.capacity == 0) {
      this.createChartForClosed();
    } else {
      this.createChart();
    }
  }

  private createChart() {
    this.doughnutChartData = {
      labels: this.doughnutChartLabels,
      datasets: [
        {
          data: [this.occupied, this.capacity - this.occupied],
          backgroundColor: ['#FF6384', '#2d815b'],
          hoverBackgroundColor: ['#FF6384', '#2d815b']
        }
      ]
    };
  }

  private createChartForClosed() {
    this.doughnutChartData = {
      labels: ['closed'],
      datasets: [
        {
          data: [1],
          backgroundColor: ['#aaa']
        }
      ],
    };
  }
}
