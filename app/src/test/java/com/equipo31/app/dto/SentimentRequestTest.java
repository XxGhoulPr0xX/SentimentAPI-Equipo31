package com.equipo31.app.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SentimentRequestTest {

  private static Validator validator;

  @BeforeAll
  static void setup() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  /**
   * Prueba unitaria: Verifica que un SentimentRequest con texto válido
   * no tenga violaciones de validación.
   */
  @Test
  void testValidSentimentRequest() {
    SentimentRequest request = new SentimentRequest();
    request.setText("Este producto es excelente");

    Set<ConstraintViolation<SentimentRequest>> violations = validator.validate(request);
    assertTrue(violations.isEmpty(), "No debería haber violaciones para un texto válido");
  }

  /**
   * Prueba unitaria: Verifica que un SentimentRequest con texto vacío
   * genere una violación de validación.
   */
  @Test
  void testEmptyTextValidation() {
    SentimentRequest request = new SentimentRequest();
    request.setText("");

    Set<ConstraintViolation<SentimentRequest>> violations = validator.validate(request);
    assertFalse(violations.isEmpty(), "Debería haber violación para texto vacío");
    assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("obligatorio")));
  }

  /**
   * Prueba unitaria: Verifica que un SentimentRequest con texto nulo
   * genere una violación de validación.
   */
  @Test
  void testNullTextValidation() {
    SentimentRequest request = new SentimentRequest();
    request.setText(null);

    Set<ConstraintViolation<SentimentRequest>> violations = validator.validate(request);
    assertFalse(violations.isEmpty(), "Debería haber violación para texto nulo");
  }

  /**
   * Prueba unitaria: Verifica que un SentimentRequest con texto menor a 3
   * caracteres genere una violación de validación de longitud mínima.
   */
  @Test
  void testMinLengthValidation() {
    SentimentRequest request = new SentimentRequest();
    request.setText("ab");

    Set<ConstraintViolation<SentimentRequest>> violations = validator.validate(request);
    assertFalse(violations.isEmpty(), "Debería haber violación para texto menor a 3 caracteres");
    assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("al menos 3")));
  }

  /**
   * Prueba unitaria: Verifica que un SentimentRequest con exactamente 3
   * caracteres (longitud mínima) sea válido sin violaciones.
   */
  @Test
  void testValidMinLengthText() {
    SentimentRequest request = new SentimentRequest();
    request.setText("abc");

    Set<ConstraintViolation<SentimentRequest>> violations = validator.validate(request);
    assertTrue(violations.isEmpty(), "No debería haber violaciones para texto de 3 caracteres");
  }

  /**
   * Prueba unitaria: Verifica que los métodos getter y setter de texto
   * funcionen correctamente en SentimentRequest.
   */
  @Test
  void testGetterSetterText() {
    SentimentRequest request = new SentimentRequest();
    String textoEsperado = "Me encanta esta aplicación";
    request.setText(textoEsperado);

    assertEquals(textoEsperado, request.getText(), "El getter debería retornar el texto asignado");
  }

  /**
   * Prueba unitaria: Verifica que un SentimentRequest con solo espacios en
   * blanco genere una violación de validación.
   */
  @Test
  void testWhitespaceOnlyText() {
    SentimentRequest request = new SentimentRequest();
    request.setText("   ");

    Set<ConstraintViolation<SentimentRequest>> violations = validator.validate(request);
    assertFalse(violations.isEmpty(), "Debería haber violación para texto con solo espacios");
  }

  /**
   * Prueba unitaria: Verifica que un SentimentRequest con un texto muy largo
   * (1000 caracteres) sea válido sin violaciones de validación.
   */
  @Test
  void testLongValidText() {
    SentimentRequest request = new SentimentRequest();
    String longText = "a".repeat(1000);
    request.setText(longText);

    Set<ConstraintViolation<SentimentRequest>> violations = validator.validate(request);
    assertTrue(violations.isEmpty(), "No debería haber violaciones para texto largo válido");
  }
}
