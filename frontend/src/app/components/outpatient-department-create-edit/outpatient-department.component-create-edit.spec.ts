import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OutpatientDepartmentComponent } from './outpatient-department-create-edit.component';

describe('OutpatientDepartmentComponent', () => {
  let component: OutpatientDepartmentComponent;
  let fixture: ComponentFixture<OutpatientDepartmentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OutpatientDepartmentComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OutpatientDepartmentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
