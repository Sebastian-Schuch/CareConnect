import { TestBed } from '@angular/core/testing';

import { AllergyService } from './allergy.service';
import {HttpClientModule} from "@angular/common/http";
import {HttpClientTestingModule} from "@angular/common/http/testing";

describe('AllergyService', () => {
  let service: AllergyService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule, HttpClientTestingModule]
    });
    service = TestBed.inject(AllergyService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
