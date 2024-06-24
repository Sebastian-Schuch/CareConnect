import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ApiKeyViewComponent } from './api-key-view.component';

describe('ApiKeyViewComponent', () => {
  let component: ApiKeyViewComponent;
  let fixture: ComponentFixture<ApiKeyViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ApiKeyViewComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ApiKeyViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
