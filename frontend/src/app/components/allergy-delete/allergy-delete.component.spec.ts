import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AllergyDeleteComponent } from './allergy-delete.component';

describe('AllergyDeleteComponent', () => {
  let component: AllergyDeleteComponent;
  let fixture: ComponentFixture<AllergyDeleteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AllergyDeleteComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(AllergyDeleteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
