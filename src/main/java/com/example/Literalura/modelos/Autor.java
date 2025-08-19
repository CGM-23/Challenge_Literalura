package com.example.Literalura.modelos;

import jakarta.persistence.*;

import java.util.Objects;
@Entity
@Table(
        name = "autores",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_autor",
                columnNames = {"nombre","fecha_nacimiento","fecha_fallecimiento"}
        )
)
public class Autor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long pk;

    private String nombre;
    @Column(name = "fecha_nacimiento")
    private Integer fechaNacimiento;
    @Column(name = "fecha_fallecimiento")
    private Integer fechaFallecimiento;
    public Autor() {}

    public Autor(String nombre, Integer fechaNacimiento, Integer fechaFallecimiento) {
        this.nombre = nombre;
        this.fechaNacimiento = fechaNacimiento;
        this.fechaFallecimiento = fechaFallecimiento;
    }

    public static AutorApi from(AutorApi a) {
        if (a == null) return null;
        return new AutorApi(a.nombre(), a.fechaNacimiento(), a.fechaFallecimiento());
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getFechaFallecimiento() {
        return fechaFallecimiento;
    }

    public void setFechaFallecimiento(Integer fechaFallecimiento) {
        this.fechaFallecimiento = fechaFallecimiento;
    }

    public Integer getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Integer fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    // equals/hashCode para poder hacer distinct() por autor
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Autor)) return false;
        Autor autor = (Autor) o;
        return Objects.equals(nombre, autor.nombre) &&
                Objects.equals(fechaNacimiento, autor.fechaNacimiento) &&
                Objects.equals(fechaFallecimiento, autor.fechaFallecimiento);
    }
    @Override
    public int hashCode() {
        return Objects.hash(nombre, fechaNacimiento, fechaFallecimiento);
    }
}