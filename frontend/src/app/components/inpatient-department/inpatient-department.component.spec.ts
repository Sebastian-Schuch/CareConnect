import {ComponentFixture, TestBed} from '@angular/core/testing';

import {InpatientDepartmentComponent} from './inpatient-department.component';
import {HttpClientModule} from "@angular/common/http";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {FormsModule} from "@angular/forms";

describe('InpatientDepartmentComponent', () => {
  let component: InpatientDepartmentComponent;
  let fixture: ComponentFixture<InpatientDepartmentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HttpClientModule, HttpClientTestingModule, FormsModule],
      declarations: [InpatientDepartmentComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(InpatientDepartmentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
