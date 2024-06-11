import {Component, OnInit} from '@angular/core';
import {UserDtoList, UserDtoSearch} from "../../../dtos/user";
import {FormsModule} from "@angular/forms";
import {Role} from "../../../dtos/Role";
import {ActivatedRoute, RouterLink} from "@angular/router";
import {NgForOf, NgIf} from "@angular/common";
import {MatIcon} from "@angular/material/icon";
import {MatButton} from "@angular/material/button";
import {UserService} from "../../../services/user.service";
import {ToastrService} from "ngx-toastr";
import {MatDialog} from "@angular/material/dialog";
import {DeleteUserDialogComponent} from "../delete-user-dialog/delete-user-dialog.component";
import {StaysManageComponent} from "../../stays/stays-manage/stays-manage.component";

@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [
    FormsModule,
    RouterLink,
    NgIf,
    NgForOf,
    MatIcon,
    MatButton
  ],
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.scss', '../../../../styles.scss']
})
export class UserListComponent implements OnInit {

  role: Role = null;

  users: UserDtoList[] = [];

  searchParams: UserDtoSearch = {
    limit: 100
  };

  constructor(private service: UserService, private route: ActivatedRoute, private notification: ToastrService, private dialog: MatDialog) {
  }

  public ngOnInit() {
    this.route.data.subscribe(data => {
      this.role = data.role;
      this.loadUsers();
    });
  }

  public get roleString(): string {
    switch (this.role) {
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

  private loadUsers() {
    let observable;
    switch (this.role) {
      case Role.patient:
        observable = this.service.searchPatient(this.searchParams);
        break;
      case Role.doctor:
        observable = this.service.searchDoctor(this.searchParams);
        break;
      case Role.secretary:
        observable = this.service.searchSecretary(this.searchParams);
        break;
      case Role.admin:
        observable = this.service.searchAdmin(this.searchParams);
        break;
      default:
        console.error('Invalid role');
        return;
    }
    observable.subscribe({
      next: users => {
        this.users = users;
      },
      error: error => {
        this.notification.error('Error loading users', 'Error');
        console.error('Error loading users', error);
      }
    });
  }

  public openDeleteModal(email: string) {
    const dialogRef = this.dialog.open(DeleteUserDialogComponent, {
      width: '500px', data: {email: email, role: this.role}
    });

    dialogRef.componentInstance.deletedUser.subscribe(() => {
      this.reloadUsers();
    });
  }

  public stayMenu(user: UserDtoList){
    this.dialog.open(StaysManageComponent, {
      data: user
    });
  }

  public reloadUsers() {
    this.loadUsers();
  }

  public searchChanged() {
    this.loadUsers();
  }

  get isPatientList() {
    return this.role === Role.patient;
  }
}
