import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MedicationFromFileComponent } from './medication-from-file.component';

describe('MedicationFromFileComponent', () => {
  let component: MedicationFromFileComponent;
  let fixture: ComponentFixture<MedicationFromFileComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MedicationFromFileComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(MedicationFromFileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
