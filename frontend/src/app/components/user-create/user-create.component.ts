import {Component, OnInit} from '@angular/core';
import {UserCreateDto} from "../../dtos/user";
import {UserService} from "../../services/user.service";
import {ActivatedRoute, Router} from "@angular/router";
import {FormsModule, NgForm, NgModel} from "@angular/forms";
import {Observable} from "rxjs";
import {Role} from "../../dtos/Role";
import {NgIf} from "@angular/common";
//import {ToastrService} from 'ngx-toastr';
//import {ErrorFormatterService} from "../../../service/error-formatter.service";

@Component({
  selector: 'app-user-create',
  standalone: true,
  imports: [
    FormsModule,
    NgIf
  ],
  templateUrl: './user-create.component.html',
  styleUrl: './user-create.component.scss'
})


export class UserCreateComponent implements OnInit {

  mode: Role = Role.patient;

  user: UserCreateDto = {
    email: '',
    firstname: '',
    lastname: ''
  };

  constructor(
    private service: UserService,
    private router: Router,
    private route: ActivatedRoute
    //private notification: ToastrService,
    //private errorFormatter: ErrorFormatterService
  ) {
  }

  public get role(): string {
    switch (this.mode) {
      case Role.admin:
        return 'Admin';
      case Role.doctor:
        return 'Doctor';
      case Role.secretary:
        return 'Secretary';
      case Role.patient:
        return 'Patient';
      default:
        return '?';
    }
  }

  get modeIsAdmin(): boolean {
    return this.mode === Role.admin;
  }

  get modeIsDoctor(): boolean {
    return this.mode === Role.doctor;
  }

  get modeIsSecretary(): boolean {
    return this.mode === Role.secretary;
  }

  get modeIsPatient(): boolean {
    return this.mode === Role.patient;
  }

  ngOnInit(): void {
    this.route.data.subscribe(data => {
      this.mode = data.mode;
    });
  }

  public dynamicCssClassesForInput(input: NgModel): any {
    return {
      'is-invalid': !input.valid && !input.pristine,
    };
  }

  public onSubmit(form: NgForm): void {
    console.log('is form valid?', form.valid, this.user);
    if (form.valid) {
      let observable: Observable<UserCreateDto>;
      switch (this.mode) {
        case Role.admin:
          observable = this.service.createAdmin(this.user);
          break;
        case Role.doctor:
          observable = this.service.createDoctor(this.user);
          break;
        case Role.secretary:
          observable = this.service.createSecretary(this.user);
          break;
        case Role.patient:
          observable = this.service.createPatient(this.user);
          break;
        default:
          console.error('Unknown Role', this.mode);
          return;
      }
      observable.subscribe({
        next: () => {
          //this.notification.success(`Role ${this.user.name} successfully created.`);
          //this.router.navigate(['/dashboard']);
        },
        error: error => {
          console.error('Error creating User', error);
          //this.notification.error(this.errorFormatter.format(error), "Could not create User", {
          //enableHtml: true,
          //timeOut: 10000,
          //})
        }
      });
    }
  }


}
