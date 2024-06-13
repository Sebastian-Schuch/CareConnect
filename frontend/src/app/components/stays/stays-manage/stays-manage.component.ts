import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {MatDialogActions, MatDialogClose, MatDialogContent, MatDialogTitle} from "@angular/material/dialog";
import {MatFormField} from "@angular/material/form-field";
import {MatOption, MatSelect} from "@angular/material/select";
import {MatButton} from "@angular/material/button";
import {UserDtoList} from "../../../dtos/user";
import {MatInput} from "@angular/material/input";
import {MatAutocomplete, MatAutocompleteTrigger} from "@angular/material/autocomplete";
import {FormControl, ReactiveFormsModule} from "@angular/forms";
import {catchError, of, switchMap} from "rxjs";
import {StationDto} from "../../../dtos/Station";
import {StationService} from "../../../services/station.service";
import {DatePipe, NgForOf, NgIf} from "@angular/common";
import {StaysService} from "../../../services/stays.service";
import {MatCard, MatCardActions, MatCardContent, MatCardTitle} from "@angular/material/card";

@Component({
  selector: 'app-stays-manage',
  standalone: true,
  imports: [
    MatDialogContent,
    MatFormField,
    MatSelect,
    MatOption,
    MatDialogActions,
    MatDialogClose,
    MatButton,
    MatDialogTitle,
    MatInput,
    MatAutocompleteTrigger,
    ReactiveFormsModule,
    MatAutocomplete,
    NgForOf,
    NgIf,
    DatePipe,
    MatCard,
    MatCardTitle,
    MatCardContent,
    MatCardActions
  ],
  templateUrl: './stays-manage.component.html',
  styleUrl: './stays-manage.component.scss'
})


export class StaysManageComponent implements OnInit {
  user: UserDtoList;
  filteredStations: StationDto[];
  myControl = new FormControl();
  isCheckedIn: boolean = false;
  currentStay = null;
  @Input() patientId: number;
  @Output() stayChanged = new EventEmitter<any>();

  constructor(private stationService: StationService,
              private staysService: StaysService) {

    this.myControl.valueChanges.pipe(
      switchMap(value => {
        let searchTerm = value?.name || value;

        return this.stationService.getStations(searchTerm, 0, 5).pipe(
          catchError(() => of({stations: [], totalCount: 0}))
        );
      })
    ).subscribe(stations => this.filteredStations = stations.stations);
  }

  isInputEmpty(): boolean {
    return !this.myControl.value;
  }

  checkPatientIn() {
    this.staysService.createNewStay({
      patientId: this.patientId,
      station: this.myControl.value
    }).subscribe(stay => {
      this.isCheckedIn = true;
      stay.arrival = new Date(stay.arrival + "Z");
      this.currentStay = stay;
    });
  }

  displayFn(station: StationDto): string {
    return station && station.name ? station.name : '';
  }

  dischargePatient() {
    this.staysService.endStay(this.currentStay).subscribe(stay => {
      this.isCheckedIn = false;
      this.stayChanged.emit();
    });
  }

  ngOnInit(): void {
    this.stationService.getStations('', 0, 5).subscribe(stations => this.filteredStations = stations.stations);
    this.staysService.getCurrentStay(this.patientId).subscribe(stay => {
      this.isCheckedIn = stay != null
      this.currentStay = stay;
      if (stay && stay.arrival) {
        stay.arrival = new Date(stay.arrival + "Z");
      }
    });
  }


}
