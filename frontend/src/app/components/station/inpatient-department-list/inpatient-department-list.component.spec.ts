import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InpatientDepartmentListComponent } from './inpatient-department-list.component';

describe('InpatientDepartmentListComponent', () => {
  let component: InpatientDepartmentListComponent;
  let fixture: ComponentFixture<InpatientDepartmentListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InpatientDepartmentListComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(InpatientDepartmentListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
