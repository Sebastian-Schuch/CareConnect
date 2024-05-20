import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {HeaderComponent} from './components/header/header.component';
import {FooterComponent} from './components/footer/footer.component';
import {HomeComponent} from './components/home/home.component';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
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
import {MAT_DATE_LOCALE, MatNativeDateModule, provideNativeDateAdapter} from '@angular/material/core';
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatIconModule} from "@angular/material/icon";
import {MatTableModule} from "@angular/material/table";
import {MatPaginatorModule} from "@angular/material/paginator";
import {MatButton, MatIconButton} from "@angular/material/button";
import {StationComponent} from "./components/station/station.component";
import {AllergyComponent} from "./components/allergy/allergy.component";
import {LoginComponent} from "./components/login/login.component";
import {MedicationCreateComponent} from "./components/medication-create/medication-create.component";
import {ToastrModule} from "ngx-toastr";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";

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
    LoginComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    HttpClientModule,
    ToastrModule.forRoot(),
    BrowserAnimationsModule,
    NgbModule,
    FormsModule,
    MatChipsModule,
    MatFormFieldModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatInputModule,
    MatAutocompleteModule,
    MatIconModule,
    MatTableModule,
    MatPaginatorModule,
    MatButton,
    MatIconButton
  ],
  providers: [httpInterceptorProviders, provideAnimationsAsync('noop'),
    {provide: MAT_DATE_LOCALE, useValue: 'de-DE'}, provideNativeDateAdapter()
  ],

  bootstrap: [AppComponent]
})
export class AppModule {
}
