package com.equipo31.app.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SentimentRecordTest {

  /**
   * Prueba unitaria: Verifica que el constructor sin argumentos de
   * SentimentRecord inicializa los campos correctamente (null para id, text y
   * prevision, 0.0 para probabilidad).
   */
  @Test
  void testDefaultConstructor() {
    SentimentRecord record = new SentimentRecord();

    assertNull(record.getId());
    assertNull(record.getText());
    assertNull(record.getPrevision());
    assertEquals(0.0, record.getProbabilidad());
    assertNotNull(record.getCreatedAt());
  }

  /**
   * Prueba unitaria: Verifica que el constructor parametrizado de
   * SentimentRecord asigna correctamente el texto, predicción y probabilidad.
   */
  @Test
  void testParameterizedConstructor() {
    String expectedText = "Producto muy bueno";
    String expectedPrevision = "Positivo";
    double expectedProbabilidad = 0.92;

    SentimentRecord record = new SentimentRecord(expectedText, expectedPrevision, expectedProbabilidad);

    assertNull(record.getId());
    assertEquals(expectedText, record.getText());
    assertEquals(expectedPrevision, record.getPrevision());
    assertEquals(expectedProbabilidad, record.getProbabilidad());
    assertNotNull(record.getCreatedAt());
  }

  /**
   * Prueba unitaria: Verifica que la fecha de creación (createdAt) se
   * establezca automáticamente al crear un SentimentRecord.
   */
  @Test
  void testCreatedAtIsSetAutomatically() {
    SentimentRecord record = new SentimentRecord("Texto", "Positivo", 0.9);
    LocalDateTime beforeCreation = LocalDateTime.now().minusSeconds(1);
    LocalDateTime afterCreation = LocalDateTime.now().plusSeconds(1);

    assertTrue(record.getCreatedAt().isAfter(beforeCreation));
    assertTrue(record.getCreatedAt().isBefore(afterCreation));
  }

  /**
   * Prueba unitaria: Verifica que todos los getters de SentimentRecord
   * retornen los valores correctamente asignados.
   */
  @Test
  void testGettersReturnCorrectValues() {
    String text = "Análisis de sentimiento";
    String prevision = "Negativo";
    double probabilidad = 0.75;
    LocalDateTime now = LocalDateTime.now();

    SentimentRecord record = new SentimentRecord(text, prevision, probabilidad);
    record.setCreatedAt(now);

    assertEquals(text, record.getText());
    assertEquals(prevision, record.getPrevision());
    assertEquals(probabilidad, record.getProbabilidad());
    assertEquals(now, record.getCreatedAt());
  }

  /**
   * Prueba unitaria: Verifica que SentimentRecord maneje correctamente
   * diferentes valores de probabilidad en el rango de 0.0 a 1.0.
   */
  @Test
  void testSentimentRecordWithDifferentProbabilities() {
    double[] probabilities = { 0.0, 0.25, 0.5, 0.75, 1.0 };

    for (double prob : probabilities) {
      SentimentRecord record = new SentimentRecord("Test", "Positivo", prob);
      assertEquals(prob, record.getProbabilidad());
    }
  }

  /**
   * Prueba unitaria: Verifica que se puedan crear múltiples registros de
   * sentimiento independientes con datos diferentes.
   */
  @Test
  void testMultipleSentimentRecords() {
    SentimentRecord record1 = new SentimentRecord("Texto 1", "Positivo", 0.8);
    SentimentRecord record2 = new SentimentRecord("Texto 2", "Negativo", 0.7);

    assertNotEquals(record1.getText(), record2.getText());
    assertNotEquals(record1.getPrevision(), record2.getPrevision());
    assertNotEquals(record1.getProbabilidad(), record2.getProbabilidad());
  }

  /**
   * Prueba unitaria: Verifica que SentimentRecord pueda almacenar correctamente
   * textos muy largos (repeticiones de más de 100 caracteres).
   */
  @Test
  void testSentimentRecordLongText() {
    String longText = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. ".repeat(10);
    SentimentRecord record = new SentimentRecord(longText, "Neutral", 0.5);

    assertEquals(longText, record.getText());
    assertTrue(record.getText().length() > 100);
  }

  /**
   * Prueba unitaria: Verifica que SentimentRecord maneje correctamente
   * casos extremos de probabilidad (valores muy cercanos a 0 y a 1).
   */
  @Test
  void testSentimentRecordProbabilityEdgeCases() {
    SentimentRecord recordMin = new SentimentRecord("Test", "Negativo", 0.01);
    SentimentRecord recordMax = new SentimentRecord("Test", "Positivo", 0.99);

    assertEquals(0.01, recordMin.getProbabilidad());
    assertEquals(0.99, recordMax.getProbabilidad());
  }
}
