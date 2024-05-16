import { TestBed } from '@angular/core/testing';

import { StationService } from './station.service';
import {HttpClientModule} from "@angular/common/http";
import {StationComponent} from "../components/station/station.component";
import {HttpClientTestingModule} from "@angular/common/http/testing";

describe('StationService', () => {
  let service: StationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule, HttpClientTestingModule]
    });
    service = TestBed.inject(StationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
