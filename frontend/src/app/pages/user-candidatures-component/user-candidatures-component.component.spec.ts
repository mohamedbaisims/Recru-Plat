import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserCandidaturesComponentComponent } from './user-candidatures-component.component';

describe('UserCandidaturesComponentComponent', () => {
  let component: UserCandidaturesComponentComponent;
  let fixture: ComponentFixture<UserCandidaturesComponentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [UserCandidaturesComponentComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserCandidaturesComponentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
