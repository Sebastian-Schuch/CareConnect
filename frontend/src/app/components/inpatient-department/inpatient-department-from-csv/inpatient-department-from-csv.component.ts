import {Component} from '@angular/core';
import {CsvConverterService} from "../../../services/csv-converter.service";
import {ToastrService} from "ngx-toastr";
import {catchError, forkJoin, of} from "rxjs";
import {InpatientDepartmentDto} from "../../../dtos/inpatient-department";
import {InpatientDepartmentService} from "../../../services/inpatient-department.service";

function containsKey(obj: any, key: string): boolean {
  return obj && obj.hasOwnProperty(key);
}

@Component({
  selector: 'app-inpatient-department-from-csv',
  templateUrl: './inpatient-department-from-csv.component.html',
  styleUrl: './inpatient-department-from-csv.component.scss'
})
export class InpatientDepartmentFromCsvComponent {

  public jsonData: any;
  public error: string | null = null;

  public InpatientDepartments: InpatientDepartmentDto[] = [];

  constructor(
    private service: InpatientDepartmentService,
    private csvService: CsvConverterService,
    private notification: ToastrService,
  ) {
  }

  addAll() {
    if (!this.jsonData || this.jsonData.length === 0) {
      this.notification.error('No data to add.');
      return;
    }

    const requests = this.jsonData.map((item: InpatientDepartmentDto) =>
      this.service.createInpatientDepartment(item).pipe(
        catchError(error => {
          this.notification.error('Error creating inpatient department: ' + error.message);
          return of(null);  // Return an observable that resolves to null in case of error
        })
      )
    );

    forkJoin(requests).subscribe({
      next: (results) => {
        // @ts-ignore
        const allSuccessful = results.every(result => result !== null);
        if (allSuccessful) {
          this.notification.success('All inpatient departments were successfully added.');
        } else {
          this.notification.error('Some inpatient departments could not be added.');
        }
        this.jsonData = null;
      },
      error: error => {
        this.notification.error('Error adding inpatient departments: ' + error.message);
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
          this.jsonData = converted.filter(i => i.name !== ''
            && containsKey(i, 'capacity')
            && containsKey(i, 'name')
            && i.capacity !== undefined
            && i.name !== undefined
          );
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
