package com.equipo31.app.service;

import com.opencsv.CSVReader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;

import java.util.ArrayList;
import java.util.List;

@Service
public class AnalizarArchivoService {

    public List<String> extraerComentariosDeCSV(MultipartFile file, String columnName) throws Exception {
        List<String> comentarios = new ArrayList<>();

        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            try (CSVReader csvReader = new CSVReader(reader)) {
                String[] cabecera = csvReader.readNext();

                if (cabecera == null)
                    throw new RuntimeException("El archivo está vacío.");

                int columnIndex = -1;
                for (int i = 0; i < cabecera.length; i++) {
                    if (cabecera[i].equalsIgnoreCase(columnName)) {
                        columnIndex = i;
                        break;
                    }
                }

                if (columnIndex == -1) {
                    throw new RuntimeException("La columna " + columnName + " no se encontró.");
                }

                String[] fila;
                while ((fila = csvReader.readNext()) != null) {
                    if (columnIndex < fila.length) {
                        comentarios.add(fila[columnIndex]);
                    }
                }
            }
        }
        return comentarios;
    }
}