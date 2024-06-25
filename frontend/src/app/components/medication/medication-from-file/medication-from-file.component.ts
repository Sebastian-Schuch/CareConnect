import {Component, EventEmitter, Output} from '@angular/core';
import {CsvConverterService} from "../../../services/csv-converter.service";
import {MedicationService} from "../../../services/medication.service";
import {ToastrService} from "ngx-toastr";
import {MedicationDto} from "../../../dtos/medication";
import {forkJoin, of} from "rxjs";
import {ErrorFormatterService} from "../../../services/error-formatter.service";

@Component({
  selector: 'app-medication-from-file',
  templateUrl: './medication-from-file.component.html',
  styleUrl: './medication-from-file.component.scss'
})
export class MedicationFromFileComponent {
  @Output() creationSuccess = new EventEmitter<void>();

  public jsonData: any;
  public error: string | null = null;

  public medication: MedicationDto[] = [];

  constructor(
    private csvService: CsvConverterService,
    private medService: MedicationService,
    private notification: ToastrService,
    private errorFormatter: ErrorFormatterService
  ) {
  }

  onFileChange(event: any) {
    const file: File = event.target.files[0];

    if (file) {
      this.csvService.parseCsv(file).then(
        (data) => {
          const converted = data.map(item => {
            return {
              name: item.medication,
              medication: item.medication,
              unitOfMeasurement: item.unitOfMeasurement
            };
          })

          this.jsonData = converted.filter(i => i.name !== '');
          this.error = null;
        },
        (error) => {
          this.error = error.message;
          this.jsonData = null;
        }
      );
    }
  }

  private onSuccessfulCreation() {
    this.creationSuccess.emit();
  }

  public addAll() {
    const requests = this.jsonData.map((item: any) => {
      item.name = item.medication;
      if (item.name === '' || item.name === undefined || this.medication.includes(item.name)) {
        return of(null);  // Return an observable that resolves to null to keep forkJoin intact
      }
      const newMed: MedicationDto = {
        name: item.name,
        unitOfMeasurement: item.unitOfMeasurement,
        id: undefined
      };
      return this.medService.createMedication(newMed);
    }).filter(request => request !== null);  // Filter out the null observables

    forkJoin(requests).subscribe({
      next: (results) => {
        // @ts-ignore
        const allSuccessful = results.every(result => result !== null);
        if (allSuccessful) {
          this.notification.success('All medications were successfully added.');
        } else {
          this.notification.error('Some medications could not be added.');
        }
        this.onSuccessfulCreation();
      },
      error: async error => {
        await this.errorFormatter.printErrorToNotification(error, 'Error adding medications', this.notification);
      }
    });
  }

}
