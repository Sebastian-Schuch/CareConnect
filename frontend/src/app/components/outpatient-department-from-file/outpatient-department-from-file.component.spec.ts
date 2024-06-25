import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OutpatientDepartmentFromFileComponent } from './outpatient-department-from-file.component';

describe('OutpatientDepartmentFromFileComponent', () => {
  let component: OutpatientDepartmentFromFileComponent;
  let fixture: ComponentFixture<OutpatientDepartmentFromFileComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OutpatientDepartmentFromFileComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(OutpatientDepartmentFromFileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
