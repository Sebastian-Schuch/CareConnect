import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InpatientDepartmentFromCsvComponent } from './inpatient-department-from-csv.component';

describe('InpatientDepartmentFromCsvComponent', () => {
  let component: InpatientDepartmentFromCsvComponent;
  let fixture: ComponentFixture<InpatientDepartmentFromCsvComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InpatientDepartmentFromCsvComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(InpatientDepartmentFromCsvComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
