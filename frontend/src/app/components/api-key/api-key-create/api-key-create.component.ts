import {Component, OnInit} from '@angular/core';
import {MatButton} from "@angular/material/button";
import {MatDatepicker, MatDatepickerInput, MatDatepickerToggle} from "@angular/material/datepicker";
import {MatDialogActions, MatDialogContent, MatDialogRef, MatDialogTitle} from "@angular/material/dialog";
import {MatFormField, MatHint, MatLabel, MatSuffix} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {FormBuilder, FormControl, FormGroup, ReactiveFormsModule} from "@angular/forms";
import {ApiKeyService} from "../../../services/api-key.service";
import {ToastrService} from "ngx-toastr";
import {ErrorFormatterService} from "../../../services/error-formatter.service";
import {ApiKeyDtoCreate} from "../../../dtos/api-keys";

@Component({
  selector: 'app-api-key-create',
  standalone: true,
  imports: [
    MatButton,
    MatDatepicker,
    MatDatepickerInput,
    MatDatepickerToggle,
    MatDialogActions,
    MatDialogContent,
    MatDialogTitle,
    MatFormField,
    MatHint,
    MatInput,
    MatLabel,
    MatSuffix,
    ReactiveFormsModule
  ],
  templateUrl: './api-key-create.component.html',
  styleUrls: ['./api-key-create.component.scss', '../../../../styles.scss']
})
export class ApiKeyCreateComponent implements OnInit {

  createApiKey: FormGroup;
  description: FormControl;

  constructor(public dialogRef: MatDialogRef<ApiKeyCreateComponent>,
              private apiKeyService: ApiKeyService,
              private notification: ToastrService,
              private errorFormatterService: ErrorFormatterService,
              private formBuilder: FormBuilder) {
  }

  ngOnInit() {
    this.description = new FormControl('');
    this.createApiKey = this.formBuilder.group({
      // other form controls...
      description: this.description
    });
  }

  submit(): void {
    const newApiKey: ApiKeyDtoCreate = {
      description: this.createApiKey.get('description').value
    };
    this.apiKeyService.createApiKey(newApiKey).subscribe({
      next: data => {

        this.notification.success('API key created successfully', 'Success');
        this.close(data);
      },
      error: async error => {
        await this.errorFormatterService.printErrorToNotification(error, "Couldn't create API key", this.notification);
      }
    });
  }

  close(data: any): void {
    this.dialogRef.close(data);
  }
}
