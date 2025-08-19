package com.example.Literalura.repository;

import com.example.Literalura.modelos.Autor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AutorRepository extends JpaRepository<Autor, Long> {
    Optional<Autor> findByNombreAndFechaNacimientoAndFechaFallecimiento(
            String nombre, Integer fechaNacimiento, Integer fechaFallecimiento
    );
}