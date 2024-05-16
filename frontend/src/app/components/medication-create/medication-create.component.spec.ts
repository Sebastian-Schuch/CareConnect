import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MedicationCreateComponent } from './medication-create.component';
import {HttpClientModule} from "@angular/common/http";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {ActivatedRoute} from "@angular/router";
import {FormsModule} from "@angular/forms";

describe('MedicationCreateComponent', () => {
  let component: MedicationCreateComponent;
  let fixture: ComponentFixture<MedicationCreateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MedicationCreateComponent],
      imports: [HttpClientModule, HttpClientTestingModule, FormsModule],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: {
                get(): string {
                  return '1';
                }
              }
            }
          }
        }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MedicationCreateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
