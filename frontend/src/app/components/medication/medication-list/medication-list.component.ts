import {Component, OnInit} from '@angular/core';
import {FormsModule} from "@angular/forms";
import {MatIcon} from "@angular/material/icon";
import {MatPaginator, PageEvent} from "@angular/material/paginator";
import {NgForOf} from "@angular/common";
import {Router} from "@angular/router";
import {ErrorFormatterService} from "../../../services/error-formatter.service";
import {ToastrService} from "ngx-toastr";
import {MatDialog} from "@angular/material/dialog";
import {MedicationService} from "../../../services/medication.service";
import {MedicationDto} from "../../../dtos/medication";
import {MedicationDeleteComponent} from "../medication-delete/medication-delete.component";

@Component({
  selector: 'app-medication-list',
  standalone: true,
  imports: [
    FormsModule,
    MatIcon,
    MatPaginator,
    NgForOf
  ],
  templateUrl: './medication-list.component.html',
  styleUrl: './medication-list.component.scss'
})
export class MedicationListComponent implements OnInit {
  constructor(private router: Router, private medicationService: MedicationService, private errorFormatterService: ErrorFormatterService, private notificationService: ToastrService, public dialog: MatDialog) {
  }

  totalItems: number = 0;
  medications: MedicationDto[] = [];
  medicationName: string = '';
  size: number = 10;
  page: number = 0;

  public reloadMedications(): void {
    this.medicationService.searchMedications(this.medicationName, this.page, this.size).subscribe({
      next: medicationPage => {
        this.medications = medicationPage.medications;
        this.totalItems = medicationPage.totalItems;
      },
      error: async error => {
        await this.errorFormatterService.printErrorToNotification(error, "Error loading Medications", this.notificationService);
      }
    });
  }

  onPageChange($event: PageEvent) {
    this.size = $event.pageSize;
    this.page = $event.pageIndex;
    this.reloadMedications();
  }

  public navigateToEditMedication(medicationId: number): void {
    this.router.navigate(['home/admin/medications/' + medicationId + '/edit']);
  }

  private deleteMedication(id: number) {
    this.medicationService.deleteMedication(id).subscribe({
      next: () => {
        this.reloadMedications();
        this.notificationService.success("Medication deleted successfully");
      },
      error: async error => {
        await this.errorFormatterService.printErrorToNotification(error, "Error deleting Medication", this.notificationService);
      }
    });
  }

  openDeleteDialog(medication: MedicationDto) {
    const dialogRef = this.dialog.open(MedicationDeleteComponent,
      {data: {medication: medication}});

    dialogRef.afterClosed().subscribe({
      next: value => {
        if (value) {
          this.deleteMedication(medication.id);
          if (this.medications.length === 1) {
            this.page = this.page - 1;
            this.reloadMedications();
          }
        }
      },
      error: err => console.error(err)
    })
  }

  ngOnInit(): void {
    this.reloadMedications();
  }
}
