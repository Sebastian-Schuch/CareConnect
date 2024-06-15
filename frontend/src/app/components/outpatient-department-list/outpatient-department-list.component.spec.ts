import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OutpatientDepartmentListComponent } from './outpatient-department-list.component';

describe('OutpatientDepartmentListComponent', () => {
  let component: OutpatientDepartmentListComponent;
  let fixture: ComponentFixture<OutpatientDepartmentListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OutpatientDepartmentListComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(OutpatientDepartmentListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
