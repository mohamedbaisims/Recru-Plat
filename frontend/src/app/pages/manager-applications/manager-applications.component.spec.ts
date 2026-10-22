import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManagerApplicationsComponent } from './manager-applications.component';

describe('ManagerApplicationsComponent', () => {
  let component: ManagerApplicationsComponent;
  let fixture: ComponentFixture<ManagerApplicationsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ManagerApplicationsComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ManagerApplicationsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
