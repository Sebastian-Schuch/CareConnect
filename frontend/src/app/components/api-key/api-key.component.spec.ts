import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ApiKeyComponent } from './api-key.component';

describe('ApiKeyComponent', () => {
  let component: ApiKeyComponent;
  let fixture: ComponentFixture<ApiKeyComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ApiKeyComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ApiKeyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
