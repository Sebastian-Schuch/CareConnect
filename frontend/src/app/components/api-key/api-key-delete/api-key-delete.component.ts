import {Component, Inject} from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from "@angular/material/dialog";
import {ApiKeyService} from "../../../services/api-key.service";
import {ToastrService} from "ngx-toastr";
import {ErrorFormatterService} from "../../../services/error-formatter.service";
import {ApiKeyDto} from "../../../dtos/api-keys";
import {MatButton} from "@angular/material/button";

@Component({
  selector: 'app-api-key-delete',
  standalone: true,
  imports: [
    MatButton,
    MatDialogActions,
    MatDialogContent,
    MatDialogTitle
  ],
  templateUrl: './api-key-delete.component.html',
  styleUrls: ['./api-key-delete.component.scss', '../../../../styles.scss']
})
export class ApiKeyDeleteComponent {

  apiKeyDto: ApiKeyDto = null;

  constructor(@Inject(MAT_DIALOG_DATA) public data: ApiKeyDto,
              public dialogRef: MatDialogRef<ApiKeyDeleteComponent>,
              private apiKeyService: ApiKeyService,
              private notification: ToastrService,
              private errorFormatterService: ErrorFormatterService) {
    this.apiKeyDto = data;
  }

  submit(): void {
    this.apiKeyService.deleteApiKey(this.apiKeyDto.id).subscribe({
      next: () => {
        this.notification.success('Api key deleted successfully', 'Success');
        this.close()
      },
      error: error => {
        this.notification.error(this.errorFormatterService.format(error), 'Error');
        this.close();
      }
    });
  }

  close(): void {
    this.dialogRef.close();
  }

}
