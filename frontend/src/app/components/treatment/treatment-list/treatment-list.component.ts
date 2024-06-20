import {Component, OnInit} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatIcon} from "@angular/material/icon";
import {MatPaginator, PageEvent} from "@angular/material/paginator";
import {NgClass, NgForOf, NgIf} from "@angular/common";
import {Role} from "../../../dtos/Role";
import {TreatmentDto, TreatmentDtoSearch} from "../../../dtos/treatment";
import {TreatmentService} from "../../../services/treatment.service";
import {ToastrService} from "ngx-toastr";
import {ErrorFormatterService} from "../../../services/error-formatter.service";
import {ActivatedRoute, Router} from "@angular/router";
import {UserService} from "../../../services/user.service";
import {UserDto} from "../../../dtos/user";
import {TreatmentMedicineSelection} from "../../../dtos/treatmentMedicine";
import {getDate, getHours, getMinutes, getMonth, getYear} from "date-fns";
import {StaysService} from "../../../services/stays.service";
import {StayDto} from "../../../dtos/stays";
import {MatButton} from "@angular/material/button";

export enum TreatmentListMode {
  view,
  search
}

@Component({
  selector: 'app-treatment-list',
  standalone: true,
  imports: [
    FormsModule,
    MatIcon,
    MatPaginator,
    NgForOf,
    NgIf,
    ReactiveFormsModule,
    MatButton,
    NgClass
  ],
  templateUrl: './treatment-list.component.html',
  styleUrls: ['./treatment-list.component.scss', '../../../../styles.scss']
})
export class TreatmentListComponent implements OnInit {

  constructor(
    private treatmentService: TreatmentService,
    private notificationService: ToastrService,
    private errorFormatterService: ErrorFormatterService,
    private route: ActivatedRoute,
    private userService: UserService,
    private stayService: StaysService,
    private router: Router
  ) {
  }

  role: Role = null;

  mode: TreatmentListMode = null;

  treatments: TreatmentDto[] = [];

  totalItems: number = 0;

  stays: StayDto[] = []

  totalStayItems: number = 0;

  colors: string[] = [];

  search: TreatmentDtoSearch = {
    page: 0,
    size: 10,
    patientId: -1,
    startDate: null,
    endDate: null,
    treatmentTitle: "",
    medicationName: "",
    doctorName: "",
    patientName: "",
    svnr: "",
    departmentName: ""
  };

  ngOnInit() {
    this.route.data.subscribe(data => {
      this.role = data.role;
      this.mode = data.mode;
      if (this.role == Role.patient) {
        this.userService.getPatientCredentials().subscribe({
          next: data => {
            this.search.patientId = data.id;
            this.reloadTreatments();
          },
          error: async error => {
            await this.errorFormatterService.printErrorToNotification(error, "Couldn't load user", this.notificationService);
          }
        });
      } else {
        this.reloadTreatments();
      }
    });
  }

  public reloadTreatments() {
    this.treatmentService.searchTreatments(this.search).subscribe({
      next: data => {
        this.treatments = data.treatments;
        this.totalItems = data.totalItems;
        if (this.role === Role.patient) {
          this.loadStays();
        }
      },
      error: async error => {
        await this.errorFormatterService.printErrorToNotification(error, "Couldn't load treatments", this.notificationService, "Please try again later.");
      }
    })
  }

  private loadStays() {
    this.stayService.getAllStays(this.search.patientId, 0, 100).subscribe({
      next: data => {
        this.stays = data.content;
        this.totalStayItems = data.totalElements;
        let offset = 0;
        let lastStay = -1;
        for (let i = 0; i < this.treatments.length; i++) {
          let currentStay = this.isTreatmentInStay(this.treatments[i]);
          if (currentStay) {
            if (lastStay == currentStay) {
              offset += 1;
              lastStay = currentStay;
            } else {
              lastStay = currentStay;
            }
            this.colors.push((i - offset) % 2 == 0 ? 'table-primary' : 'table-info');
          } else {
            lastStay = -1;
            offset += 1;
            this.colors.push(' ');
          }
        }
      }
    })
  }

  onPageChange($event: PageEvent) {
    this.search.size = $event.pageSize;
    this.search.page = $event.pageIndex;
    this.reloadTreatments();
  }

  public showUserName(user: UserDto): string {
    return user.firstname + " " + user.lastname;
  }

  public showDoctors(doctors: UserDto[]): string {
    let result = "";
    doctors.forEach(doctor => {
      result += this.showUserName(doctor) + ", ";
    });
    return result.substring(0, result.length - 2);
  }

  public showMedicines(medicines: TreatmentMedicineSelection[]): string {
    let result = "";
    medicines.forEach(medicine => {
      result += medicine.medication.name + ", ";
    });
    return result.substring(0, result.length - 2);
  }

  public getDayTimeString(day: any): string {
    return getDate(day) + '/' + (getMonth(day) + 1) + '/' + getYear(day) + " " + ((getHours(day) < 10) ? '0' : '') + getHours(day) + ":" + ((getMinutes(day) < 10) ? '0' : '') + getMinutes(day);
  }

  public showDate(start: Date, end: Date): string {
    return this.getDayTimeString(start) + " - " + this.getDayTimeString(end);
  }

  public navigateToEditTreatment(id: number) {
    this.router.navigate(['home/doctor/treatment/' + id + '/edit']);
  }

  public isTreatmentInStay(treatment: TreatmentDto): number {
    for (let stay of this.stays) {
      if (stay.arrival <= treatment.treatmentStart && stay.discharge >= treatment.treatmentEnd ||
        stay.arrival <= treatment.treatmentStart && stay.discharge >= treatment.treatmentStart ||
        stay.arrival <= treatment.treatmentEnd && stay.discharge >= treatment.treatmentEnd ||
        stay.arrival >= treatment.treatmentStart && stay.discharge <= treatment.treatmentEnd) {
        return stay.id
      }
    }
    return null;
  }

  public navigateToViewTreatment(id: number) {
    switch (this.role) {
      case Role.doctor:
        this.router.navigate(['home/doctor/treatment/' + id]);
        break;
      case Role.patient:
        this.router.navigate(['home/patient/treatment/' + id]);
        break;
      case Role.secretary:
        this.router.navigate(['home/secretary/treatment/' + id]);
        break;
      default:
        console.error("Role not supported: " + this.role);
    }
  }

  public switchToSearch() {
    this.router.navigate(['search'], {relativeTo: this.route});
  }

  protected readonly Role = Role;
  protected readonly TreatmentListMode = TreatmentListMode;
}
