import {Component, Inject, OnInit} from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from "@angular/material/dialog";
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatError, MatFormField, MatLabel} from "@angular/material/form-field";
import {MatButton} from "@angular/material/button";
import {MatInput} from "@angular/material/input";
import {NgIf} from "@angular/common";
import {eq} from "lodash";
import {AuthRequest} from "../../../dtos/auth-request";
import {ToastrService} from 'ngx-toastr';
import {UserService} from "../../../services/user.service";

@Component({
  selector: 'app-change-password-form-modal',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatFormField,
    MatDialogActions,
    MatButton,
    MatDialogClose,
    MatInput,
    MatDialogContent,
    MatDialogTitle,
    MatError,
    MatLabel,
    NgIf
  ],
  templateUrl: './change-password-form-modal.component.html',
  styleUrl: './change-password-form-modal.component.scss'
})
export class ChangePasswordFormModalComponent implements OnInit {


  changePasswordForm: FormGroup;

  constructor(public dialogRef: MatDialogRef<ChangePasswordFormModalComponent>, private fb: FormBuilder, private userService: UserService, @Inject(MAT_DIALOG_DATA) public data: any, private notification: ToastrService) {
    this.changePasswordForm = this.fb.group({
      newPw: ['', Validators.required],
      newPwRepeat: ['', Validators.required]
    });
  }

  ngOnInit(): void {

  }

  get newPwControl() {
    return this.changePasswordForm.get('newPw');
  }

  get newPwRepeatControl() {
    return this.changePasswordForm.get('newPwRepeat');
  }

  public equalPasswords(): boolean {
    return this.newPwControl.value === this.newPwRepeatControl.value;
  }

  /*
  * Submit the medication form if the form is valid and close the dialog
   */
  onSubmit() {
    if (this.changePasswordForm.valid && this.equalPasswords()) {
      let auth: AuthRequest = {
        email: this.data.email,
        password: this.newPwRepeatControl.value
      }
      let observable = this.userService.changePassword(auth);
      observable.subscribe({
        next: () => {
          this.notification.success('Password changed successfully');
          this.dialogRef.close();
        },
        error: error => {
          console.error('There was an error!', error);
        }

      })
    }
  }

  protected readonly eq = eq;
}
