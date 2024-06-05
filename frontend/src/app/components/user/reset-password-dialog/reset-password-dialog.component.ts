import {Component, Inject} from '@angular/core';
import {MatButton} from "@angular/material/button";
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from "@angular/material/dialog";
import {ToastrService} from "ngx-toastr";
import {UserService} from "../../../services/user.service";
import {AuthRequest} from "../../../dtos/auth-request";

@Component({
  selector: 'app-reset-password-dialog',
  standalone: true,
  imports: [
    MatButton,
    MatDialogActions,
    MatDialogClose,
    MatDialogContent,
    MatDialogTitle
  ],
  templateUrl: './reset-password-dialog.component.html',
  styleUrl: './reset-password-dialog.component.scss'
})
export class ResetPasswordDialogComponent {

  constructor(public dialogRef: MatDialogRef<ResetPasswordDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: any, private notification: ToastrService, private userService: UserService) {
  }

  public resetUserPassword() {
    let email: AuthRequest = {
      email: this.data.user.email,
      password: ''
    }
    let observable = this.userService.resetPassword(email);
    observable.subscribe({
      next: (response) => {
        this.notification.success('Password of user ' + this.data.user.firstname + ' ' + this.data.user.lastname + ' has been reset');
        const url = window.URL.createObjectURL(response);
        window.open(url);
        this.dialogRef.close();
      },
      error: error => {
        this.notification.error('Password could not be reset');
        console.error('There was an error!', error);
      }
    });
  }
}
