export interface Sentiment {
  comentario: string;
}

export interface ResponseSentiment {
  id: number;
  comentario: string;
  sentimiento: 'positivo' | 'negativo';
  probabilidad: number;
}
