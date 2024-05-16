import { TestBed } from '@angular/core/testing';

import { TreatmentService } from './treatment.service';
import {HttpClientModule} from "@angular/common/http";
import {HttpClientTestingModule} from "@angular/common/http/testing";

describe('TreatmentService', () => {
  let service: TreatmentService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule, HttpClientTestingModule]
    });
    service = TestBed.inject(TreatmentService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
