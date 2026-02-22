import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { provideHttpClient } from '@angular/common/http';
import { TableEditorComponent } from './components/table-editor/table-editor.component';
import { CalculationResponse, FuelTable } from './models/fuel-table.model';
import { VeApiService } from './services/ve-api.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, TableEditorComponent],
  providers: [provideHttpClient()],
  templateUrl: './app.component.html'
})
export class AppComponent {
  tabs = ['VE Current', 'AFR Actual', 'AFR Target', 'VE Corrected'];
  activeTab = this.tabs[0];

  warning = '';
  result?: CalculationResponse;
  globalAfrTarget = new FormControl<number>(14.7, { nonNullable: true });

  veCurrent = this.createDefaultTable();
  afrActual = this.createDefaultTable();
  afrTarget = this.createDefaultTable();

  constructor(private api: VeApiService) {}

  generate(): void {
    this.warning = '';
    this.api.calculate({ veCurrent: this.veCurrent, afrActual: this.afrActual, afrTarget: this.afrTarget }).subscribe({
      next: res => {
        this.result = res;
        this.checkWarning(res.deltaPercent.values.flat());
        this.activeTab = 'VE Corrected';
      },
      error: err => {
        this.warning = err?.error?.error ?? 'Calculation failed.';
      }
    });
  }

  applyGlobalTarget(): void {
    const value = this.globalAfrTarget.value;
    this.afrTarget.values = this.afrTarget.values.map(row => row.map(() => value));
  }

  smooth3x3(): void {
    if (!this.result) return;
    const original = this.result.veCorrected.values;
    const smoothed = original.map((row, r) => row.map((_, c) => {
      let sum = 0; let count = 0;
      for (let rr = r - 1; rr <= r + 1; rr++) {
        for (let cc = c - 1; cc <= c + 1; cc++) {
          if (original[rr]?.[cc] !== undefined) { sum += original[rr][cc]; count++; }
        }
      }
      return Math.round((sum / count) * 100) / 100;
    }));
    this.result.veCorrected.values = smoothed;
  }

  exportCsv(): void {
    if (!this.result) return;
    const t = this.result.veCorrected;
    const lines = [',' + t.rpmAxis.join(',')];
    t.mapAxis.forEach((map, r) => lines.push(map + ',' + t.values[r].join(',')));
    const blob = new Blob([lines.join('\n')], { type: 'text/csv' });
    const a = document.createElement('a');
    a.href = URL.createObjectURL(blob);
    a.download = 've-corrected.csv';
    a.click();
    URL.revokeObjectURL(a.href);
  }

  reset(): void {
    this.result = undefined;
    this.warning = '';
    this.veCurrent = this.createDefaultTable();
    this.afrActual = this.createDefaultTable();
    this.afrTarget = this.createDefaultTable();
    this.activeTab = 'VE Current';
  }

  private createDefaultTable(): FuelTable {
    return {
      rpmAxis: [1000, 2000, 3000],
      mapAxis: [40, 60, 80],
      values: [
        [50, 55, 60],
        [60, 65, 70],
        [70, 75, 80]
      ]
    };
  }

  private checkWarning(deltas: number[]): void {
    if (deltas.some(d => Math.abs(d) > 30)) {
      this.warning = 'Warning: one or more cells exceed ±30% delta.';
    }
  }
}
