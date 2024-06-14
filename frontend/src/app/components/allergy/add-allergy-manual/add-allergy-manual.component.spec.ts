import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddAllergyManualComponent } from './add-allergy-manual.component';

describe('AddAllergyManualComponent', () => {
  let component: AddAllergyManualComponent;
  let fixture: ComponentFixture<AddAllergyManualComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddAllergyManualComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(AddAllergyManualComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
