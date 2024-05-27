import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LandingSecretaryComponent } from './landing-secretary.component';

describe('LandingSecretaryComponent', () => {
  let component: LandingSecretaryComponent;
  let fixture: ComponentFixture<LandingSecretaryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LandingSecretaryComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(LandingSecretaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
