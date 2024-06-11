import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StaysManageComponent } from './stays-manage.component';

describe('StaysComponent', () => {
  let component: StaysManageComponent;
  let fixture: ComponentFixture<StaysManageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StaysManageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(StaysManageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
