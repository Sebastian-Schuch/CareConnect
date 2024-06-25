import {Component, EventEmitter, Output} from '@angular/core';
import {AllergyService} from "../../../services/allergy.service";
import {ToastrService} from "ngx-toastr";
import {CsvConverterService} from "../../../services/csv-converter.service";
import {AllergyDto} from "../../../dtos/allergy";
import {catchError, forkJoin, of} from "rxjs";

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

    const requests = this.jsonData.map((item: any) =>
      this.allergyService.createAllergy(item).pipe(
        catchError(error => {
          this.notification.error('Error creating allergy: ' + error.message);
          return of({ item, success: false });  // Return an object to keep track of the error
        })
      )
    );

    forkJoin(requests).subscribe({
      next: (results) => {
        if (results) {
          // @ts-ignore
          const allSuccessful = results.every(result => result !== null && result.success !== false);
          if (allSuccessful) {
            this.notification.success('All allergies were successfully created.');
          } else {
            this.notification.error('Some allergies could not be created.');
          }
          this.onSuccessfulCreation();
        }
      },
      error: error => {
        this.notification.error('Error creating allergies: ' + error.message);
      }
    });
  }

  private onSuccessfulCreation(): void {
    this.creationSuccess.emit();
  }

}
