import { NgClass } from '@angular/common';
import {
  ChangeDetectionStrategy,
  Component,
  inject,
  OnInit,
  signal,
} from '@angular/core';
import {
  FormControl,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatTableModule } from '@angular/material/table';
import { MatTabsModule } from '@angular/material/tabs';

import { ResponseSentiment } from '../../core/interfaces/sentiment-api';
import {
  CampoSeleccion,
  Valores,
} from '../../shared/ui/campo-seleccion/campo-seleccion';
import { Footer } from '../../shared/ui/footer/footer';
import { GraficoPie } from '../../shared/ui/grafico-pie/grafico-pie';
import { Header } from '../../shared/ui/header/header';
import { SentimentApiService } from './../../core/services/sentiment-api-service';

@Component({
  selector: 'app-inicio',
  imports: [
    Header,
    Footer,
    MatTabsModule,
    CampoSeleccion,
    FormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatTableModule,
    MatPaginatorModule,
    MatButtonModule,
    MatIconModule,
    ReactiveFormsModule,
    NgClass,
    GraficoPie,
  ],
  templateUrl: './inicio.html',
  styleUrl: './inicio.css',

  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Inicio implements OnInit {
  private _sentimentApiService = inject(SentimentApiService);
  textareaFormControl = new FormControl('', [Validators.required]);
  displayedColumnsTableIndividual: string[] = [
    'comentario',
    'sentimiento',
    'probabilidad',
  ];
  displayedColumnsTableMasiva: string[] = [
    'comentario',
    'sentimiento',
    'probabilidad',
    'acciones',
  ];
  dataSource = signal<ResponseSentiment[]>([]);
  formas: Valores[] = [
    { value: 'individual', viewValue: 'Individual' },
    { value: 'masiva', viewValue: 'Masiva' },
  ];
  formaAnalisis = 'individual';
  archivoCargado = signal<File | null>(null);
  sentimientoPredominante = signal<string>('');
  datosGraficoPie = signal<{ value: number; name: string }[]>([]);

  ngOnInit() {
    const campoGuardado = sessionStorage.getItem('forma-analisis');
    if (campoGuardado) {
      this.formaAnalisis = campoGuardado;
    }
  }
  cambiarFormaAnlisis(nuevoValor: string) {
    this.formaAnalisis = nuevoValor;
    sessionStorage.setItem('forma-analisis', nuevoValor);
    this._reiniciarFormulario();
  }
  cambiarArchivoGuardado(archivos: FileList | null) {
    if (archivos && archivos.length > 0) {
      this.archivoCargado.set(archivos[0]);
    } else {
      this.archivoCargado.set(null);
    }
  }
  eliminarArchivoCargado() {
    this.archivoCargado.set(null);
  }
  analizarComentarios() {
    if (this.formaAnalisis === 'individual') {
      if (this.textareaFormControl.valid) {
        const comentario = this.textareaFormControl.value!;
        this._sentimentApiService.analizar({ comentario }).subscribe({
          next: (response) => {
            this.dataSource.set([response]);
          },
        });
      }
    } else if (this.formaAnalisis === 'masiva') {
      if (this.archivoCargado()) {
        console.log('Analizando archivo CSV:', this.archivoCargado()?.name);
        this.dataSource.set([
          {
            id: 1,
            comentario:
              'Me encantó la experiencia, fue realmente satisfactoria',
            sentimiento: 'positivo',
            probabilidad: 0.95,
          },
        ]);
        this._resumenEstadistico();
      }
    }
  }
  eliminarRegistro(idResultado: number) {
    this.dataSource.set(
      this.dataSource().filter((resultado) => resultado.id !== idResultado),
    );
  }
  descartarAnalisis() {
    this._reiniciarFormulario();
  }
  // guardarAnalisis() {
  //   this._reiniciarFormulario();
  // }
  private _reiniciarFormulario() {
    this.dataSource.set([]);
    this.textareaFormControl.reset();
    this.eliminarArchivoCargado();
    sessionStorage.removeItem('forma-analisis');
  }
  private _resumenEstadistico() {
    let positivo = 0;
    // let neutro = 0;
    let negativo = 0;
    this.dataSource().forEach((registro) => {
      if (registro.sentimiento === 'positivo') positivo += 1;
      if (registro.sentimiento === 'negativo') negativo += 1;
    });
    this.sentimientoPredominante.set(
      positivo > negativo ? 'positivo' : 'negativo',
    );
    this.datosGraficoPie.set([
      {
        value: positivo,
        name: 'positivo',
      },
      {
        value: negativo,
        name: 'negativo',
      },
    ]);
  }
}
