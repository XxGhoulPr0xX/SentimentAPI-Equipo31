package com.equipo31.app.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Map;

@RestController
@RequestMapping("/model")
public class ModelUploadController {

  @PostMapping("/upload")
  public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) throws IOException {

    if (file == null || file.isEmpty()) {
      return ResponseEntity.badRequest()
          .body(Map.of("error", "Archivo vacío o no enviado."));
    }

    String name = file.getOriginalFilename() == null ? "model.onnx" : file.getOriginalFilename();
    if (!name.toLowerCase().endsWith(".onnx")) {
      return ResponseEntity.badRequest()
          .body(Map.of("error", "Solo se permiten archivos .onnx"));
    }

    Path dir = Paths.get("models");
    Files.createDirectories(dir);

    Path target = dir.resolve(name);
    Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

    return ResponseEntity.ok(Map.of(
        "message", "Modelo guardado",
        "file", name,
        "url", "/models/" + name));
  }
}
