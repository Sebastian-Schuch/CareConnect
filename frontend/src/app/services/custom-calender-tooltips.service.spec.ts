import {TestBed} from '@angular/core/testing';

import {CustomCalenderTooltipsService} from './custom-calender-tooltips.service';

describe('CustomCalenderTooltipsService', () => {
  let service: CustomCalenderTooltipsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CustomCalenderTooltipsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
