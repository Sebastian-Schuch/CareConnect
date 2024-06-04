import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChangePasswordFormModalComponent } from './change-password-form-modal.component';

describe('ChangePasswordFormModalComponent', () => {
  let component: ChangePasswordFormModalComponent;
  let fixture: ComponentFixture<ChangePasswordFormModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChangePasswordFormModalComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ChangePasswordFormModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
