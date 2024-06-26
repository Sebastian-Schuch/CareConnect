import {ComponentFixture, TestBed} from '@angular/core/testing';

import {MedicationCreateEditComponent} from './medication-create-edit.component';
import {HttpClientModule} from "@angular/common/http";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {ActivatedRoute} from "@angular/router";
import {FormsModule} from "@angular/forms";

describe('MedicationCreateComponent', () => {
  let component: MedicationCreateEditComponent;
  let fixture: ComponentFixture<MedicationCreateEditComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MedicationCreateEditComponent],
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

    fixture = TestBed.createComponent(MedicationCreateEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
