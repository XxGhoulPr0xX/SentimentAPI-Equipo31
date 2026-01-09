import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';

import {
  SentimentRequest,
  SentimentResponse,
} from '../interfaces/sentiment-api';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class SentimentApiService {
  private _httpCliente = inject(HttpClient);
  private _url = environment.apiUrl;

  analizarComentario(body: SentimentRequest) {
    return this._httpCliente.post<SentimentResponse>(
      `${this._url}/sentiment-api/analizar-comentario`,
      body,
    );
  }

  analizarArchivo(formData: FormData) {
    return this._httpCliente.post<SentimentResponse[]>(
      `${this._url}/sentiment-api/analizar-archivo`,
      formData,
    );
  }

  configurarIdioma(param: string) {
    return this._httpCliente.post(
      `${this._url}/api/config/idioma`,
      {},
      {
        params: {
          lang: param,
        },
      },
    );
  }

  obtenerDatosAlmacenados() {
    return this._httpCliente.get<SentimentResponse[]>(
      `${this._url}/stats/list`,
    );
  }
}
