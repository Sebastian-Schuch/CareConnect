import {Component, OnInit} from '@angular/core';
import {StationDto, StationDtoCreate} from "../../dtos/Station";
import {StationService} from "../../services/station.service";
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
  selector: 'app-station',
  templateUrl: './station.component.html',
  styleUrls: ['./station.component.scss', '../../../styles.scss'],
})

export class StationComponent implements OnInit {

  mode: InpatientDepartmentCreateEditMode = InpatientDepartmentCreateEditMode.create;

  ngOnInit(): void {
    this.route.data.subscribe(data => {
      this.route.params.subscribe(params => {
        this.mode = data.mode;
        if (this.mode == InpatientDepartmentCreateEditMode.edit) {
          if (isNaN(params['id']) === false) {
            this.stationService.getStationById(params['id']).subscribe({
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

  create: StationDtoCreate = {
    name: '',
    capacity: 0
  };

  update: StationDto = {
    id: undefined,
    name: "",
    capacity: 0
  };

  constructor(
    private stationService: StationService,
    private notification: ToastrService,
    private errorFormatterService: ErrorFormatterService,
    private route: ActivatedRoute,
    private router: Router
  ) {
  }

  getModel(): StationDto | StationDtoCreate {
    return this.mode === InpatientDepartmentCreateEditMode.create ? this.create : this.update;
  }

  public onSubmit(form: NgForm): void {
    if (form.valid) {
      let observable: Observable<StationDto>;
      if (this.mode == InpatientDepartmentCreateEditMode.create) {
        observable = this.stationService.createStation(this.create);
      } else if (this.mode == InpatientDepartmentCreateEditMode.edit) {
        observable = this.stationService.editStation(this.update);
      }

      observable.subscribe({
        next: data => {
          this.notification.success(`Successfully ${this.mode === InpatientDepartmentCreateEditMode.create ? "created" : "updated"} ${data.name} Station`);
          this.router.navigate(['/home/admin/inpatient-department']);
        },
        error: async error => {
          switch (error.status) {
            case 422:
              this.notification.error(this.errorFormatterService.format(JSON.parse(await error.error.text()).ValidationErrors), `Could not ${this.mode === InpatientDepartmentCreateEditMode.create ? "create" : "update"} Inpatient Department`, {
                enableHtml: true,
                timeOut: 10000
              });
              break;
            case 401:
              this.notification.error(await error.error.text(), `Could not ${this.mode === InpatientDepartmentCreateEditMode.create ? "create" : "update"} Inpatient Department`);
              this.router.navigate(['/']);
              break;
            default:
              this.notification.error(await error.error.text(), `Could not ${this.mode === InpatientDepartmentCreateEditMode.create ? "create" : "update"} Inpatient Department`);
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

  protected readonly InpatientDepartmentCreateEditMode = InpatientDepartmentCreateEditMode;
}
