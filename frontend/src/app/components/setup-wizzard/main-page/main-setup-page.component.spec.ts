import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MainSetupPage } from './main-setup-page.component';

describe('MainPageComponent', () => {
  let component: MainSetupPage;
  let fixture: ComponentFixture<MainSetupPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MainSetupPage]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MainSetupPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
