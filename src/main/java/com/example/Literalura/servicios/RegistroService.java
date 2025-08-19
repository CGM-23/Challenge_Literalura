package com.example.Literalura.servicios;

import com.example.Literalura.modelos.*;
import com.example.Literalura.repository.AutorRepository;
import com.example.Literalura.repository.LibroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistroService {
    private final LibroRepository libroRepo;
    private final AutorRepository autorRepo;

    public RegistroService(LibroRepository libroRepo, AutorRepository autorRepo) {
        this.libroRepo = libroRepo;
        this.autorRepo = autorRepo;
    }

    @Transactional
    public Libro guardarLibroConAutores(Libro libro) {
        // Asegurar autores existentes/únicos
        if (libro.getAutores() != null) {
            var lista = new java.util.ArrayList<Autor>(libro.getAutores().size());
            for (Autor a : libro.getAutores()) {
                var existente = autorRepo
                        .findByNombreAndFechaNacimientoAndFechaFallecimiento(
                                a.getNombre(), a.getFechaNacimiento(), a.getFechaFallecimiento())
                        .orElseGet(() -> autorRepo.save(a));
                lista.add(existente);
            }
            libro.setAutores(lista);
        }
        // Guardar libro (id = Gutendex, hace upsert)
        return libroRepo.save(libro);
    }
}