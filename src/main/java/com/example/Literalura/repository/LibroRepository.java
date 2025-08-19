package com.example.Literalura.repository;

import com.example.Literalura.modelos.Libro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LibroRepository extends JpaRepository<Libro, Integer> {

}