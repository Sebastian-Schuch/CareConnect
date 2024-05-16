import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TreatmentComponent } from './treatment.component';
import {HttpClientModule} from "@angular/common/http";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {MatAutocompleteModule} from "@angular/material/autocomplete";

describe('TreatmentComponent', () => {
  let component: TreatmentComponent;
  let fixture: ComponentFixture<TreatmentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TreatmentComponent],
      imports: [HttpClientModule, HttpClientTestingModule, MatAutocompleteModule]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TreatmentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
