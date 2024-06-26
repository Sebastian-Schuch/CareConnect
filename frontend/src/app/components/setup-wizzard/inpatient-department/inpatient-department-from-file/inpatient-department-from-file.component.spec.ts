import {ComponentFixture, TestBed} from '@angular/core/testing';

import {InpatientDepartmentFromFileComponent} from './inpatient-department-from-file.component';

describe('InpatientDepartmentFromCsvComponent', () => {
  let component: InpatientDepartmentFromFileComponent;
  let fixture: ComponentFixture<InpatientDepartmentFromFileComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InpatientDepartmentFromFileComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(InpatientDepartmentFromFileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
