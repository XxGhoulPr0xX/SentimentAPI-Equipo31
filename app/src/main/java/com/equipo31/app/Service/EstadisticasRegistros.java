package com.equipo31.app.Service;

import com.equipo31.app.entity.SentimentRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EstadisticasRegistros {

    private static final Logger log = LoggerFactory.getLogger(EstadisticasRegistros.class);

    // Servicio que se encarga de imprimir en consola primero los datos que se han
    // insertado en la base de datos H2
    public void imprimirRegistrosEnConsola(List<SentimentRecord> registros) {
        log.info("");
        log.info("======= REPORTE DE BASE DE DATOS H2 =======");

        if (registros == null || registros.isEmpty()) {
            log.info("La lista proporcionada está vacía o es nula.");
        } else {
            log.info("Total de registros a mostrar: {}", registros.size());
            log.info("-----------------------------------------------------------------------------------");
            log.info(String.format("%-5s | %-15s | %-12s | %-8s | %-20s", "ID", "FECHA", "SENTIMIENTO", "PROB",
                    "TEXTO"));
            log.info("-----------------------------------------------------------------------------------");

            for (SentimentRecord r : registros) {
                log.info(String.format("%-5d | %-15s | %-12s | %-8.4f | %-20s",
                        r.getId(),
                        r.getCreatedAt().toLocalTime(),
                        r.getPrevision(),
                        r.getProbabilidad(),
                        r.getText().length() > 20 ? r.getText().substring(0, 17) + "..." : r.getText()));
            }
        }
        log.info("===========================================");
    }
}