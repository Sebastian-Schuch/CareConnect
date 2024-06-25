import {Component, EventEmitter, Input, Output, SimpleChanges} from '@angular/core';
import {MedicationDto} from "../../../dtos/medication";

function containsKeyName(obj: any): boolean {
  return obj && obj.hasOwnProperty('name');
}

@Component({
  selector: 'app-medication-list',
  templateUrl: './medication-list.component.html',
  styleUrl: './medication-list.component.scss'
})
export class MedicationListComponent {
  @Input() medication: MedicationDto[] = [];
  @Input() itemsPerPage = 10;
  @Output() pageChange = new EventEmitter<number>();

  searchQuery = '';
  totalPages: number[] = [];
  currentPage = 1;
  filteredMedications: MedicationDto[] = [];
  paginatedMedication: MedicationDto[] = [];

  ngOnInit() {


    this.filteredMedications = this.medication.filter(item => containsKeyName(item));
    this.updatePagination();
    console.log("loaded medication", this.medication);
  }

  ngOnChanges(changes: SimpleChanges) {
    if(changes.medication) {
      this.updatePagination();
    }
  }

  updatePagination() {
    this.filterMedications();
    this.calculateTotalPages();
    this.paginateMedications();
  }

  changePage(page: number) {
    if (page > 0 && page <= this.totalPages.length) {
      this.currentPage = page;
      this.paginateMedications();
      this.pageChange.emit(this.currentPage);
    }
  }

  paginateMedications() {
    const start = (this.currentPage - 1) * this.itemsPerPage;
    const end = start + this.itemsPerPage;
    this.paginatedMedication = this.filteredMedications.slice(start, end);
  }

  filterMedications() {
    this.medication = this.medication.filter(med => (med.name !== '' || med.name !== undefined) && containsKeyName("name"));

    if (this.searchQuery) {
      this.filteredMedications = this.medication.filter(medication =>
        medication.name.toLowerCase().includes(this.searchQuery.toLowerCase())
      );
    } else {
      this.filteredMedications = this.medication;
    }
    this.calculateTotalPages();
    this.paginateMedications();
  }

  calculateTotalPages() {
    this.totalPages = Array(Math.ceil(this.filteredMedications.length / this.itemsPerPage)).fill(0).map((x, i) => i + 1);
  }

  onSearchChange(query: string) {
    this.searchQuery = query;
    this.updatePagination();
  }

  get visibleMedications(): number[] {
    const maxVisiblePages = 5;
    const halfMax = Math.floor(maxVisiblePages / 2);
    let start = Math.max(this.currentPage - halfMax, 1);
    let end = Math.min(start + maxVisiblePages - 1, this.totalPages.length);

    if (end - start < maxVisiblePages - 1) {
      start = Math.max(end - maxVisiblePages + 1, 1);
    }

    return Array.from({ length: end - start + 1 }, (_, i) => start + i);
  }
}
