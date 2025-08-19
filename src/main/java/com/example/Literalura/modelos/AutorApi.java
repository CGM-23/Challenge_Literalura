package com.example.Literalura.modelos;

import com.fasterxml.jackson.annotation.JsonAlias;


public record AutorApi(
        @JsonAlias("name") String nombre,
        @JsonAlias("birth_year") Integer fechaNacimiento,
        @JsonAlias("death_year") Integer fechaFallecimiento
) {}



