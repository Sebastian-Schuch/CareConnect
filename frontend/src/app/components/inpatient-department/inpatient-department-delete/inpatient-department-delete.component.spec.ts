import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InpatientDepartmentDeleteComponent } from './inpatient-department-delete.component';

describe('InpatientDepartmentDeleteComponent', () => {
  let component: InpatientDepartmentDeleteComponent;
  let fixture: ComponentFixture<InpatientDepartmentDeleteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InpatientDepartmentDeleteComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(InpatientDepartmentDeleteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
