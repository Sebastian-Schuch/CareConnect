import {Component, OnInit} from '@angular/core';
import {StationDtoCreate} from "../../dtos/Station";
import {StationService} from "../../services/station.service";
import {NgForm, NgModel} from "@angular/forms";
import {Observable} from "rxjs";
import {ToastrService} from "ngx-toastr";
import {ErrorFormatterService} from "../../services/error-formatter.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-station',
  templateUrl: './station.component.html',
  styleUrl: './station.component.scss',
})

export class StationComponent implements OnInit {

  ngOnInit(): void {
  }

  station: StationDtoCreate = {
    name: '',
    capacity: 0
  };

  constructor(
    private stationService: StationService,
    private notification: ToastrService,
    private errorFormatterService: ErrorFormatterService,
    private router: Router
  ) {
  }

  public onSubmit(form: NgForm): void {
    if (form.valid) {
      let observable: Observable<StationDtoCreate> = this.stationService.createStation(this.station);

      observable.subscribe({
        next: data => {
          this.notification.success('Successfully created ' + data.name + ' Station');
          //this.router.navigate(['/dashboard']);
        },
        error: async error => {
          switch (error.status) {
            case 422:
              this.notification.error(this.errorFormatterService.format(JSON.parse(await error.error.text()).ValidationErrors), `Could not create Inpatient Department`, {
                enableHtml: true,
                timeOut: 10000
              });
              break;
            case 401:
              this.notification.error(await error.error.text(), `Could not create Inpatient Department`);
              this.router.navigate(['/']);
              break;
            default:
              this.notification.error(await error.error.text(), `Could not create Inpatient Department`);
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

}
