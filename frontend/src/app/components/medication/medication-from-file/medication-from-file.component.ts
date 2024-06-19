import {Component, EventEmitter, Output} from '@angular/core';
import {CsvConverterService} from "../../../services/csv-converter.service";
import {MedicationService} from "../../../services/medication.service";
import {ToastrService} from "ngx-toastr";
import {MedicationDto} from "../../../dtos/medication";

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
  ) {
  }

  onFileChange(event: any) {
    const file: File = event.target.files[0];

    if (file) {
      this.csvService.parseCsv(file).then(
        (data) => {
          const converted = data.map(item => {
            return item
          })
          console.log("medicaition from file:", converted)

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

  private addMedication(medication: MedicationDto) {
    this.medService.createMedication(medication).subscribe({
      next: () => {
        this.notification.success('Medication ' + medication.name + ' was successfully created.');
        this.onSuccessfulCreation();
      },
      error: error => {
        this.notification.error('Error creating medication: ' + error.message);
      }
    });
  }

  public addAll() {
    console.log('Adding possible all medications');
    this.jsonData.forEach((item: any) => {
      item.name = item.medication;
      if(item.name === '' || item.name === undefined) {
        return;
      }
      if(this.medication.includes(item.name)){
        return;
      }
      console.log("adding medication", item.name);
      const newMed: MedicationDto = {
        name: item.name,
        id: undefined
      }
      this.addMedication(newMed);
    });

  }
}
