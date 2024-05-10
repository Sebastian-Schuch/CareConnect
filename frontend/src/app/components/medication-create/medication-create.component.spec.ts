import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MedicationCreateComponent } from './medication-create.component';

describe('MedicationCreateComponent', () => {
  let component: MedicationCreateComponent;
  let fixture: ComponentFixture<MedicationCreateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MedicationCreateComponent]
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
