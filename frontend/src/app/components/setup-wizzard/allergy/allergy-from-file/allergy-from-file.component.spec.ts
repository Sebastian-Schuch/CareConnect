import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AllergyFromFileComponent } from './allergy-from-file.component';

describe('AllergyFromFileComponent', () => {
  let component: AllergyFromFileComponent;
  let fixture: ComponentFixture<AllergyFromFileComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AllergyFromFileComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(AllergyFromFileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
