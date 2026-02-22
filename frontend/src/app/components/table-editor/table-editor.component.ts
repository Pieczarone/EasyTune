import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { FuelTable } from '../../models/fuel-table.model';

@Component({
  selector: 'app-table-editor',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './table-editor.component.html'
})
export class TableEditorComponent {
  @Input({ required: true }) table!: FuelTable;
  @Input() readonly = false;
  @Input() showDeltaHighlight = false;
  @Output() tableChange = new EventEmitter<FuelTable>();

  addRpm(): void {
    const newRpm = (this.table.rpmAxis.at(-1) ?? 0) + 500;
    this.table.rpmAxis.push(newRpm);
    this.table.values.forEach(row => row.push(0));
    this.emitChange();
  }

  addMap(): void {
    const newMap = (this.table.mapAxis.at(-1) ?? 0) + 10;
    this.table.mapAxis.push(newMap);
    this.table.values.push(new Array(this.table.rpmAxis.length).fill(0));
    this.emitChange();
  }

  removeRpm(index: number): void {
    this.table.rpmAxis.splice(index, 1);
    this.table.values.forEach(row => row.splice(index, 1));
    this.emitChange();
  }

  removeMap(index: number): void {
    this.table.mapAxis.splice(index, 1);
    this.table.values.splice(index, 1);
    this.emitChange();
  }

  moveRpm(index: number, direction: -1 | 1): void {
    this.swap(this.table.rpmAxis, index, index + direction);
    this.table.values.forEach(row => this.swap(row, index, index + direction));
    this.emitChange();
  }

  moveMap(index: number, direction: -1 | 1): void {
    this.swap(this.table.mapAxis, index, index + direction);
    this.swap(this.table.values, index, index + direction);
    this.emitChange();
  }

  shouldHighlight(value: number): boolean {
    return this.showDeltaHighlight && Math.abs(value) > 30;
  }

  importAxisCsv(event: Event, axis: 'rpm' | 'map'): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;

    file.text().then(text => {
      const vals = text.split(/[\n,;]/).map(v => Number(v.trim())).filter(v => !Number.isNaN(v));
      if (axis === 'rpm') {
        this.table.rpmAxis = vals;
        this.table.values = this.table.mapAxis.map((_, i) => this.table.values[i]?.slice(0, vals.length).concat(new Array(Math.max(vals.length - (this.table.values[i]?.length ?? 0), 0)).fill(0)) ?? new Array(vals.length).fill(0));
      } else {
        this.table.mapAxis = vals;
        this.table.values = vals.map((_, i) => this.table.values[i] ?? new Array(this.table.rpmAxis.length).fill(0));
      }
      this.emitChange();
    });
  }

  private swap<T>(arr: T[], a: number, b: number): void {
    if (b < 0 || b >= arr.length) return;
    [arr[a], arr[b]] = [arr[b], arr[a]];
  }

  private emitChange(): void {
    this.tableChange.emit(structuredClone(this.table));
  }
}
