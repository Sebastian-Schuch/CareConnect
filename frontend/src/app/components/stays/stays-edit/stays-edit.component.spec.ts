import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StaysEditComponent } from './stays-edit.component';

describe('StaysEditComponent', () => {
  let component: StaysEditComponent;
  let fixture: ComponentFixture<StaysEditComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StaysEditComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(StaysEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
