import { Component } from '@angular/core';
import {StationService} from "../../../services/station.service";
import {CsvConverterService} from "../../../services/csv-converter.service";
import {ToastrService} from "ngx-toastr";
import {StationDto} from "../../../dtos/Station";
import {catchError, forkJoin, of} from "rxjs";

@Component({
  selector: 'app-inpatient-department-from-csv',
  templateUrl: './inpatient-department-from-csv.component.html',
  styleUrl: './inpatient-department-from-csv.component.scss'
})
export class InpatientDepartmentFromCsvComponent {

  public jsonData: any;
  public error: string | null = null;

  public InpatientDepartments : StationDto[]  = [];

  constructor(
    private service: StationService,
    private csvService: CsvConverterService,
    private notification: ToastrService,
  ) {
  }

  addAll(){
    if (!this.jsonData || this.jsonData.length === 0) {
      this.notification.error('No data to add.');
      return;
    }

    const requests = this.jsonData.map((item: StationDto) =>
      this.service.createStation(item).pipe(
        catchError(error => {
          this.notification.error('Error creating station: ' + error.message);
          return of(null);  // Return an observable that resolves to null in case of error
        })
      )
    );

    forkJoin(requests).subscribe({
      next: (results) => {
        // @ts-ignore
        const allSuccessful = results.every(result => result !== null);
        if (allSuccessful) {
          this.notification.success('All stations were successfully added.');
        } else {
          this.notification.error('Some stations could not be added.');
        }
        this.jsonData = null;
      },
      error: error => {
        this.notification.error('Error adding stations: ' + error.message);
      }
    });
  }

  onFileChange(event: any) {
    const file: File = event.target.files[0];

    if (file) {
      this.csvService.parseCsv(file).then(
        (data) => {
          const converted = data.map(item => {
            return {
              name: item.name,
              capacity: item.capacity
            };
          })
          this.jsonData = converted.filter(i => i.name !== '');
          console.log("data", this.jsonData)
          this.error = null;
        },
        (error) => {
          this.error = error.message;
          this.jsonData = null;
        }
      );
    }
  }
}
