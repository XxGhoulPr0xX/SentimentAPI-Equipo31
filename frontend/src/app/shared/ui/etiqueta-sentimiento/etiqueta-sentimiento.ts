import { NgClass } from '@angular/common';
import { ChangeDetectionStrategy, Component, input } from '@angular/core';

import { Sentimiento } from '../../../core/interfaces/sentiment-api';

@Component({
  selector: 'app-etiqueta-sentimiento',
  imports: [NgClass],
  templateUrl: './etiqueta-sentimiento.html',
  styleUrl: './etiqueta-sentimiento.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EtiquetaSentimiento {
  sentimiento = input.required<Sentimiento>();
}
