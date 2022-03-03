import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DataPageViewComponent } from './data-page-view.component';

describe('DataPageViewComponent', () => {
  let component: DataPageViewComponent;
  let fixture: ComponentFixture<DataPageViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DataPageViewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DataPageViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
