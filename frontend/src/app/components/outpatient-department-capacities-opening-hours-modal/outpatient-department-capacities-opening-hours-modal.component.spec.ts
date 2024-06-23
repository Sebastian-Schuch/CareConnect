import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OutpatientDepartmentCapacitiesOpeningHoursModalComponent } from './outpatient-department-capacities-opening-hours-modal.component';

describe('OutpatientDepartmentCapacitiesOpeningHoursModalComponent', () => {
  let component: OutpatientDepartmentCapacitiesOpeningHoursModalComponent;
  let fixture: ComponentFixture<OutpatientDepartmentCapacitiesOpeningHoursModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OutpatientDepartmentCapacitiesOpeningHoursModalComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(OutpatientDepartmentCapacitiesOpeningHoursModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
