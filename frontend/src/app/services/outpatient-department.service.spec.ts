import { TestBed } from '@angular/core/testing';

import { OutpatientDepartmentService } from './outpatient-department.service';
import {HttpClientModule} from "@angular/common/http";
import {HttpClientTestingModule} from "@angular/common/http/testing";

describe('OutpatientDepartmentService', () => {
  let service: OutpatientDepartmentService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule, HttpClientTestingModule]
    });
    service = TestBed.inject(OutpatientDepartmentService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
