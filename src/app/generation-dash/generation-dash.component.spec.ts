import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { GenerationDashComponent } from './generation-dash.component';

describe('GenerationDashComponent', () => {
  let component: GenerationDashComponent;
  let fixture: ComponentFixture<GenerationDashComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ GenerationDashComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GenerationDashComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
