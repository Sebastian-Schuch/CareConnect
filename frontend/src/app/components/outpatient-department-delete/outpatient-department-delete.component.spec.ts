import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OutpatientDepartmentDeleteComponent } from './outpatient-department-delete.component';

describe('OutpatientDepartmentDeleteComponent', () => {
  let component: OutpatientDepartmentDeleteComponent;
  let fixture: ComponentFixture<OutpatientDepartmentDeleteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OutpatientDepartmentDeleteComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(OutpatientDepartmentDeleteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
