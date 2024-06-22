import {Component, OnInit} from '@angular/core';
import {DatePipe, NgForOf} from "@angular/common";
import {MatButton} from "@angular/material/button";
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell, MatHeaderCellDef,
  MatHeaderRow,
  MatHeaderRowDef,
  MatRow, MatRowDef, MatTable
} from "@angular/material/table";
import {MatIcon} from "@angular/material/icon";
import {MatPaginator} from "@angular/material/paginator";
import {ApiKeyService} from "../../services/api-key.service";
import {MatDialog} from "@angular/material/dialog";
import {ApiKeyCreateComponent} from "./api-key-create/api-key-create.component";
import {ApiKeyViewComponent} from "./api-key-view/api-key-view.component";
import {ApiKeyDto} from "../../dtos/api-keys";
import {ApiKeyDeleteComponent} from "./api-key-delete/api-key-delete.component";

@Component({
  selector: 'app-api-key',
  standalone: true,
  imports: [
    DatePipe,
    MatButton,
    MatCell,
    MatCellDef,
    MatColumnDef,
    MatHeaderCell,
    MatHeaderRow,
    MatHeaderRowDef,
    MatIcon,
    MatRow,
    MatRowDef,
    MatTable,
    MatHeaderCellDef,
    MatPaginator,
    NgForOf
  ],
  templateUrl: './api-key.component.html',
  styleUrls:  ['./api-key.component.scss', "../../../styles.scss"]
})
export class ApiKeyComponent implements OnInit {
    dataSource: any;
    displayedColumns: string[] = ['description', 'created', 'delete'];
    length: number = 0;

    constructor(private apiKeyService: ApiKeyService,
                private dialog: MatDialog) {
    }

    ngOnInit() {
      this.loadApiKeys(null);
    }

  public loadApiKeys(event?: any){
    const pageIndex = event ? event.pageIndex : 0;
    const pageSize = event ? event.pageSize : 5;

      this.apiKeyService.getApiKeysAll(pageIndex, pageSize).subscribe((data) => {
        this.dataSource = data.apiKeys;
        this.length = data.totalElements;
      });
    }

  openApiKeyCreateDialog() {
    const dialogRef = this.dialog.open(ApiKeyCreateComponent);

    dialogRef.afterClosed().subscribe(result => {
      if(result!=null){
        this.dialog.open(ApiKeyViewComponent, { data: result });
      }
      this.loadApiKeys()
    });
  }

  deleteApiKey(apiKey: ApiKeyDto) {
      this.dialog.open(ApiKeyDeleteComponent, { data: apiKey }).afterClosed().subscribe(() => {
          this.loadApiKeys();
      });
  }

}
