import {Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {AllergyDto, AllergyDtoCreate} from "../../dtos/allergy";
import {AllergyService} from "../../services/allergy.service";
import {NgForm, NgModel} from "@angular/forms";
import {Observable} from "rxjs";
import {ToastrService} from "ngx-toastr";
import {Router} from "@angular/router";
import {ErrorFormatterService} from "../../services/error-formatter.service";
import {CsvConverterService} from "../../services/csv-converter.service";
import {NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";

@Component({
  selector: 'app-allergy',
  templateUrl: './allergy.component.html',
  styleUrl: './allergy.component.scss'
})
export class AllergyComponent implements OnInit {

  @ViewChild('content', { static: true }) content!: TemplateRef<any>;
  private modalRef!: NgbModalRef;

  isCollapsed = true;
  isJsonCollapsed: boolean = true;

  allergyname: string = '';

  public jsonData: any;
  public error: string | null = null;
  public currentPageNewAllergies = 1;
  public itemsPerPageNewAllergies = 10;
  public totalPagesNewAllergies: number[] = [];


  public currentPage = 1;
  public itemsPerPage = 10;
  public totalPages: number[] = [];


  public existingAllergies: { [key: string]: boolean } = {};
  public allergies: AllergyDto[] = [];

  constructor(
    private allergyService: AllergyService,
    private notification: ToastrService,
    private errorFormatterService: ErrorFormatterService,
    private router: Router,
    private csvService: CsvConverterService,
    private modalService: NgbModal
  ) {
  }

  ngOnInit(): void {
    this.loadAllergies();
  }

  openModal() {
    this.modalRef = this.modalService.open(this.content);
  }

  closeModal() {
    this.modalRef.close();
  }

  loadAllergies(): void {
    this.allergyService.getAllergiesAll().subscribe({
      next: data => {
        this.allergies = data;
        this.existingAllergies = this.allergies.reduce((acc, allergy) => {
          acc[allergy.name] = true;
          return acc;
        }, {});

        this.calculateTotalPages();
        this.paginateAllergies();
      },
      error: error => {
        this.notification.error('Error loading allergies: ' + error.message);
      }
    });
  }

  public onSubmit(form: NgForm): void {
    if (form.valid) {
      const allergy: AllergyDtoCreate = {
        name: this.allergyname
      };
      let observable: Observable<AllergyDtoCreate> = this.allergyService.createAllergy(allergy);

      observable.subscribe({
        next: data => {
          this.notification.success('Successfully created ' + data.name + ' Allergy');
          this.loadAllergies();
        },
        error: async error => {
          switch (error.status) {
            case 422:
              this.notification.error(this.errorFormatterService.format(JSON.parse(await error.error.text()).ValidationErrors), `Could not create Allergy`, {
                enableHtml: true,
                timeOut: 10000
              });
              break;
            case 401:
              this.notification.error(await error.error.text(), `Could not create Allergy`);
              this.router.navigate(['/']);
              break;
            case 409:
              this.notification.error('Allergy already exists', `Could not create Allergy`);
              break;
            default:
              this.notification.error(await error.error.text(), `Could not create Allergy`);
              break;
          }
        }
      });
    }
  }

  public dynamicCssClassesForInput(input: NgModel): any {
    return {
      'is-invalid': !input.valid && !input.pristine,
    };
  }

  checkExists(name: string): boolean {
    return this.existingAllergies[name] === true;
  }

  onFileChange(event: any) {
    const file: File = event.target.files[0];

    if (file) {
      this.csvService.parseCsv(file).then(
        (data) => {
          this.jsonData = data;
          this.error = null;
          this.calculateTotalPages();
          this.paginateData();
        },
        (error) => {
          this.error = error.message;
          this.jsonData = null;
        }
      );
    }
  }

  calculateTotalPages() {
    if(this.jsonData != null)
    this.totalPagesNewAllergies = Array(Math.ceil(this.jsonData.length / this.itemsPerPageNewAllergies)).fill(0).map((x, i) => i + 1);
    this.totalPages = Array(Math.ceil(this.allergies.length / this.itemsPerPage)).fill(0).map((x, i) => i + 1);
  }

  changePage(page: number) {
    if (page > 0 && page <= this.totalPagesNewAllergies.length) {
      this.currentPageNewAllergies = page;
      this.paginateData();
    }
  }

  get paginatedAllergies() {
    const start = (this.currentPage - 1) * this.itemsPerPage;
    const end = start + this.itemsPerPage;
    return this.allergies.slice(start, end);
  }

  changePageAllergies(page: number) {
    if (page > 0 && page <= this.totalPages.length) {
      this.currentPage = page;
      this.paginateAllergies();
    }
  }

  paginateAllergies() {
    this.paginatedAllergies;
  }

  get paginatedData() {
    const start = (this.currentPageNewAllergies - 1) * this.itemsPerPageNewAllergies;
    const end = start + this.itemsPerPageNewAllergies;
    return this.jsonData.slice(start, end);
  }

  paginateData() {
    this.paginatedData;
  }

  insertAll(){
  if (this.jsonData && this.jsonData.length > 0) {
      const observables: Observable<AllergyDtoCreate>[] = this.jsonData.map((entry: any) => {
        const allergy: AllergyDtoCreate = {
          name: entry.allergie // Adjust the key based on your CSV structure
        };
        return this.allergyService.createAllergy(allergy);
      });

      observables.forEach(observable => {
        observable.subscribe({
          next: data => {
            this.notification.success('Successfully created ' + data.name + ' Allergy');
          },
          error: async error => {
            switch (error.status) {
              case 422:
                this.notification.error(this.errorFormatterService.format(JSON.parse(await error.error.text()).ValidationErrors), `Could not create Allergy`, {
                  enableHtml: true,
                  timeOut: 10000
                });
                break;
              case 401:
                this.notification.error(await error.error.text(), `Could not create Allergy`);
                this.router.navigate(['/']);
                break;
              default:
                this.notification.error(await error.error.text(), `Could not create Allergy`);
                break;
            }
          }
        });
      });
    }
  }



}
