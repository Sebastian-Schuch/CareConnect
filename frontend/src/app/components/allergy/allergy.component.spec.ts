import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AllergyComponent } from './allergy.component';
import {HttpClientModule} from "@angular/common/http";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {FormsModule, NgModel} from "@angular/forms";

describe('AllergyComponent', () => {
  let component: AllergyComponent;
  let fixture: ComponentFixture<AllergyComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AllergyComponent],
      imports: [HttpClientModule, HttpClientTestingModule, FormsModule]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AllergyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
