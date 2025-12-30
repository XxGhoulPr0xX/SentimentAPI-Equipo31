export interface Resultado {
  id: number;
  comentario: string;
  sentimiento: 'positivo' | 'negativo';
  probabilidad: number;
}
