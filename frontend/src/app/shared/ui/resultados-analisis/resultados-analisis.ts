import { DecimalPipe } from '@angular/common';
import {
  ChangeDetectionStrategy,
  Component,
  computed,
  input,
  output,
  signal,
} from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatTableModule } from '@angular/material/table';

import { SentimentResponse } from '../../../core/interfaces/sentiment-api';
import { EtiquetaSentimiento } from '../etiqueta-sentimiento/etiqueta-sentimiento';
import { ResumenEstadistico } from '../resumen-estadistico/resumen-estadistico';

@Component({
  selector: 'app-resultados-analisis',
  imports: [
    MatTableModule,
    DecimalPipe,
    MatIcon,
    MatPaginatorModule,
    MatButtonModule,
    EtiquetaSentimiento,
    ResumenEstadistico,
  ],
  templateUrl: './resultados-analisis.html',
  styleUrl: './resultados-analisis.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ResultadosAnalisis {
  formaAnalisis = input.required<string>();
  displayedColumnsTable = computed(() => {
    const cols = ['comentario', 'sentimiento', 'probabilidad'];
    return this.formaAnalisis() === 'masiva' ? [...cols, 'acciones'] : cols;
  });
  allItems = input.required<SentimentResponse[]>();
  mensajeError = input<string | null>(null);
  pageIndex = signal(0);
  pageSize = signal(10);
  currentPageItems = computed(() => {
    const start = this.pageIndex() * this.pageSize();
    const end = start + this.pageSize();
    return this.allItems().slice(start, end);
  });
  onPageChange(event: PageEvent) {
    this.pageIndex.set(event.pageIndex);
    this.pageSize.set(event.pageSize);
  }
  registroEliminado = output<number>();
  eliminarRegistro(idResultado: number) {
    this.registroEliminado.emit(idResultado);
    if (
      this.expandedRow &&
      (this.expandedRow as SentimentResponse).id === idResultado
    ) {
      this.expandedRow = null;
    }
  }
  expandedRow: SentimentResponse | null = null;
  isExpanded(row: SentimentResponse) {
    return this.expandedRow === row;
  }
  toggle(row: SentimentResponse) {
    this.expandedRow = this.isExpanded(row) ? null : row;
  }
}
