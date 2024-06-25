import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MedicationFormModalComponent } from './medication-form-modal.component';

describe('MedicationFormModalComponent', () => {
  let component: MedicationFormModalComponent;
  let fixture: ComponentFixture<MedicationFormModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MedicationFormModalComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(MedicationFormModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
