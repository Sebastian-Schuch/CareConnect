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
  OutpatientDepartmentComponent,
  OutpatientDepartmentCreateEditMode
} from "./components/outpatient-department-create-edit/outpatient-department-create-edit.component";
import {
  InpatientDepartmentComponent,
  InpatientDepartmentCreateEditMode
} from "./components/inpatient-department/inpatient-department.component";
import {AllergyComponent, AllergyCreatEditMode} from "./components/allergy/allergy.component";
import {ChatComponent} from "./components/chat/chat.component";
import {UserListComponent} from "./components/user/user-list/user-list.component";
import {NotFoundComponent} from "./components/not-found/not-found.component";
import {StaysListComponent} from "./components/stays/stays-list/stays-list.component";
import {
  InpatientDepartmentListComponent
} from "./components/inpatient-department/inpatient-department-list/inpatient-department-list.component";
import {ApiKeyComponent} from "./components/api-key/api-key.component";
import {
  OutpatientDepartmentListComponent
} from "./components/outpatient-department-list/outpatient-department-list.component";
import {
  OutpatientDepartmentDetailComponent
} from "./components/outpatient-department-detail/outpatient-department-detail.component";
import {
  TreatmentListComponent,
  TreatmentListMode
} from "./components/treatment/treatment-list/treatment-list.component";
import {AllergyListComponent} from "./components/allergy-list/allergy-list.component";
import {MedicationListComponent} from "./components/medication-list/medication-list.component";
import {
  OutpatientDepartmentCapacitiesComponent
} from "./components/outpatient-department-capacities/outpatient-department-capacities.component";
import {MainSetupPage} from "./components/setup-wizzard/main-page/main-setup-page.component";
import {MedicationCreateComponent} from "./components/medication/medication-create/medication-create.component";

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
          {path: 'outpatient-department', component: OutpatientDepartmentCapacitiesComponent},
          {
            path: 'treatment', children: [
              {path: '', component: TreatmentListComponent, data: {role: Role.patient, mode: TreatmentListMode.view}},
              {
                path: 'search',
                component: TreatmentListComponent,
                data: {role: Role.patient, mode: TreatmentListMode.search}
              },
              {path: ':id', component: TreatmentComponent, data: {mode: TreatmentCreateEditMode.detail}}
            ]
          },
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
          {path:'setup', component: MainSetupPage},
          {
            path: 'register', children: [
              {
                path: 'admin',
                component: UserCreateComponent,
                data: {role: Role.admin, mode: UserCreateEditMode.create}
              },
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
              {
                path: 'outpatient-department',
                component: OutpatientDepartmentComponent,
                data: {mode: OutpatientDepartmentCreateEditMode.create}
              },
              {
                path: 'inpatient-department',
                component: InpatientDepartmentComponent,
                data: {mode: InpatientDepartmentCreateEditMode.create}
              },
              {path: 'medication', component: MedicationCreateComponent, data: {mode: MedicationCreateEditMode.CREATE}},
              {path: 'allergy', component: AllergyComponent, data: {mode: AllergyCreatEditMode.CREATE}}
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
            path: 'allergies', children: [
              {path: '', component: AllergyListComponent},
              {
                path: ':id', children: [
                  {path: 'edit', component: AllergyComponent, data: {mode: AllergyCreatEditMode.EDIT}},
                ]
              }
            ]
          },
          {
            path: 'medications', children: [
              {path: '', component: MedicationListComponent},
              {
                path: ':id', children: [
                  {path: 'edit', component: MedicationCreateComponent, data: {mode: MedicationCreateEditMode.EDIT}}
                ]
              }
            ]
          },
          {
            path: 'inpatient-department', children: [
              {path: '', component: InpatientDepartmentListComponent},
              {
                path: ':id', children: [
                  {
                    path: 'edit',
                    component: InpatientDepartmentComponent,
                    data: {mode: InpatientDepartmentCreateEditMode.edit}
                  }
                ]
              }
            ]
          },
          {
            path: 'outpatient-department', children: [
              {path: '', component: OutpatientDepartmentListComponent},
              {
                path: ':id', children: [
                  {
                    path: 'edit',
                    component: OutpatientDepartmentComponent,
                    data: {mode: OutpatientDepartmentCreateEditMode.edit}
                  },
                  {path: '', component: OutpatientDepartmentDetailComponent}
                ]
              }
            ]
          },
          {
            path: 'api-keys', component: ApiKeyComponent
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
          {path: 'outpatient-department', component: OutpatientDepartmentCapacitiesComponent},
          {
            path: 'treatment', children: [
              {path: '', component: TreatmentListComponent, data: {role: Role.doctor, mode: TreatmentListMode.search}},
              {path: 'log', component: TreatmentComponent, data: {mode: TreatmentCreateEditMode.log}},
              {
                path: ':id', children: [
                  {path: '', component: TreatmentComponent, data: {mode: TreatmentCreateEditMode.detail}},
                  {
                    path: 'edit', component: TreatmentComponent, data: {mode: TreatmentCreateEditMode.edit}
                  }
                ]
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
            path: 'treatment', children: [
              {
                path: '',
                component: TreatmentListComponent,
                data: {role: Role.secretary, mode: TreatmentListMode.search}
              },
              {
                path: ':id', component: TreatmentComponent, data: {mode: TreatmentCreateEditMode.detail}
              }
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
          {path: 'outpatient-department', component: OutpatientDepartmentCapacitiesComponent},
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
