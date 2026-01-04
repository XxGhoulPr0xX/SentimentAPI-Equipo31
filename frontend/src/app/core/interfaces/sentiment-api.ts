export interface SentimentRequest {
  text: string;
}

export interface SentimentResponse {
  id: number;
  text: string;
  prevision: 'Positivo' | 'Negativo' | 'Neutro';
  probabilidad: number;
  createdAt: string;
}
