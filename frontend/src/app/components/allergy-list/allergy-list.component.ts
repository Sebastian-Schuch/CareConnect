import {Component, OnInit} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatIcon} from "@angular/material/icon";
import {MatPaginator, PageEvent} from "@angular/material/paginator";
import {NgForOf, NgIf} from "@angular/common";
import {AllergyDto} from "../../dtos/allergy";
import {Router} from "@angular/router";
import {AllergyService} from "../../services/allergy.service";
import {ToastrService} from "ngx-toastr";
import {ErrorFormatterService} from "../../services/error-formatter.service";

@Component({
  selector: 'app-allergy-list',
  standalone: true,
  imports: [
    FormsModule,
    MatIcon,
    MatPaginator,
    NgForOf,
    NgIf,
    ReactiveFormsModule
  ],
  templateUrl: './allergy-list.component.html',
  styleUrls: ['./allergy-list.component.scss', '../../../styles.scss']
})
export class AllergyListComponent implements OnInit {

  constructor(private router: Router, private allergyService: AllergyService, private errorFormatterService: ErrorFormatterService, private notificationService: ToastrService) {
  }

  totalItems: number = 0;
  allergies: AllergyDto[] = [];
  allergyName: string = '';
  size: number = 10;
  page: number = 0;

  public reloadAllergies(): void {
    this.allergyService.searchAllergies(this.allergyName, this.page, this.size).subscribe({
      next: allergyPage => {
        this.allergies = allergyPage.allergies;
        this.totalItems = allergyPage.totalItems;
        console.log(allergyPage);
      },
      error: async error => {
        await this.errorFormatterService.printErrorToNotification(error, "Error loading Allergies", this.notificationService);
      }
    });
  }

  onPageChange($event: PageEvent) {
    this.size = $event.pageSize;
    this.page = $event.pageIndex;
    this.reloadAllergies();
  }

  public navigateToEditAllergy(allergyId: number): void {
    this.router.navigate(['home/admin/allergies/' + allergyId + '/edit']);
  }

  ngOnInit(): void {
    this.reloadAllergies();
  }
}
