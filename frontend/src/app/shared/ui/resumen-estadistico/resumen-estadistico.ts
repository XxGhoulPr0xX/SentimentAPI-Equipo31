import {
  ChangeDetectionStrategy,
  Component,
  effect,
  input,
  signal,
} from '@angular/core';

import {
  SentimentResponse,
  Sentimiento,
} from '../../../core/interfaces/sentiment-api';
import { EtiquetaSentimiento } from '../etiqueta-sentimiento/etiqueta-sentimiento';
import { DataGraphicPie, GraficoPie } from '../grafico-pie/grafico-pie';

@Component({
  selector: 'app-resumen-estadistico',
  imports: [GraficoPie, EtiquetaSentimiento],
  templateUrl: './resumen-estadistico.html',
  styleUrl: './resumen-estadistico.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ResumenEstadistico {
  allItems = input.required<SentimentResponse[]>();
  datosGraficoPie = signal<DataGraphicPie[]>([]);
  sentimientoPredominante = signal<Sentimiento>('');
  constructor() {
    effect(() => {
      if (this.allItems().length === 0) {
        this.datosGraficoPie.set([]);
        this.sentimientoPredominante.set('');
      } else this._resumenEstadistico();
    });
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
