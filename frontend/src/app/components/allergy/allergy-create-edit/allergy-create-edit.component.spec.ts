import {ComponentFixture, TestBed} from '@angular/core/testing';

import {AllergyCreateEditComponent} from './allergy-create-edit.component';
import {HttpClientModule} from "@angular/common/http";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {FormsModule} from "@angular/forms";

describe('AllergyComponent', () => {
  let component: AllergyCreateEditComponent;
  let fixture: ComponentFixture<AllergyCreateEditComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AllergyCreateEditComponent],
      imports: [HttpClientModule, HttpClientTestingModule, FormsModule]
    })
      .compileComponents();

    fixture = TestBed.createComponent(AllergyCreateEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
