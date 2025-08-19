// com/example/Literalura/modelos/Libro.java
package com.example.Literalura.modelos;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
@Entity
@Table(name = "libros")
public class Libro {
    @Id
    private Integer id;

    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Transient
    private List<Autor> autores;

    @Transient
    private List<String> idiomas;

    @Column(name = "numero_descargas")
    private Integer numeroDeDescargas;

    public Libro() {}

    public Libro(Integer id, String titulo, List<Autor> autores, List<String> idiomas, Integer numeroDeDescargas) {
        this.id = id;
        this.titulo = titulo;
        this.autores = autores;
        this.idiomas = idiomas;
        this.numeroDeDescargas = numeroDeDescargas;
    }

    public static Libro from(datosLibro d) {
        List<Autor> autores = new ArrayList<>();
        if (d.autores() != null) {
            for (AutorApi a : d.autores()) {
                autores.add(new Autor(a.nombre(), a.fechaNacimiento(), a.fechaFallecimiento()));
            }
        }
        List<String> idiomas = (d.idiomas() == null) ? List.of() : d.idiomas();
        return new Libro(d.id(), d.titulo(), autores, idiomas, d.numeroDescargas());
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNumeroDeDescargas() {
        return numeroDeDescargas;
    }

    public void setNumeroDeDescargas(Integer numeroDeDescargas) {
        this.numeroDeDescargas = numeroDeDescargas;
    }

    public List<String> getIdiomas() {
        return idiomas;
    }

    public void setIdiomas(List<String> idiomas) {
        this.idiomas = idiomas;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public List<Autor> getAutores() {
        return autores;
    }

    public void setAutores(List<Autor> autores) {
        this.autores = autores;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Libro)) return false;
        Libro l = (Libro) o;
        if (id != null && l.id != null) return Objects.equals(id, l.id);
        return Objects.equals(titulo, l.titulo);
    }
    @Override public int hashCode() {
        return (id != null) ? id.hashCode() : Objects.hashCode(titulo);
    }
}
