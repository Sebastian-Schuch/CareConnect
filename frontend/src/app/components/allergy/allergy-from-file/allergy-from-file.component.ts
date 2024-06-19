import {Component, EventEmitter, Output} from '@angular/core';
import {AllergyService} from "../../../services/allergy.service";
import {ToastrService} from "ngx-toastr";
import {ErrorFormatterService} from "../../../services/error-formatter.service";
import {Router} from "@angular/router";
import {CsvConverterService} from "../../../services/csv-converter.service";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {AllergyDto} from "../../../dtos/allergy";

@Component({
  selector: 'app-allergy-from-file',
  templateUrl: './allergy-from-file.component.html',
  styleUrl: './allergy-from-file.component.scss'
})
export class AllergyFromFileComponent {
  @Output() creationSuccess = new EventEmitter<void>();

  public jsonData: any;
  public error: string | null = null;

  public allergies: AllergyDto[] = [];

  constructor(
    private csvService: CsvConverterService,
    private allergyService: AllergyService,
    private notification: ToastrService,

  ) {
  }

  onFileChange(event: any) {
    const file: File = event.target.files[0];

    if (file) {
      this.csvService.parseCsv(file).then(
        (data) => {
          const converted = data.map(item => {
            return {
              name: item.allergie
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

  addAll() {
    console.log('Adding all allergies');
    this.jsonData.forEach((item: any) => {
      this.allergyService.createAllergy(item).subscribe({
        next: () => {
          this.notification.success('Allergy ' + item.name + ' was successfully created.');
          this.onSuccessfulCreation();
        },
        error: error => {
          this.notification.error('Error creating allergy: ' + error.message);
        }
      });
    });
  }

  private onSuccessfulCreation(): void {
    this.creationSuccess.emit();
  }

}
