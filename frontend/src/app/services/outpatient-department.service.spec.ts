import { TestBed } from '@angular/core/testing';

import { OutpatientDepartmentService } from './outpatient-department.service';

describe('OutpatientDepartmentService', () => {
  let service: OutpatientDepartmentService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OutpatientDepartmentService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
