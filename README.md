# Literalura — Búsqueda de libros (Gutendex) + Spring Boot + PostgreSQL

Aplicación de línea de comandos (CLI) en **Java / Spring Boot** que busca libros en la API pública **[Gutendex](https://gutendex.com/)**, registra los resultados en **PostgreSQL** y permite consultar libros y autores desde la base de datos.

---

## Índice

1. [Requisitos](#1-requisitos)
2. [Configuración](#2-configuración)
3. [Ejecutar y uso](#3-ejecutar-y-uso)
4. [Estructura y notas técnicas](#4-estructura-y-notas-técnicas)

---

## 1) Requisitos

- Java **17+** (probado con JDK 21)
- Maven **3.9+**
- PostgreSQL **14+**
- Conexión a internet (para consultar Gutendex)

---

## 2) Configuración

### 2.1. Base de datos

Crea la BD y las tablas mínimas:

```sql
CREATE DATABASE literalura;

\c literalura

CREATE TABLE IF NOT EXISTS autores (
  id BIGSERIAL PRIMARY KEY,
  nombre TEXT NOT NULL,
  fecha_nacimiento INTEGER,
  fecha_fallecimiento INTEGER,
  CONSTRAINT uq_autor UNIQUE (nombre, fecha_nacimiento, fecha_fallecimiento)
);

CREATE TABLE IF NOT EXISTS libros (
  id INTEGER PRIMARY KEY,         
  titulo TEXT NOT NULL,
  numero_descargas INTEGER
);

CREATE TABLE IF NOT EXISTS libros_autores (
  libro_id INTEGER NOT NULL REFERENCES libros(id)   ON DELETE CASCADE,
  autor_id BIGINT  NOT NULL REFERENCES autores(id)  ON DELETE CASCADE,
  PRIMARY KEY (libro_id, autor_id)
);
```
## 3) Ejecutar y uso
