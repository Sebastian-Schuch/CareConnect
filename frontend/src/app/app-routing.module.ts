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
import {UserCreateComponent, UserCreateEditMode} from "./components/user/user-create-edit/user-create.component";
import {Role} from "./dtos/Role";
import {TreatmentComponent, TreatmentCreateEditMode} from "./components/treatment/treatment.component";
import {
  OutpatientDepartmentComponent
} from "./components/outpatient-department-create-edit/outpatient-department-create-edit.component";
import {InpatientDepartmentCreateEditMode, StationComponent} from "./components/station/station.component";
import {MedicationCreateComponent} from "./components/medication-create/medication-create.component";
import {AllergyComponent} from "./components/allergy/allergy.component";
import {ChatComponent} from "./components/chat/chat.component";
import {UserListComponent} from "./components/user/user-list/user-list.component";
import {NotFoundComponent} from "./components/not-found/not-found.component";
import {StaysListComponent} from "./components/stays/stays-list/stays-list.component";
import {
  InpatientDepartmentListComponent
} from "./components/station/inpatient-department-list/inpatient-department-list.component";

const routes: Routes = [
  {path: '', component: LandingLoggedOutComponent},
  {path: 'login', component: LoginComponent},
  {
    path: 'home', children: [
      {
        path: 'patient', canActivate: [PatientGuard], children: [
          {path: '', component: LandingPatientComponent},
          {
            path: 'appointments', children: [
              {path: '', component: AppointmentsPatientComponent},
              {path: 'book', component: CalendarWrapperComponent},
            ]
          },
          {path: 'telemedicine', component: ChatComponent},
          {path: 'inpatient-department', component: InpatientDepartmentListComponent},
          {
            path: ':id', children: [
              {path: '', component: UserCreateComponent, data: {role: Role.patient, mode: UserCreateEditMode.view}},
              {path: 'edit', component: UserCreateComponent, data: {role: Role.patient, mode: UserCreateEditMode.edit}}
            ]
          }
        ]
      },
      {
        path: 'admin', canActivate: [AdminGuard], children: [
          {path: '', component: LandingAdminComponent},
          {
            path: 'register', children: [
              //{path: 'admin', component: UserCreateComponent, data: {roleString: Role.admin, mode: UserCreateEditMode.create}},
              {
                path: 'doctor',
                component: UserCreateComponent,
                data: {role: Role.doctor, mode: UserCreateEditMode.create}
              },
              {
                path: 'secretary',
                component: UserCreateComponent,
                data: {role: Role.secretary, mode: UserCreateEditMode.create}
              },
              {path: 'outpatient-department', component: OutpatientDepartmentComponent},
              {
                path: 'inpatient-department',
                component: StationComponent,
                data: {mode: InpatientDepartmentCreateEditMode.create}
              },
              {path: 'medicine', component: MedicationCreateComponent},
              {path: 'allergy', component: AllergyComponent}
            ]
          },
          {
            path: 'users', children: [
              {
                path: 'doctors', children: [
                  {path: '', component: UserListComponent, data: {role: Role.doctor}},
                  {
                    path: ':id', children: [
                      {
                        path: '',
                        component: UserCreateComponent,
                        data: {role: Role.doctor, mode: UserCreateEditMode.view}
                      },
                      {
                        path: 'edit',
                        component: UserCreateComponent,
                        data: {role: Role.doctor, mode: UserCreateEditMode.edit}
                      }
                    ]
                  }
                ]
              },
              {
                path: 'secretaries', children: [
                  {path: '', component: UserListComponent, data: {role: Role.secretary}},
                  {
                    path: ':id', children: [
                      {
                        path: '',
                        component: UserCreateComponent,
                        data: {role: Role.secretary, mode: UserCreateEditMode.view}
                      },
                      {
                        path: 'edit',
                        component: UserCreateComponent,
                        data: {role: Role.secretary, mode: UserCreateEditMode.edit}
                      }
                    ]
                  }
                ]
              },
              {
                path: 'admins', children: [
                  {path: '', component: UserListComponent, data: {role: Role.admin}},
                  {
                    path: ':id', children: [
                      {
                        path: '',
                        component: UserCreateComponent,
                        data: {role: Role.admin, mode: UserCreateEditMode.view}
                      },
                      {
                        path: 'edit',
                        component: UserCreateComponent,
                        data: {role: Role.admin, mode: UserCreateEditMode.edit}
                      }
                    ]
                  }
                ]
              }
            ]
          },
          {
            path: 'inpatient-department', children: [
              {path: '', component: InpatientDepartmentListComponent},
              {
                path: ':id', children: [
                  {path: 'edit', component: StationComponent, data: {mode: InpatientDepartmentCreateEditMode.edit}}
                ]
              }
            ]
          },
          {
            path: ':id', children: [
              {path: '', component: UserCreateComponent, data: {role: Role.admin, mode: UserCreateEditMode.view}},
              {path: 'edit', component: UserCreateComponent, data: {role: Role.admin, mode: UserCreateEditMode.edit}}
            ]
          }
        ]
      },
      {
        path: 'doctor', canActivate: [DoctorGuard], children: [
          {path: '', component: LandingDoctorComponent},
          {path: 'telemedicine', component: ChatComponent},
          {path: 'inpatient-department', component: InpatientDepartmentListComponent},
          {
            path: 'treatment', children: [
              {path: 'log', component: TreatmentComponent, data: {mode: TreatmentCreateEditMode.log}},
              {
                path: ':id', children: [
                  {
                    path: 'edit', component: TreatmentComponent, data: {mode: TreatmentCreateEditMode.edit}
                  }]
              }]
          },
          {
            path: ':id', children: [
              {path: '', component: UserCreateComponent, data: {role: Role.doctor, mode: UserCreateEditMode.view}},
              {
                path: 'edit',
                component: UserCreateComponent,
                data: {role: Role.doctor, mode: UserCreateEditMode.edit}
              }
            ]
          }]
      },
      {
        path: 'secretary', canActivate: [SecretaryGuard], children: [
          {path: '', component: LandingSecretaryComponent},
          {
            path: 'appointments', children: [
              {path: '', component: AppointmentsSecretaryComponent},
              {path: 'book', component: CalendarWrapperComponent},
            ]
          },
          {
            path: 'patients', children: [
              {path: '', component: UserListComponent, data: {role: Role.patient}},
              {
                path: 'register',
                component: UserCreateComponent,
                data: {role: Role.patient, mode: UserCreateEditMode.create}
              },
              {
                path: ':id',
                children: [
                  {path: '', component: UserCreateComponent, data: {role: Role.patient, mode: UserCreateEditMode.view}},
                  {
                    path: 'edit',
                    component: UserCreateComponent,
                    data: {role: Role.patient, mode: UserCreateEditMode.edit}
                  },
                  {
                    path: 'stay',
                    component: StaysListComponent
                  }
                ]
              },
            ]
          },
          {
            path: 'stay', children: [
              //{path: 'register', component: null},
              //{path: ':id', children: [
              //{path: '', component: stayViewComponent},
              //{path: 'edit', component: stayEditComponent}
              //]},
            ]
          },
          {path: 'inpatient-department', component: InpatientDepartmentListComponent},
          {
            path: ':id', children: [
              {path: '', component: UserCreateComponent, data: {role: Role.secretary, mode: UserCreateEditMode.view}},
              {
                path: 'edit',
                component: UserCreateComponent,
                data: {role: Role.secretary, mode: UserCreateEditMode.edit}
              }
            ]
          }
        ]
      }
    ]
  },
  {path: 'not-found', component: NotFoundComponent},
  {path: '**', redirectTo: '/not-found'}
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {useHash: true})],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
