import { DecimalPipe } from '@angular/common';
import {
  ChangeDetectionStrategy,
  Component,
  computed,
  effect,
  input,
  output,
  signal,
} from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatTableModule } from '@angular/material/table';

import {
  SentimentResponse,
  Sentimiento,
} from '../../../core/interfaces/sentiment-api';
import { EtiquetaSentimiento } from '../etiqueta-sentimiento/etiqueta-sentimiento';
import { DataGraphicPie, GraficoPie } from '../grafico-pie/grafico-pie';

@Component({
  selector: 'app-resultados-analisis',
  imports: [
    MatTableModule,
    DecimalPipe,
    MatIcon,
    MatPaginatorModule,
    MatButtonModule,
    EtiquetaSentimiento,
    GraficoPie,
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
  sentimientoPredominante = signal<Sentimiento>('');
  datosGraficoPie = signal<DataGraphicPie[]>([]);
  allItems = input.required<SentimentResponse[]>();
  pageIndex = signal(0);
  pageSize = signal(10);
  currentPageItems = computed(() => {
    const start = this.pageIndex() * this.pageSize();
    const end = start + this.pageSize();
    return this.allItems().slice(start, end);
  });
  constructor() {
    effect(() => {
      if (this.allItems().length === 0) {
        this.datosGraficoPie.set([]);
        this.sentimientoPredominante.set('');
      } else this._resumenEstadistico();
    });
  }
  onPageChange(event: PageEvent) {
    this.pageIndex.set(event.pageIndex);
    this.pageSize.set(event.pageSize);
  }
  registroEliminado = output<number>();
  eliminarRegistro(idResultado: number) {
    this.registroEliminado.emit(idResultado);
    this._resumenEstadistico();
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
  private _resumenEstadistico() {
    if (this.allItems().length === 0) return;

    let positivo = 0;
    let neutro = 0;
    let negativo = 0;

    this.allItems().forEach((registro) => {
      if (registro.prevision === 'Positivo') {
        positivo++;
      } else if (registro.prevision === 'Neutro') {
        neutro++;
      } else if (registro.prevision === 'Negativo') {
        negativo++;
      }
    });

    let predominante: Sentimiento = 'Neutro';
    if (positivo > neutro && positivo > negativo) {
      predominante = 'Positivo';
    } else if (negativo > positivo && negativo > neutro) {
      predominante = 'Negativo';
    }

    this.sentimientoPredominante.set(predominante);

    this.datosGraficoPie.set([
      { value: positivo, name: 'Positivo' },
      { value: negativo, name: 'Negativo' },
      { value: neutro, name: 'Neutro' },
    ]);
  }
}
