import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OutpatientDepartmentDetailComponent } from './outpatient-department-detail.component';
import {HttpClientModule} from "@angular/common/http";
import {FormsModule} from "@angular/forms";
import {HttpClientTestingModule} from "@angular/common/http/testing";

describe('OutpatientDepartmentDetailComponent', () => {
  let component: OutpatientDepartmentDetailComponent;
  let fixture: ComponentFixture<OutpatientDepartmentDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OutpatientDepartmentDetailComponent ],
      imports: [FormsModule, HttpClientModule, HttpClientTestingModule]
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
