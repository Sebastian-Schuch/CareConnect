import {ComponentFixture, TestBed} from '@angular/core/testing';

import {UserCreateComponent} from './user-create.component';
import {HttpClientModule} from "@angular/common/http";
import {ActivatedRoute} from "@angular/router";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {FormsModule} from "@angular/forms";
import {of} from "rxjs";

describe('UserCreateComponent', () => {
  let component: UserCreateComponent;
  let fixture: ComponentFixture<UserCreateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [UserCreateComponent],
      imports: [HttpClientModule, HttpClientTestingModule, FormsModule],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({mode: 'testMode'}) // provide a stub value for 'data' observable
          }
        }
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(UserCreateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
