import {NgModule} from '@angular/core';
import {mapToCanActivate, RouterModule, Routes} from '@angular/router';
import {HomeComponent} from './components/home/home.component';
import {LoginComponent} from './components/login/login.component';
import {AuthGuard} from './guards/auth.guard';
import {MessageComponent} from './components/message/message.component';
import {UserCreateComponent} from "./components/user-create/user-create.component";
import {Role} from "./dtos/Role";
import {MedicationCreateComponent} from "./components/medication-create/medication-create.component";

const routes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'login', component: LoginComponent},
  {path: 'message', canActivate: mapToCanActivate([AuthGuard]), component: MessageComponent},
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
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {useHash: true})],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
