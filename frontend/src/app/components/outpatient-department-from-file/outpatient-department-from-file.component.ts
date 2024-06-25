import {Component, ViewEncapsulation} from '@angular/core';
import {CsvConverterService} from "../../services/csv-converter.service";
import {ToastrService} from "ngx-toastr";
import {OutpatientDepartmentService} from "../../services/outpatient-department.service";
import {OutpatientDepartmentDto} from "../../dtos/outpatient-department";
import {forkJoin, map} from "rxjs";

@Component({
  selector: 'app-outpatient-department-from-file',
  templateUrl: './outpatient-department-from-file.component.html',
  styleUrl: './outpatient-department-from-file.component.scss'
})
export class OutpatientDepartmentFromFileComponent {
  constructor(
    private csvConverterService: CsvConverterService,
    private notification: ToastrService,
    private outPatientDepartmentService: OutpatientDepartmentService

  ) { }

  public jsonData: any;
  public error: string | null = null;

  onFileChange(event: any) {
    const file: File = event.target.files[0];

    if (file) {
      this.csvConverterService.parsCsvToOutPatientDepartment(file).then(
        (data) => {
          this.jsonData = data.filter(i => i.name !== '');
          this.error = null;
          console.log("Parsed data:", this.jsonData);
        },
        (error) => {
          this.error = error.message;
          this.jsonData = null;
          this.notification.error('Error parsing CSV: ' + error.message);
        }
      );
    }
  }

  addAll() {
    const requests = this.jsonData.map(department =>
      this.outPatientDepartmentService.createOutpatientDepartment(department).pipe(
        map(response => response)  // Ensure we return the response for each request
      )
    );

    forkJoin(requests).subscribe(
      (responses) => {
        this.notification.success('All departments added successfully');
      },
      (error) => {
        this.notification.error('Error adding departments: ' + error.message);
      }
    );
  }
}
