import {ComponentFixture, TestBed} from '@angular/core/testing';

import {CalenderComponent} from './calender.component';
import {CalendarDateFormatter, CalendarModule, CalendarUtils, DateAdapter} from "angular-calendar";
import {adapterFactory} from "angular-calendar/date-adapters/date-fns";
import {NoopAnimationsModule} from "@angular/platform-browser/animations";

describe('CalenderComponent', () => {
  let component: CalenderComponent;
  let fixture: ComponentFixture<CalenderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CalenderComponent],
      imports: [
        CalendarModule.forRoot({
          provide: DateAdapter,
          useFactory: adapterFactory,
        }),
        NoopAnimationsModule
      ],
      providers: [
        CalendarDateFormatter,
        CalendarUtils
      ],
    })
      .compileComponents();

    fixture = TestBed.createComponent(CalenderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
