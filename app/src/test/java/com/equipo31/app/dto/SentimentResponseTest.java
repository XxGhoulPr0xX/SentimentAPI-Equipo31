package com.equipo31.app.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SentimentResponseTest {

  /**
   * Prueba unitaria: Verifica que el constructor parametrizado de
   * SentimentResponse y todos los getters funcionen correctamente.
   */
  @Test
  void testSentimentResponseConstructorAndGetters() {
    Long expectedId = 1L;
    String expectedText = "Excelente servicio";
    String expectedPrevision = "Positivo";
    double expectedProbabilidad = 0.95;
    LocalDateTime expectedDateTime = LocalDateTime.now();

    SentimentResponse response = new SentimentResponse(
        expectedId,
        expectedText,
        expectedPrevision,
        expectedProbabilidad,
        expectedDateTime);

    assertEquals(expectedId, response.getId(), "El ID debería coincidir");
    assertEquals(expectedText, response.getText(), "El texto debería coincidir");
    assertEquals(expectedPrevision, response.getPrevision(), "La predicción debería coincidir");
    assertEquals(expectedProbabilidad, response.getProbabilidad(), "La probabilidad debería coincidir");
    assertEquals(expectedDateTime, response.getCreatedAt(), "La fecha debería coincidir");
  }

  /**
   * Prueba unitaria: Verifica que SentimentResponse pueda almacenar
   * correctamente sentimientos negativos con su predicción y probabilidad
   * correspondientes.
   */
  @Test
  void testSentimentResponseWithNegativeSentiment() {
    SentimentResponse response = new SentimentResponse(
        2L,
        "Producto defectuoso",
        "Negativo",
        0.85,
        LocalDateTime.now());

    assertEquals("Negativo", response.getPrevision());
    assertEquals(0.85, response.getProbabilidad());
  }

  /**
   * Prueba unitaria: Verifica que SentimentResponse maneje correctamente
   * diferentes valores de probabilidad (rango 0.0 a 1.0).
   */
  @Test
  void testSentimentResponseWithDifferentProbabilities() {
    double[] probabilities = { 0.5, 0.75, 0.99, 0.01 };

    for (double prob : probabilities) {
      SentimentResponse response = new SentimentResponse(1L, "Test", "Positivo", prob, LocalDateTime.now());
      assertEquals(prob, response.getProbabilidad());
    }
  }

  /**
   * Prueba unitaria: Verifica que la fecha y hora de creación en
   * SentimentResponse se mantenga consistente con la asignada.
   */
  @Test
  void testSentimentResponseDateTimeConsistency() {
    LocalDateTime now = LocalDateTime.now();
    SentimentResponse response = new SentimentResponse(1L, "Test", "Positivo", 0.8, now);

    assertNotNull(response.getCreatedAt());
    assertEquals(now, response.getCreatedAt());
  }

  /**
   * Prueba unitaria: Verifica que SentimentResponse maneje correctamente
   * múltiples IDs diferentes en un rango de 1 a 5.
   */
  @Test
  void testSentimentResponseWithDifferentIds() {
    for (long id = 1; id <= 5; id++) {
      SentimentResponse response = new SentimentResponse(
          id,
          "Texto" + id,
          id % 2 == 0 ? "Positivo" : "Negativo",
          0.5 + (id * 0.1),
          LocalDateTime.now());
      assertEquals(id, response.getId());
    }
  }
}
