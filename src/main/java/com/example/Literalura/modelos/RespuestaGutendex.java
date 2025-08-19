package com.example.Literalura.modelos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RespuestaGutendex(
        int count,
        String next,
        String previous,
        List<datosLibro> results
) {}