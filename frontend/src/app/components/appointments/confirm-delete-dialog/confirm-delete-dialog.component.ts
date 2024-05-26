import {Component, EventEmitter, HostBinding, Input, OnInit, Output} from '@angular/core';

@Component({
  selector: 'app-confirm-delete-dialog',
  standalone: true,
  imports: [],
  templateUrl: './confirm-delete-dialog.component.html',
  styleUrl: './confirm-delete-dialog.component.scss'
})
export class ConfirmDeleteDialogComponent implements OnInit {
  @Input() deleteWhat = '?';
  @Output() confirm = new EventEmitter<void>();

  @HostBinding('class') cssClass = 'modal fade';

  constructor() {
  }

  ngOnInit(): void {
  }

}
