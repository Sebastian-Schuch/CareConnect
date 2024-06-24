import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MedicationDeleteComponent } from './medication-delete.component';

describe('MedicationDeleteComponent', () => {
  let component: MedicationDeleteComponent;
  let fixture: ComponentFixture<MedicationDeleteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MedicationDeleteComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(MedicationDeleteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
