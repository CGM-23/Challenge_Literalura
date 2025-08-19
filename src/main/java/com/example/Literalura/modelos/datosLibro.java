package com.example.Literalura.modelos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record datosLibro(
        @JsonProperty("id") Integer id,
        @JsonProperty("title") String titulo,
        @JsonProperty("authors") java.util.List<AutorApi> autores,
        @JsonProperty("languages") java.util.List<String> idiomas,
        @JsonProperty("download_count") Integer numeroDescargas
) {}