import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LandingPatientComponent } from './landing-patient.component';

describe('LandingPatientComponent', () => {
  let component: LandingPatientComponent;
  let fixture: ComponentFixture<LandingPatientComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LandingPatientComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(LandingPatientComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
