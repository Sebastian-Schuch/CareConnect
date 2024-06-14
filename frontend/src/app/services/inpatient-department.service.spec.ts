import {TestBed} from '@angular/core/testing';

import {InpatientDepartmentService} from './inpatient-department.service';
import {HttpClientModule} from "@angular/common/http";
import {HttpClientTestingModule} from "@angular/common/http/testing";

describe('InpatientDepartmentService', () => {
  let service: InpatientDepartmentService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule, HttpClientTestingModule]
    });
    service = TestBed.inject(InpatientDepartmentService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
