import {Component, EventEmitter, Output, ViewChild} from '@angular/core';
import {CsvConverterService} from "../../../../services/csv-converter.service";
import {ToastrService} from "ngx-toastr";
import {OutpatientDepartmentService} from "../../../../services/outpatient-department.service";
import {forkJoin, map} from "rxjs";

function containsKey(obj: any, key: string): boolean {
  return obj && obj.hasOwnProperty(key);
}

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
  ) {
  }

  @Output() reloadEvent = new EventEmitter<void>();
  @ViewChild('fileInput') fileInput: any;
  public jsonData: any;
  public error: string | null = null;

  onFileChange(event: any) {
    const file: File = event.target.files[0];

    if (file) {
      this.csvConverterService.parsCsvToOutPatientDepartment(file).then(
        (data) => {
          this.jsonData = data.filter(i => i.name !== ''
            && containsKey(i, 'name')
            && containsKey(i, 'description')
            && containsKey(i, 'capacity')
            && containsKey(i, 'openingHours'));
          this.error = null;
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
        this.clearFileInput();
        this.reloadEvent.emit();

      },
      (error) => {
        this.notification.error('Error adding departments: ' + error.message);
        this.clearFileInput();

      }
    );
  }

  clearFileInput() {
    this.fileInput.nativeElement.value = '';
  }
}
