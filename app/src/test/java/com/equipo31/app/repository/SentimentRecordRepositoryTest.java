package com.equipo31.app.repository;

import com.equipo31.app.entity.SentimentRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class SentimentRecordRepositoryTest {

  @Autowired
  private SentimentRecordRepository repository;

  /**
   * Prueba unitaria: Verifica que se puede guardar un
   * SentimentRecord en la base de datos y recuperarlo correctamente por su ID.
   */
  @Test
  void testSaveAndFindSentimentRecord() {
    SentimentRecord record = new SentimentRecord("Excelente producto", "Positivo", 0.95);

    SentimentRecord saved = repository.save(record);

    assertNotNull(saved.getId());
    assertEquals("Excelente producto", saved.getText());
    assertEquals("Positivo", saved.getPrevision());
  }

  /**
   * Prueba unitaria: Verifica que el método countByPrevision()
   * retorna el número correcto de registros con predicción "Positivo".
   */
  @Test
  void testCountByPrevisionPositivo() {
    repository.deleteAll();

    repository.save(new SentimentRecord("Texto 1", "Positivo", 0.8));
    repository.save(new SentimentRecord("Texto 2", "Positivo", 0.9));
    repository.save(new SentimentRecord("Texto 3", "Negativo", 0.7));

    long countPositivo = repository.countByPrevision("Positivo");

    assertEquals(2, countPositivo);
  }

  /**
   * Prueba unitaria: Verifica que el método countByPrevision()
   * retorna el número correcto de registros con predicción "Negativo".
   */
  @Test
  void testCountByPrevisionNegativo() {
    repository.deleteAll();

    repository.save(new SentimentRecord("Texto 1", "Positivo", 0.8));
    repository.save(new SentimentRecord("Texto 2", "Negativo", 0.7));
    repository.save(new SentimentRecord("Texto 3", "Negativo", 0.75));

    long countNegativo = repository.countByPrevision("Negativo");

    assertEquals(2, countNegativo);
  }

  /**
   * Prueba unitaria: Verifica que countByPrevision() retorna 0
   * cuando no hay registros con la predicción solicitada.
   */
  @Test
  void testCountByPrevisionEmpty() {
    repository.deleteAll();

    repository.save(new SentimentRecord("Texto 1", "Positivo", 0.8));
    repository.save(new SentimentRecord("Texto 2", "Positivo", 0.9));

    long countNegativo = repository.countByPrevision("Negativo");

    assertEquals(0, countNegativo);
  }

  /**
   * Prueba unitaria: Verifica que count() retorna el número
   * correcto de registros totales almacenados en la base de datos.
   */
  @Test
  void testFindAllRecords() {
    repository.deleteAll();

    repository.save(new SentimentRecord("Texto 1", "Positivo", 0.8));
    repository.save(new SentimentRecord("Texto 2", "Negativo", 0.7));
    repository.save(new SentimentRecord("Texto 3", "Positivo", 0.9));

    long count = repository.count();

    assertEquals(3, count);
  }

  /**
   * Prueba unitaria: Verifica que se puede eliminar un
   * SentimentRecord por su ID y que después no se encuentra en la base de datos.
   */
  @Test
  void testDeleteSentimentRecord() {
    repository.deleteAll();

    SentimentRecord record = repository.save(new SentimentRecord("Texto a eliminar", "Positivo", 0.8));
    Long recordId = record.getId();

    repository.deleteById(recordId);

    assertTrue(repository.findById(recordId).isEmpty());
  }

  /**
   * Prueba unitaria: Verifica que se puede recuperar un
   * SentimentRecord guardado en la base de datos y que sus datos coincidan con
   * los originales.
   */
  @Test
  void testUpdateSentimentRecord() {
    repository.deleteAll();

    SentimentRecord record = repository.save(new SentimentRecord("Texto original", "Positivo", 0.8));
    Long recordId = record.getId();

    SentimentRecord found = repository.findById(recordId).orElse(null);
    assertNotNull(found);
    assertEquals("Texto original", found.getText());
  }

  /**
   * Prueba unitaria: Verifica que se puedan guardar múltiples
   * registros con probabilidades progresivas y que el conteo total y por
   * predicción funcionen correctamente.
   */
  @Test
  void testMultipleSentimentsCount() {
    repository.deleteAll();

    for (int i = 0; i < 5; i++) {
      repository.save(new SentimentRecord("Texto " + i, "Positivo", 0.8 + (i * 0.02)));
    }

    long totalCount = repository.count();
    long positiveCount = repository.countByPrevision("Positivo");

    assertEquals(5, totalCount);
    assertEquals(5, positiveCount);
  }
}
