import {ComponentFixture, TestBed} from '@angular/core/testing';

import {OutpatientDepartmentComponent} from './outpatient-department-create-edit.component';
import {HttpClientModule} from "@angular/common/http";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {FormsModule} from "@angular/forms";

describe('OutpatientDepartmentComponent', () => {
  let component: OutpatientDepartmentComponent;
  let fixture: ComponentFixture<OutpatientDepartmentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [OutpatientDepartmentComponent],
      imports: [HttpClientModule, HttpClientTestingModule, FormsModule]
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
