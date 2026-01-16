package com.equipo31.app;

import com.equipo31.app.dto.SentimentRequest;
import com.equipo31.app.dto.SentimentResponse;
import com.equipo31.app.entity.SentimentRecord;
import com.equipo31.app.repository.SentimentRecordRepository;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SentimentControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private SentimentRecordRepository repository;

  @Autowired
  private ObjectMapper objectMapper;

  @BeforeEach
  void setup() {
    repository.deleteAll();
  }

  /**
   * Prueba de integración: Verifica que el endpoint de análisis de sentimiento
   * reciba un comentario válido y retorne una respuesta exitosa con todos los
   * campos esperados (id, texto, predicción, probabilidad y fecha de creación).
   */
  @Test
  void testAnalyzeSentimentCommentEndpointSuccess() throws Exception {
    SentimentRequest request = new SentimentRequest();
    request.setText("Este producto es excelente y funciona perfectamente");

    mockMvc.perform(post("/sentiment-api/analizar-comentario")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.text").value("Este producto es excelente y funciona perfectamente"))
        .andExpect(jsonPath("$.prevision").exists())
        .andExpect(jsonPath("$.probabilidad").exists())
        .andExpect(jsonPath("$.createdAt").exists());
  }

  /**
   * Prueba de integración: Verifica que el endpoint rechace un comentario
   * que no cumple con la validación de longitud mínima (menor a 3 caracteres),
   * retornando un estado HTTP 400 (Bad Request).
   */
  @Test
  void testAnalyzeSentimentCommentInvalidRequest() throws Exception {
    SentimentRequest request = new SentimentRequest();
    request.setText("ab");

    mockMvc.perform(post("/sentiment-api/analizar-comentario")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  /**
   * Prueba de integración: Verifica que el endpoint rechace un comentario
   * vacío, retornando un estado HTTP 400 (Bad Request).
   */
  @Test
  void testAnalyzeSentimentCommentEmptyText() throws Exception {
    SentimentRequest request = new SentimentRequest();
    request.setText("");

    mockMvc.perform(post("/sentiment-api/analizar-comentario")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  /**
   * Prueba de integración: Verifica que el endpoint rechace un comentario
   * nulo (null), retornando un estado HTTP 400 (Bad Request).
   */
  @Test
  void testAnalyzeSentimentCommentNullText() throws Exception {
    mockMvc.perform(post("/sentiment-api/analizar-comentario")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"text\": null}"))
        .andExpect(status().isBadRequest());
  }

  /**
   * Prueba de integración: Verifica que el endpoint rechace un comentario
   * que contiene solo espacios en blanco, retornando un estado HTTP 400 (Bad
   * Request).
   */
  @Test
  void testAnalyzeSentimentCommentWhitespaceOnly() throws Exception {
    SentimentRequest request = new SentimentRequest();
    request.setText("   ");

    mockMvc.perform(post("/sentiment-api/analizar-comentario")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  /**
   * Prueba de integración: Verifica que después de analizar un comentario
   * exitosamente, el registro se guarde correctamente en la base de datos.
   */
  @Test
  void testAnalyzeSentimentCommentSavesInDatabase() throws Exception {
    SentimentRequest request = new SentimentRequest();
    request.setText("Producto malo y defectuoso");

    long initialCount = repository.count();

    @SuppressWarnings("unused")
    MvcResult result = mockMvc.perform(post("/sentiment-api/analizar-comentario")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andReturn();

    long finalCount = repository.count();

    assertEquals(initialCount + 1, finalCount, "Un nuevo registro debe guardarse en la base de datos");
  }

  /**
   * Prueba de integración: Verifica que la respuesta del endpoint contenga
   * todos los campos requeridos con valores válidos (id, texto, predicción,
   * probabilidad dentro del rango 0-1, y fecha de creación).
   */
  @Test
  void testAnalyzeSentimentCommentResponseStructure() throws Exception {
    SentimentRequest request = new SentimentRequest();
    request.setText("Servicio excelente");

    MvcResult result = mockMvc.perform(post("/sentiment-api/analizar-comentario")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andReturn();

    String responseBody = result.getResponse().getContentAsString();
    SentimentResponse response = objectMapper.readValue(responseBody, SentimentResponse.class);

    assertNotNull(response.getId());
    assertEquals("Servicio excelente", response.getText());
    assertNotNull(response.getPrevision());
    assertTrue(response.getProbabilidad() >= 0 && response.getProbabilidad() <= 1);
    assertNotNull(response.getCreatedAt());
  }

  /**
   * Prueba de integración: Verifica que el endpoint pueda procesar
   * correctamente textos largos (múltiples oraciones) y retorne el análisis de
   * sentimiento exitosamente.
   */
  @Test
  void testAnalyzeSentimentCommentLongText() throws Exception {
    SentimentRequest request = new SentimentRequest();
    request.setText(
        "Este es un texto muy largo que contiene múltiples oraciones para probar que el sistema puede analizar comentarios más extensos. El análisis de sentimiento debe ser capaz de procesar textos de cualquier longitud razonable. Esto es importante para validar que la API funciona correctamente.");

    mockMvc.perform(post("/sentiment-api/analizar-comentario")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.probabilidad").exists());
  }

  /**
   * Prueba de integración: Verifica que el endpoint pueda procesar múltiples
   * análisis de sentimiento en secuencia y guardar cada uno correctamente en la
   * BD.
   */
  @Test
  void testMultipleSentimentAnalysis() throws Exception {
    String[] textos = {
        "Producto muy bueno",
        "Muy malo y decepcionante",
        "Calidad excepcional",
        "No recomendado"
    };

    for (String texto : textos) {
      SentimentRequest request = new SentimentRequest();
      request.setText(texto);

      mockMvc.perform(post("/sentiment-api/analizar-comentario")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isOk());
    }

    assertEquals(textos.length, repository.count(), "Se deben guardar todos los registros de análisis");
  }

  /**
   * Prueba de integración: Verifica la integridad de datos del flujo completo:
   * el texto, predicción y probabilidad retornados en la respuesta coinciden
   * exactamente con los datos guardados en la base de datos.
   */
  @Test
  void testSentimentAnalysisDataIntegrity() throws Exception {
    SentimentRequest request = new SentimentRequest();
    String testText = "Integridad de datos - Prueba completa";
    request.setText(testText);

    MvcResult result = mockMvc.perform(post("/sentiment-api/analizar-comentario")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andReturn();

    String responseBody = result.getResponse().getContentAsString();
    SentimentResponse response = objectMapper.readValue(responseBody, SentimentResponse.class);

    SentimentRecord savedRecord = repository.findById(response.getId()).orElse(null);
    assertNotNull(savedRecord);
    assertEquals(testText, savedRecord.getText());
    assertEquals(response.getPrevision(), savedRecord.getPrevision());
    assertEquals(response.getProbabilidad(), savedRecord.getProbabilidad());
  }

  /**
   * Prueba de integración: Verifica que el endpoint maneje correctamente
   * caracteres especiales, acentos y símbolos (@, #, !, ¿, etc.) en el
   * análisis de sentimiento.
   */
  @Test
  void testAnalyzeSentimentWithSpecialCharacters() throws Exception {
    SentimentRequest request = new SentimentRequest();
    request.setText("¡Excelente! ¿Increíble? Sí, muy bueno. @user #test");

    mockMvc.perform(post("/sentiment-api/analizar-comentario")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.prevision").exists());
  }
}
