import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FilteredCandidatesByDomainComponent } from './filtered-candidates-by-domain.component';

describe('FilteredCandidatesByDomainComponent', () => {
  let component: FilteredCandidatesByDomainComponent;
  let fixture: ComponentFixture<FilteredCandidatesByDomainComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FilteredCandidatesByDomainComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(FilteredCandidatesByDomainComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
