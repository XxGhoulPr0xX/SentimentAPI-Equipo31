import { DecimalPipe, NgClass } from '@angular/common';
import { HttpErrorResponse, HttpStatusCode } from '@angular/common/http';
import {
  ChangeDetectionStrategy,
  Component,
  inject,
  OnInit,
  signal,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
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
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import { MatTabsModule } from '@angular/material/tabs';
import { merge } from 'rxjs';

import { SentimentResponse } from '../../core/interfaces/sentiment-api';
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
    DecimalPipe,
  ],
  templateUrl: './inicio.html',
  styleUrl: './inicio.css',

  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Inicio implements OnInit {
  private _snackBar = inject(MatSnackBar);
  private _sentimentApiService = inject(SentimentApiService);
  textarea = new FormControl('', [
    Validators.required,
    Validators.minLength(10),
    Validators.maxLength(5000),
  ]);
  errorMessage = signal('');
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
  dataSource = signal<SentimentResponse[]>([]);
  formas: Valores[] = [
    { value: 'individual', viewValue: 'Individual' },
    { value: 'masiva', viewValue: 'Masiva' },
  ];
  formaAnalisis = 'individual';
  archivoCargado = signal<File | null>(null);
  sentimientoPredominante = signal<string>('');
  datosGraficoPie = signal<{ value: number; name: string }[]>([]);

  constructor() {
    merge(this.textarea.statusChanges, this.textarea.valueChanges)
      .pipe(takeUntilDestroyed())
      .subscribe(() => this.actualizarMensajeErrorTextarea());
  }
  ngOnInit() {
    const campoGuardado = sessionStorage.getItem('forma-analisis');
    if (campoGuardado) {
      this.formaAnalisis = campoGuardado;
    }
  }
  actualizarMensajeErrorTextarea() {
    if (this.textarea.hasError('required')) {
    this.errorMessage.set('Comentario obligatorio');
    return;
  }
  if (this.textarea.hasError('minlength')) {
    this.errorMessage.set('Mínimo 10 caracteres');
    return;
  }
  if (this.textarea.hasError('maxlength')) {
    this.errorMessage.set('Máximo 5000 caracteres');
    return;
  }
  this.errorMessage.set('');
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
      this.textarea.markAsTouched();

      const text = (this.textarea.value ?? '').trim();
    if (!text) return;

    if (this.textarea.valid) {
      this._sentimentApiService.analizar({ text }).subscribe({
          next: (response) => {
            this.dataSource.set([response]);
            this._snackBar.open(
              'Tu comentario fue analizado correctamente.',
              'Cerrar',
              {
                duration: 10000,
              },
            );
          },
          error: (e: HttpErrorResponse) => {
            let mensaje = 'Ocurrió un error.';
            if (
              e.status === 0 ||
              e.status === HttpStatusCode.InternalServerError
            ) {
              mensaje = 'Error inesperado, vuelva a intentarlo más tarde.';
            } else if (e.status === HttpStatusCode.BadRequest) {
              mensaje = 'Parece que has cometido errores.';
            }
            this._snackBar.open(mensaje, 'Cerrar', {
              duration: 10000,
            });
          },
        });
      }
    } else if (this.formaAnalisis === 'masiva') {
      if (this.archivoCargado()) {
        console.log('Analizando archivo CSV:', this.archivoCargado()?.name);
        this.dataSource.set([
          {
            id: 1,
            text: 'Me encantó la experiencia, fue realmente satisfactoria',
            prevision: 'Positivo',
            probabilidad: 0.95,
            createdAt: '2025-12-30T23:22:42.9809864',
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
    this.textarea.reset();
    this.eliminarArchivoCargado();
    sessionStorage.removeItem('forma-analisis');
  }
  private _resumenEstadistico() {
    let positivo = 0;
    let neutro = 0;
    let negativo = 0;

    this.dataSource().forEach((registro) => {
      if (registro.prevision === 'Positivo') {
        positivo++;
      } else if (registro.prevision === 'Neutro') {
        neutro++;
      } else if (registro.prevision === 'Negativo') {
        negativo++;
      }
    });

    let predominante = 'Neutro';
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
