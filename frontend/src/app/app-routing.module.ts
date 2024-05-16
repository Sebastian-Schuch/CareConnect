import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {HomeComponent} from './components/home/home.component';
import {AllergyComponent} from "./components/allergy/allergy.component";
import {TreatmentComponent} from "./components/treatment/treatment.component";
import {UserCreateComponent} from "./components/user-create/user-create.component";
import {Role} from "./dtos/Role";
import {MedicationCreateComponent} from "./components/medication-create/medication-create.component";
import {StationComponent} from "./components/station/station.component";
import {
  OutpatientDepartmentComponent
} from "./components/outpatient-department-create-edit/outpatient-department-create-edit.component";
import {LoginComponent} from "./components/login/login.component";

const routes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'login', component: LoginComponent},
  {path: 'allergies', component: AllergyComponent},
  {path: 'department', component: OutpatientDepartmentComponent},
  {path: 'treatment', children: [
      {path: 'create', component: TreatmentComponent},
      {path: ':id/detail', component: TreatmentComponent},
      {path: ':id/edit', component: TreatmentComponent}
    ]},
  {
    path: 'user', children: [
      {
        path: 'create', children: [
          {path: 'admin', component: UserCreateComponent, data: {mode: Role.admin}},
          {path: 'doctor', component: UserCreateComponent, data: {mode: Role.doctor}},
          {path: 'secretary', component: UserCreateComponent, data: {mode: Role.secretary}},
          {path: 'patient', component: UserCreateComponent, data: {mode: Role.patient}}
        ]
      }
    ]
  },
  {
    path: 'medication', children: [
      {
        path: 'create', component: MedicationCreateComponent
      }
    ]
  },
  {path: 'station', component: StationComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {useHash: true})],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
