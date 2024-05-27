import {BrowserModule} from '@angular/platform-browser';
import {CUSTOM_ELEMENTS_SCHEMA, NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {HeaderComponent} from './components/header/header.component';
import {FooterComponent} from './components/footer/footer.component';
import {HomeComponent} from './components/home/home.component';
import {NgbModalModule, NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {httpInterceptorProviders} from './interceptors';
import {UserCreateComponent} from "./components/user-create/user-create.component";
import {
  OutpatientDepartmentComponent
} from "./components/outpatient-department-create-edit/outpatient-department-create-edit.component";
import {
  OutpatientDepartmentDetailComponent
} from "./components/outpatient-department-detail/outpatient-department-detail.component";
import {TreatmentComponent} from "./components/treatment/treatment.component";
import {provideAnimationsAsync} from '@angular/platform-browser/animations/async';
import {MatChipsModule} from "@angular/material/chips";
import {MatAutocompleteModule} from "@angular/material/autocomplete";
import {MatSelectModule} from "@angular/material/select";
import {MatInputModule} from '@angular/material/input';
import {MatDatepickerModule} from "@angular/material/datepicker";
import {MAT_DATE_LOCALE, MatNativeDateModule, MatOptionModule, provideNativeDateAdapter} from '@angular/material/core';
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatIconModule} from "@angular/material/icon";
import {MatTableModule} from "@angular/material/table";
import {MatPaginatorModule} from "@angular/material/paginator";
import {MatButton, MatIconButton} from "@angular/material/button";
import {StationComponent} from "./components/station/station.component";
import {AllergyComponent} from "./components/allergy/allergy.component";
import {LoginComponent} from "./components/login/login.component";
import {MedicationCreateComponent} from "./components/medication-create/medication-create.component";
import {CalendarModule, DateAdapter} from "angular-calendar";
import {adapterFactory} from "angular-calendar/date-adapters/date-fns";
import {CalenderComponent} from "./components/appointments/calender/calendar/calender.component";
import {CommonModule} from "@angular/common";
import {FlatpickrModule} from "angularx-flatpickr";

import {ToastrModule} from "ngx-toastr";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {ChatComponent} from "./components/chat/chat.component";
import {CalendarWrapperComponent} from "./components/appointments/calender/calendar-wrapper/calendar-wrapper.component";
import {
  AppointmentsPatientComponent
} from "./components/appointments/appointments-patient/appointments-patient.component";
import {
  ConfirmDeleteDialogComponent
} from "./components/appointments/confirm-delete-dialog/confirm-delete-dialog.component";
import {
  AppointmentsSecretaryComponent
} from "./components/appointments/appointments-secretary/appointments-secretary.component";

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    FooterComponent,
    HomeComponent,
    LoginComponent,
    TreatmentComponent,
    StationComponent,
    AllergyComponent,
    UserCreateComponent,
    OutpatientDepartmentComponent,
    OutpatientDepartmentDetailComponent,
    MedicationCreateComponent,
    LoginComponent,
    CalenderComponent,
    CalendarWrapperComponent,
    AppointmentsPatientComponent,
    AppointmentsSecretaryComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    HttpClientModule,
    ToastrModule.forRoot(),
    BrowserAnimationsModule,
    NgbModule,
    MatChipsModule,
    MatFormFieldModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatInputModule,
    MatAutocompleteModule,
    MatOptionModule,
    MatIconModule,
    MatTableModule,
    MatPaginatorModule,
    MatButton,
    MatIconButton,
    CommonModule,
    FormsModule,
    NgbModalModule,
    FlatpickrModule.forRoot(),
    CalendarModule.forRoot({
      provide: DateAdapter,
      useFactory: adapterFactory,
    }),
    ConfirmDeleteDialogComponent,
    MatIconButton,
    ChatComponent
  ],
  providers: [httpInterceptorProviders, provideAnimationsAsync('noop'),
    {provide: MAT_DATE_LOCALE, useValue: 'de-DE'}, provideNativeDateAdapter()
  ],
  exports: [
    CalenderComponent
  ],
  bootstrap: [AppComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class AppModule {
}
