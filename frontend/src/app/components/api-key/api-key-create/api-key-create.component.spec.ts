import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ApiKeyCreateComponent } from './api-key-create.component';

describe('ApiKeyCreateComponent', () => {
  let component: ApiKeyCreateComponent;
  let fixture: ComponentFixture<ApiKeyCreateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ApiKeyCreateComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ApiKeyCreateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
