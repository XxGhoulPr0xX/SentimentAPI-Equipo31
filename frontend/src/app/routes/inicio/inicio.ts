import { HttpErrorResponse, HttpStatusCode } from '@angular/common/http';
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
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import { MatTabsModule } from '@angular/material/tabs';

import { SentimentResponse } from '../../core/interfaces/sentiment-api';
import {
  CampoSeleccion,
  Valores,
} from '../../shared/ui/campo-seleccion/campo-seleccion';
import { Footer } from '../../shared/ui/footer/footer';
import { Header } from '../../shared/ui/header/header';
import { ResultadosAnalisis } from '../../shared/ui/resultados-analisis/resultados-analisis';
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
    MatButtonModule,
    MatIconModule,
    ReactiveFormsModule,
    ResultadosAnalisis,
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
  ]);
  input = new FormControl('', [Validators.required, Validators.minLength(2)]);
  formas: Valores[] = [
    { value: 'individual', viewValue: 'Individual' },
    { value: 'masiva', viewValue: 'Masiva' },
  ];
  idiomas: Valores[] = [
    { value: 'es', viewValue: 'Español' },
    { value: 'en', viewValue: 'English' },
  ];
  idiomaAnalisis = 'es';
  formaAnalisis = 'individual';
  archivoCargado = signal<File | null>(null);
  resultadosAnalisis = signal<SentimentResponse[]>([]);
  ngOnInit() {
    const campoGuardado = sessionStorage.getItem('forma-analisis');
    if (campoGuardado) {
      this.formaAnalisis = campoGuardado;
    }
  }
  cambiarFormaAnlisis(nuevoValor: string) {
    this.formaAnalisis = nuevoValor;
    sessionStorage.setItem('forma-analisis', nuevoValor);
    this._reiniciar();
  }
  cambiarIdiomaAnlisis(nuevoValor: string) {
    this.idiomaAnalisis = nuevoValor;
    this._sentimentApiService.configurarIdioma(this.idiomaAnalisis).subscribe({
      next: (body) => {
        console.log(body);
      },
    });
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
    const inputFile = document.getElementById('file') as HTMLInputElement;
    if (inputFile) inputFile.value = '';
  }
  analizarComentarios() {
    if (this.formaAnalisis === 'individual') {
      if (this.textarea.valid) {
        const text = this.textarea.value as string;
        this._sentimentApiService.analizarComentario({ text }).subscribe({
          next: (response) => {
            this.resultadosAnalisis.set([response]);
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
      if (this.archivoCargado() && this.input.valid) {
        const archivo = this.archivoCargado() as File;
        const columnName = this.input.value as string;
        const formData = new FormData();
        formData.append('file', archivo);
        formData.append('columnName', columnName);
        this._sentimentApiService.analizarArchivo(formData).subscribe({
          next: (response) => {
            this.resultadosAnalisis.set(response);
            this._snackBar.open(
              'Tu archivo fue analizado correctamente.',
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
    }
  }
  eliminarRegistro(id: number) {
    this.resultadosAnalisis.update((items) =>
      items.filter((item) => item.id !== id),
    );
  }
  descartarAnalisis() {
    this._reiniciar();
  }
  // guardarAnalisis() {
  //   this._reiniciarFormulario();
  // }
  private _reiniciar() {
    this.resultadosAnalisis.set([]);
    this.textarea.reset();
    this.input.reset();
    this.eliminarArchivoCargado();
  }
}
