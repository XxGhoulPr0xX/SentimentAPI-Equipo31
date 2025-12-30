import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input,
  Output,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';

export interface Valores {
  value: string;
  viewValue: string;
}

@Component({
  selector: 'app-campo-seleccion',
  imports: [MatFormFieldModule, MatSelectModule, MatInputModule, FormsModule],
  templateUrl: './campo-seleccion.html',
  styleUrl: './campo-seleccion.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CampoSeleccion {
  @Input() appearance: 'fill' | 'outline' = 'fill';
  @Input({ required: true }) seleccion = '';
  @Input({ required: true }) valores: Valores[] = [];
  @Output() cambiosSeleccion = new EventEmitter<string>();

  onCambiosSeleccion(nuevoValor: string) {
    this.seleccion = nuevoValor;
    this.cambiosSeleccion.emit(this.seleccion);
  }
}
