import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OutpatientDepartmentDetailComponent } from './outpatient-department-detail.component';

describe('OutpatientDepartmentDetailComponent', () => {
  let component: OutpatientDepartmentDetailComponent;
  let fixture: ComponentFixture<OutpatientDepartmentDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OutpatientDepartmentDetailComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(OutpatientDepartmentDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
