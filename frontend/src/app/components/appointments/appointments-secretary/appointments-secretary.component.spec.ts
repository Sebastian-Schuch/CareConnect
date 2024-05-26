import {ComponentFixture, TestBed} from '@angular/core/testing';

import {AppointmentsSecretaryComponent} from './appointments-secretary.component';

describe('AppointmentsSecretaryComponent', () => {
  let component: AppointmentsSecretaryComponent;
  let fixture: ComponentFixture<AppointmentsSecretaryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppointmentsSecretaryComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(AppointmentsSecretaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
