import {Component, EventEmitter, Input, Output, SimpleChanges} from '@angular/core';
import {InpatientDepartmentDto} from "../../../dtos/inpatient-department";

@Component({
  selector: 'app-small-list',
  templateUrl: './small-list.component.html',
  styleUrl: './small-list.component.scss'
})
export class SmallListComponent {


  @Input() inpatientDepartments: InpatientDepartmentDto[] = [];
  @Input() itemsPerPage = 10;
  @Output() pageChange = new EventEmitter<number>();

  searchQuery = '';
  filteredInpatientDepartments: InpatientDepartmentDto[] = [];
  paginatedInpatientDepartments: InpatientDepartmentDto[] = [];
  currentPage = 1;
  totalPages: number[] = [];

  ngOnInit() {
    this.updatePagination();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.allergies) {
      this.updatePagination();
    }
  }

  updatePagination() {
    this.filterAllergies();
    this.calculateTotalPages();
    this.paginateAllergies();
  }

  filterAllergies() {
    if (this.searchQuery) {
      this.filteredInpatientDepartments = this.inpatientDepartments.filter(allergy =>
        allergy.name.toLowerCase().includes(this.searchQuery.toLowerCase())
      );
    } else {
      this.filteredInpatientDepartments = this.inpatientDepartments;
    }
    this.calculateTotalPages();
    this.paginateAllergies();
  }

  calculateTotalPages() {
    this.totalPages = Array(Math.ceil(this.filteredInpatientDepartments.length / this.itemsPerPage)).fill(0).map((x, i) => i + 1);
  }

  changePage(page: number) {
    if (page > 0 && page <= this.totalPages.length) {
      this.currentPage = page;
      this.paginateAllergies();
      this.pageChange.emit(this.currentPage);
    }
  }

  paginateAllergies() {
    const start = (this.currentPage - 1) * this.itemsPerPage;
    const end = start + this.itemsPerPage;
    this.paginatedInpatientDepartments = this.filteredInpatientDepartments.slice(start, end);
  }

  onSearchChange(query: string) {
    this.searchQuery = query;
    this.updatePagination();
  }

  get visiblePages() {
    const maxVisiblePages = 5;
    const halfMax = Math.floor(maxVisiblePages / 2);
    let start = Math.max(this.currentPage - halfMax, 1);
    let end = Math.min(start + maxVisiblePages - 1, this.totalPages.length);

    if (end - start < maxVisiblePages - 1) {
      start = Math.max(end - maxVisiblePages + 1, 1);
    }

    return Array.from({length: end - start + 1}, (_, i) => start + i);
  }
}
