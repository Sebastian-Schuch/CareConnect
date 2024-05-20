import {Component, OnInit} from '@angular/core';
import {StationCreateDto} from "../../dtos/Station";
import {StationService} from "../../services/station.service";
import {NgForm, NgModel} from "@angular/forms";
import {Observable} from "rxjs";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-station',
  templateUrl: './station.component.html',
  styleUrl: './station.component.scss',
})

export class StationComponent implements OnInit {

  ngOnInit(): void {
  }

  station: StationCreateDto = {
    name: '',
    capacity: 0
  };

  constructor(
    private stationService: StationService,
    private notification: ToastrService
  ) {
  }

  public onSubmit(form: NgForm): void {
    console.log('is form valid?', form.valid, this.station);
    if (form.valid) {
      let observable: Observable<StationCreateDto> = this.stationService.createStation(this.station);

      observable.subscribe({
        next: data => {
          this.notification.success('Successfully created ' + data.name + ' Station');
          //this.router.navigate(['/dashboard']);
        },
        error: error => {
          console.error('Error creating User', error);
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
