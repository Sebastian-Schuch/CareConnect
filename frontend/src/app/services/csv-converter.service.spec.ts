import { TestBed } from '@angular/core/testing';

import { CsvConverterService } from './csv-converter.service';

describe('CsvConverterService', () => {
  let service: CsvConverterService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CsvConverterService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
