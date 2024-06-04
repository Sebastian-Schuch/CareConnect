import {Component, EventEmitter, Inject, Output} from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from "@angular/material/dialog";
import {ToastrService} from "ngx-toastr";
import {Role} from "../../../dtos/Role";
import {MatButton} from "@angular/material/button";
import {UserService} from "../../../services/user.service";
import {AuthRequest} from "../../../dtos/auth-request";

@Component({
  selector: 'app-delete-user-form-modal',
  standalone: true,
  imports: [
    MatDialogTitle,
    MatButton,
    MatDialogActions,
    MatDialogClose,
    MatDialogContent
  ],
  templateUrl: './delete-user-dialog.component.html',
  styleUrl: './delete-user-dialog.component.scss'
})
export class DeleteUserDialogComponent {

  constructor(public dialogRef: MatDialogRef<DeleteUserDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: any, private notification: ToastrService, private userService: UserService) {
  }

  @Output() deletedUser = new EventEmitter<any>();

  /*
  * Submit the medication form if the form is valid and close the dialog
   */
  deleteUser() {
    let email: AuthRequest = {
      email: this.data.email,
      password: ''
    }
    let observable = this.userService.disableUser(email);
    observable.subscribe({
      next: () => {
        this.notification.success('User deleted');
        this.deletedUser.emit();
        this.dialogRef.close();
      },
      error: error => {
        this.notification.error('User could not be deleted');
        console.error('There was an error!', error);
      }
    });
  }

  public get roleString(): string {
    switch (this.data.role) {
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
}
