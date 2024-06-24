import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ApiKeyDeleteComponent } from './api-key-delete.component';

describe('ApiKeyDeleteComponent', () => {
  let component: ApiKeyDeleteComponent;
  let fixture: ComponentFixture<ApiKeyDeleteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ApiKeyDeleteComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ApiKeyDeleteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
