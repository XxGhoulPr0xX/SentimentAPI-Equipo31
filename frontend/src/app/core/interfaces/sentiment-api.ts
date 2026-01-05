export type Sentimiento = 'Positivo' | 'Negativo' | 'Neutro' | '';

export interface SentimentRequest {
  text: string;
}

export interface SentimentResponse {
  id: number;
  text: string;
  prevision: Sentimiento;
  probabilidad: number;
  createdAt: string;
}
