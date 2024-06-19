import { Component } from '@angular/core';
import {CsvConverterService} from "../../services/csv-converter.service";
import {error} from "@angular/compiler-cli/src/transformers/util";
import {ToastrService} from "ngx-toastr";
import {OutpatientDepartmentService} from "../../services/outpatient-department.service";
import {OutpatientDepartmentDto} from "../../dtos/outpatient-department";

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

  private addDepartment(department: OutpatientDepartmentDto){
    this.outPatientDepartmentService.createOutpatientDepartment(department).subscribe(
      (response) => {
        this.notification.success('Department added successfully');
      },
      (error) => {
        this.notification.error('Error adding department: ' + error.message);
      }
    );
  }

  addAll(){
    this.jsonData.forEach(department => {
      this.addDepartment(department);
    });
  }
}
