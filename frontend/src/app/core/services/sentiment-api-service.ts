import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';

import {
  SentimentRequest,
  SentimentResponse,
} from '../interfaces/sentiment-api';

@Injectable({
  providedIn: 'root',
})
export class SentimentApiService {
  private _httpCliente = inject(HttpClient);
  private _url = 'http://localhost:5000';

  analizar(body: SentimentRequest) {
    return this._httpCliente.post<SentimentResponse>(
      `${this._url}/sentiment`,
      body,
    );
  }
}
