import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {LoginComponent} from './components/login/login.component';
import {LandingLoggedOutComponent} from "./components/landing/landing-logged-out/landing-logged-out.component";
import {LandingPatientComponent} from "./components/landing/landing-patient/landing-patient.component";
import {LandingAdminComponent} from "./components/landing/landing-admin/landing-admin.component";
import {LandingDoctorComponent} from "./components/landing/landing-doctor/landing-doctor.component";
import {LandingSecretaryComponent} from "./components/landing/landing-secretary/landing-secretary.component";
import {
  AppointmentsPatientComponent
} from "./components/appointments/appointments-patient/appointments-patient.component";
import {PatientGuard} from "./services/AuthGuards/patient.guard";
import {AdminGuard} from "./services/AuthGuards/admin.guard";
import {DoctorGuard} from "./services/AuthGuards/doctor.guard";
import {SecretaryGuard} from "./services/AuthGuards/secretary.guard";
import {CalendarWrapperComponent} from "./components/appointments/calender/calendar-wrapper/calendar-wrapper.component";

import {
  AppointmentsSecretaryComponent
} from "./components/appointments/appointments-secretary/appointments-secretary.component";
import {UserCreateComponent} from "./components/user-create/user-create.component";
import {Role} from "./dtos/Role";
import {TreatmentComponent} from "./components/treatment/treatment.component";
import {
  OutpatientDepartmentComponent
} from "./components/outpatient-department-create-edit/outpatient-department-create-edit.component";
import {StationComponent} from "./components/station/station.component";
import {MedicationCreateComponent} from "./components/medication-create/medication-create.component";
import {AllergyComponent} from "./components/allergy/allergy.component";
import {ChatComponent} from "./components/chat/chat.component";

const routes: Routes = [
  {path: '', component: LandingLoggedOutComponent},
  {path: 'login', component: LoginComponent},
  {
    path: 'home', children: [
      {
        path: 'patient', canActivate: [PatientGuard], children: [
          {path: '', component: LandingPatientComponent},
          {path: 'book-appointment', component: CalendarWrapperComponent},
          {path: 'appointments', component: AppointmentsPatientComponent},
          {path: 'telemedicine', component: ChatComponent},
        ]
      },
      {
        path: 'admin', canActivate: [AdminGuard], children: [
          {path: '', component: LandingAdminComponent},
          //{path: 'register-admin', component: UserCreateComponent, data: {role: Role.admin}},
          {path: 'register-doctor', component: UserCreateComponent, data: {role: Role.doctor}},
          {path: 'register-secretary', component: UserCreateComponent, data: {role: Role.secretary}},
          {path: 'register-outpatient-department', component: OutpatientDepartmentComponent},
          {path: 'register-inpatient-department', component: StationComponent},
          {path: 'register-medicine', component: MedicationCreateComponent},
          {path: 'register-allergy', component: AllergyComponent}
        ]
      },
      {
        path: 'doctor', canActivate: [DoctorGuard], children: [
          {path: '', component: LandingDoctorComponent},
          {path: 'telemedicine', component: ChatComponent},
          {path: 'log-treatment', component: TreatmentComponent},
          //{path: 'edit-treatment', component: null}
        ]
      },
      {
        path: 'secretary', canActivate: [SecretaryGuard], children: [
          {path: '', component: LandingSecretaryComponent},
          {path: 'book-appointment', component: CalendarWrapperComponent},
          {path: 'appointments', component: AppointmentsSecretaryComponent},
          {path: 'register-patient', component: UserCreateComponent, data: {role: Role.patient}},
          //{path: 'edit-patient', component: null},
          //{path: 'register-stay', component: null},
          //{path: 'edit-stay', component: null},
        ]
      }
    ]
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {useHash: true})],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
