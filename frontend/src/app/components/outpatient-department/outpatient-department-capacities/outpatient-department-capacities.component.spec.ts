import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OutpatientDepartmentCapacitiesComponent } from './outpatient-department-capacities.component';

describe('OutpatientDepartmentCapacitiesComponent', () => {
  let component: OutpatientDepartmentCapacitiesComponent;
  let fixture: ComponentFixture<OutpatientDepartmentCapacitiesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OutpatientDepartmentCapacitiesComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(OutpatientDepartmentCapacitiesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
