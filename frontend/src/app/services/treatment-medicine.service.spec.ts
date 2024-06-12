import { TestBed } from '@angular/core/testing';

import { TreatmentMedicineService } from './treatment-medicine.service';

describe('TreatmentMedicineService', () => {
  let service: TreatmentMedicineService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TreatmentMedicineService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
