import {Component, OnInit} from '@angular/core';
import {InpatientDepartmentDto, InpatientDepartmentDtoCreate} from "../../dtos/inpatient-department";
import {InpatientDepartmentService} from "../../services/inpatient-department.service";
import {NgForm, NgModel} from "@angular/forms";
import {Observable} from "rxjs";
import {ToastrService} from "ngx-toastr";
import {ErrorFormatterService} from "../../services/error-formatter.service";
import {ActivatedRoute, Router} from "@angular/router";

export enum InpatientDepartmentCreateEditMode {
  create,
  edit
}

@Component({
  selector: 'app-inpatientDepartment',
  templateUrl: './inpatient-department.component.html',
  styleUrls: ['./inpatient-department.component.scss', '../../../styles.scss'],
})

export class InpatientDepartmentComponent implements OnInit {

  mode: InpatientDepartmentCreateEditMode = InpatientDepartmentCreateEditMode.create;

  ngOnInit(): void {
    this.route.data.subscribe(data => {
      this.route.params.subscribe(params => {

        this.mode = data.mode;
        if (this.mode == InpatientDepartmentCreateEditMode.edit) {
          if (isNaN(params['id']) === false) {
            this.inpatientDepartmentService.getInpatientDepartmentById(params['id']).subscribe({
              next: data => this.update = data,
              error: error => console.error(error)
            })
          } else {
            this.router.navigate(['/home/admin/inpatient-department']);
          }
        }
      })
    });
  }

  create: InpatientDepartmentDtoCreate = {
    name: '',
    capacity: 0
  };

  update: InpatientDepartmentDto = {
    id: undefined,
    name: "",
    capacity: 0
  };

  constructor(
    private inpatientDepartmentService: InpatientDepartmentService,
    private notification: ToastrService,
    private errorFormatterService: ErrorFormatterService,
    private route: ActivatedRoute,
    private router: Router
  ) {
  }

  getModel(): InpatientDepartmentDto | InpatientDepartmentDtoCreate {
    return this.mode === InpatientDepartmentCreateEditMode.create ? this.create : this.update;
  }

  public onSubmit(form: NgForm): void {
    if (form.valid) {
      let observable: Observable<InpatientDepartmentDto>;
      if (this.mode == InpatientDepartmentCreateEditMode.create) {
        observable = this.inpatientDepartmentService.createInpatientDepartment(this.create);
      } else if (this.mode == InpatientDepartmentCreateEditMode.edit) {
        observable = this.inpatientDepartmentService.editInpatientDepartment(this.update);
      }

      observable.subscribe({
        next: data => {
          this.notification.success(`Successfully ${this.mode === InpatientDepartmentCreateEditMode.create ? "created" : "updated"} ${data.name} Inpatient Department`);
          this.router.navigate(['/home/admin/inpatient-department']);
        },
        error: async error => {
          await this.errorFormatterService.printErrorToNotification(error, `Error ${this.mode === InpatientDepartmentCreateEditMode.create ? "created" : "updated"} Inpatient Department`, this.notification);
        }
      });
    }
  }

  public dynamicCssClassesForInput(input: NgModel): any {
    return {
      'is-invalid': !input.valid && !input.pristine,
    };
  }

  protected readonly InpatientDepartmentCreateEditMode = InpatientDepartmentCreateEditMode;
}
