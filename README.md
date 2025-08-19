# Literalura — Búsqueda de libros (Gutendex) + Spring Boot + PostgreSQL

Aplicación de línea de comandos (CLI) en **Java / Spring Boot** que busca libros en la API pública **[Gutendex](https://gutendex.com/)**, registra los resultados en **PostgreSQL** y permite consultar libros y autores desde la base de datos.

---

## Índice
1. [Descripción del proyecto](#0-descripción-del-proyecto)  
2. [Requisitos](#1-requisitos)
3. [Configuración](#2-configuración)
4. [Ejecutar y uso](#3-ejecutar-y-uso)

---
## 1) Descripción del proyecto

Aplicación **CLI (consola)** en **Java / Spring Boot** que consulta la API pública **[Gutendex](https://gutendex.com/)** para buscar libros por título.  
Guarda en **PostgreSQL** el libro encontrado (usando el **id de Gutendex**), sus **autores** (sin duplicados) y permite consultar desde la base:

- Listar libros registrados  
- Listar autores registrados  
- Filtrar autores vivos en un año dado

> Nota: La opción “libros por idioma” usa el registro **en memoria**; si se desea desde BD, basta persistir los idiomas (p. ej. tabla `book_languages` o `@ElementCollection`).


## 2) Requisitos

- Java **17+** (probado con JDK 21)
- Maven **3.9+**
- PostgreSQL **14+**
- Conexión a internet (para consultar Gutendex)

---

## 3) Configuración

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
## 4) Ejecutar y uso
```bash
mvn clean package
mvn spring-boot:run
```
1: ingresa una parte del título (ej. quijote).
Se consulta Gutendex, se toma el primer resultado y se guarda en BD (libro + autores, sin duplicados).

2: lista los libros guardados en libros (con sus autores).

3: lista los autores guardados en autores.

4: pide un año y filtra autores vivos en ese año.

5: lista por idioma de los resultados en memoria (si quieres que sea desde BD, añade persistencia de idiomas).
