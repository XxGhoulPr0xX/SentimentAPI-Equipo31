import { DatePipe, DecimalPipe } from '@angular/common';
import { HttpErrorResponse, HttpStatusCode } from '@angular/common/http';
import {
  ChangeDetectionStrategy,
  Component,
  inject,
  OnInit,
  signal,
} from '@angular/core';
import {
  AbstractControl,
  FormControl,
  FormsModule,
  ReactiveFormsModule,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
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
    MatCardModule,
    DatePipe,
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
    this.noSoloEspaciosValidator,
  ]);
  input = new FormControl('', [
    Validators.required,
    Validators.minLength(2),
    this.noSoloEspaciosValidator,
  ]);
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
  indexSeleccionado = 0;
  archivoCargado = signal<File | null>(null);
  resultadosAnalisis = signal<SentimentResponse[]>([]);
  datosAlmacenados = signal<SentimentResponse[]>([]);
  mensajeError = signal<string | null>(null);
  ngOnInit() {
    this._cargarSessionStorage();
    this._cargarDatosAlmacenados();
  }
  manejarIndexSeleccionado(index: number) {
    this.indexSeleccionado = index;
    sessionStorage.setItem('tab-seleccionado', String(index));
  }
  cambiarFormaAnlisis(nuevoValor: string) {
    this.formaAnalisis = nuevoValor;
    sessionStorage.setItem('forma-analisis', nuevoValor);
    this._reiniciar();
  }
  cambiarIdiomaAnlisis(nuevoValor: string) {
    this.idiomaAnalisis = nuevoValor;
    sessionStorage.setItem('idioma-analisis', nuevoValor);
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
    // Limpiar error y resultados previos al iniciar nuevo análisis
    this.mensajeError.set(null);
    this.resultadosAnalisis.set([]);

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
            this._cargarDatosAlmacenados();
          },
          error: (e: HttpErrorResponse) => {
            let mensaje = 'Ocurrió un error inesperado.';
            let descripcion = '';
            if (e.error && e.error.error) {
              mensaje = e.error.error;
            } else if (e.status === 0) {
              mensaje = 'No se pudo conectar con el servidor.';
              descripcion =
                'Verifica que el servicio de análisis esté en ejecución o revisa tu conexión a internet.';
            } else if (e.status === HttpStatusCode.InternalServerError) {
              mensaje = 'Error interno del servidor.';
              descripcion =
                'El servicio de análisis tuvo un problema procesando tu solicitud. Intenta nuevamente.';
            } else if (e.status === HttpStatusCode.BadRequest) {
              mensaje = 'Solicitud inválida.';
              descripcion =
                'El comentario enviado no cumple con el formato esperado.';
            } else if (e.status === HttpStatusCode.ServiceUnavailable) {
              mensaje = 'Servicio no disponible.';
              descripcion =
                'El servicio de análisis está temporalmente fuera de línea.';
            }
            const mensajeCompleto = descripcion
              ? `${mensaje} ${descripcion}`
              : mensaje;
            this.mensajeError.set(mensajeCompleto);
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
            this._cargarDatosAlmacenados();
          },
          error: (e: HttpErrorResponse) => {
            let mensaje = 'Ocurrió un error inesperado.';
            let descripcion = '';
            if (e.error && e.error.error) {
              mensaje = e.error.error;
            } else if (e.status === 0) {
              mensaje = 'No se pudo conectar con el servidor.';
              descripcion =
                'Verifica que el servicio de análisis esté en ejecución o revisa tu conexión a internet.';
            } else if (e.status === HttpStatusCode.InternalServerError) {
              mensaje = 'Error interno del servidor.';
              descripcion =
                'Hubo un problema procesando tu archivo. Verifica que el formato CSV sea correcto.';
            } else if (e.status === HttpStatusCode.BadRequest) {
              mensaje = 'Archivo o columna inválidos.';
              descripcion =
                'Verifica que el archivo sea CSV y que la columna especificada exista.';
            } else if (e.status === HttpStatusCode.ServiceUnavailable) {
              mensaje = 'Servicio no disponible.';
              descripcion =
                'El servicio de análisis está temporalmente fuera de línea.';
            }
            const mensajeCompleto = descripcion
              ? `${mensaje} ${descripcion}`
              : mensaje;
            this.mensajeError.set(mensajeCompleto);
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
  nuevoAnalisis() {
    this._reiniciar();
  }
  private _cargarDatosAlmacenados() {
    this._sentimentApiService.obtenerDatosAlmacenados().subscribe({
      next: (response) => {
        this.datosAlmacenados.set(response.reverse());
      },
    });
  }
  private _cargarSessionStorage() {
    const formaGuardada = sessionStorage.getItem('forma-analisis');
    const idiomaGuardado = sessionStorage.getItem('idioma-analisis');
    const tabGuardado = sessionStorage.getItem('tab-seleccionado');
    if (formaGuardada) {
      this.formaAnalisis = formaGuardada;
    }
    if (idiomaGuardado) {
      this.idiomaAnalisis = idiomaGuardado;
    }
    if (tabGuardado) {
      this.indexSeleccionado = Number(tabGuardado);
    }
  }
  private _reiniciar() {
    this.resultadosAnalisis.set([]);
    this.textarea.reset();
    this.input.reset();
    this.eliminarArchivoCargado();
  }

  // Validador personalizado para rechazar textos que solo contengan espacios
  noSoloEspaciosValidator(control: AbstractControl): ValidationErrors | null {
    if (control.value && control.value.trim().length === 0) {
      return { soloEspacios: true };
    }
    return null;
  }
}
