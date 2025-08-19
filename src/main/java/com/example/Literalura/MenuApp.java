// com/example/Literalura/MenuApp.java
package com.example.Literalura;

import com.example.Literalura.modelos.*;
import com.example.Literalura.servicios.ConsumoApi;
import com.example.Literalura.servicios.convertirDatos;
import com.example.Literalura.repository.AutorRepository;
import com.example.Literalura.repository.LibroRepository;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.Collator;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class MenuApp {

    private final Scanner teclado = new Scanner(System.in);
    private final ConsumoApi consumo = new ConsumoApi();
    private final convertirDatos conversor = new convertirDatos();

    private final LibroRepository libroRepo;
    private final AutorRepository autorRepo;

    
    private final Set<Libro> librosRegistrados = new LinkedHashSet<>();
    private final Set<Autor> autoresRegistrados = new LinkedHashSet<>();

    public MenuApp(LibroRepository libroRepo, AutorRepository autorRepo) {
        this.libroRepo = libroRepo;
        this.autorRepo = autorRepo;
    }

    public void menu() {
        int opcion = -1;
        while (opcion != 0) {
            String menu = """
                    Elija la opción a través de su número:
                      1.- Buscar libro por título 
                      2.- Lista libros registrados 
                      3.- Lista autores registrados 
                      4.- Lista autores vivos en un determinado año 
                      5.- Listar libros por idioma 
                      0 - Salir
                    """;
            System.out.println(menu);
            System.out.print("> ");
            while (!teclado.hasNextInt()) { teclado.nextLine(); System.out.print("> "); }
            opcion = teclado.nextInt(); teclado.nextLine();

            switch (opcion) {
                case 1 -> buscarLibroWebYGuardar();   
                case 2 -> listarLibrosRegistradosBD();
                case 3 -> listarAutoresRegistradosBD();
                case 4 -> listarAutoresVivosEnAnioBD();
                case 5 -> listarLibrosPorIdioma();
                case 0 -> System.out.println("Adiós, ¡vuelva pronto!");
                default -> System.out.println("Opción inválida");
            }
        }
    }

    @Transactional 
    public void buscarLibroWebYGuardar() {
        System.out.print("Ingrese parte del título: ");
        String termino = teclado.nextLine().trim();
        if (termino.isBlank()) { System.out.println("Debe ingresar texto."); return; }

        String url = "https://gutendex.com/books/?search=" +
                URLEncoder.encode(termino, StandardCharsets.UTF_8);

        String json = consumo.obtenerDatos(url);
        RespuestaGutendex respuesta = conversor.obtenerDatos(json, RespuestaGutendex.class);

        if (respuesta == null || respuesta.results() == null || respuesta.results().isEmpty()) {
            System.out.println("No se encontraron libros.");
            return;
        }

        datosLibro dto = respuesta.results().get(0);
        Libro libro = Libro.from(dto);

     
        Libro entidad = libroRepo.findById(libro.getId()).orElseGet(() -> {
            Libro l = new Libro();
            l.setId(libro.getId());
            return l;
        });
        entidad.setTitulo(libro.getTitulo());
        entidad.setNumeroDeDescargas(libro.getNumeroDeDescargas()); 

        List<Autor> vinculados = new ArrayList<>();
        if (libro.getAutores() != null) {
            for (Autor a : libro.getAutores()) {
                Autor existente = autorRepo
                        .findByNombreAndFechaNacimientoAndFechaFallecimiento(
                                a.getNombre(), a.getFechaNacimiento(), a.getFechaFallecimiento())
                        .orElseGet(() -> autorRepo.save(new Autor(
                                a.getNombre(), a.getFechaNacimiento(), a.getFechaFallecimiento())));
                vinculados.add(existente);
            }
        }
        entidad.setAutores(vinculados);
        libroRepo.save(entidad);
       
        librosRegistrados.add(libro);
        autoresRegistrados.addAll(libro.getAutores());

        System.out.println("\n Libro guardado:");
        System.out.println("   ID: " + entidad.getId());
        System.out.println("   Título: " + entidad.getTitulo());
        System.out.println("   Autores: " + (vinculados.isEmpty()
                ? "(sin autores)"
                : vinculados.stream().map(Autor::getNombre).collect(Collectors.joining(", "))));
        System.out.println("   Descargas: " + (entidad.getNumeroDeDescargas() == null ? 0 : entidad.getNumeroDeDescargas()));
        System.out.println();
    }

    // (2) Libros desde BD
    private void listarLibrosRegistradosBD() {
        var libros = libroRepo.findAll();
        if (libros.isEmpty()) { System.out.println("No hay libros registrados en la BD."); return; }
        System.out.println("\n Libros en BD:");
        int i = 1;
        for (Libro l : libros) {
            String autores = (l.getAutores() == null || l.getAutores().isEmpty())
                    ? "(sin autores)"
                    : l.getAutores().stream().map(Autor::getNombre).collect(Collectors.joining(", "));
            System.out.printf(" %d) [%s] %s — Autores: %s — Descargas: %s%n",
                    i++, l.getId(), l.getTitulo(), autores, String.valueOf(l.getNumeroDeDescargas()));
        }
        System.out.println();
    }

    // (3) Autores desde BD
    private void listarAutoresRegistradosBD() {
        var autores = autorRepo.findAll();
        if (autores.isEmpty()) { System.out.println("No hay autores registrados en la BD."); return; }
        Collator collator = Collator.getInstance(new Locale("es"));
        autores.sort(Comparator.comparing(Autor::getNombre, collator));
        System.out.println("\n👤 Autores en BD:");
        int i = 1;
        for (Autor a : autores) {
            System.out.printf(" %d) %s (nac: %s, fallec: %s)%n",
                    i++, nullSafe(a.getNombre()), nullSafe(a.getFechaNacimiento()), nullSafe(a.getFechaFallecimiento()));
        }
        System.out.println();
    }

    
    private void listarAutoresVivosEnAnioBD() {
        System.out.print("Ingrese un año (ej. 1605): ");
        Integer anio = leerEntero();
        if (anio == null) return;

        var autores = autorRepo.findAll();
        Collator collator = Collator.getInstance(new Locale("es"));
        var vivos = autores.stream()
                .filter(a -> {
                    Integer nac = a.getFechaNacimiento();
                    Integer def = a.getFechaFallecimiento();
                    return (nac == null || nac <= anio) && (def == null || def >= anio);
                })
                .sorted(Comparator.comparing(Autor::getNombre, collator))
                .toList();

        if (vivos.isEmpty()) {
            System.out.println("No se encontraron autores vivos en " + anio + " (en tu BD).");
        } else {
            System.out.println("\n Autores vivos en " + anio + ":");
            int i = 1;
            for (Autor a : vivos) {
                System.out.printf(" %d) %s (nac: %s, fallec: %s)%n",
                        i++, nullSafe(a.getNombre()), nullSafe(a.getFechaNacimiento()), nullSafe(a.getFechaFallecimiento()));
            }
        }
        System.out.println();
    }

    
    private void listarLibrosPorIdioma() {
        if (librosRegistrados.isEmpty()) {
            System.out.println("No hay libros registrados en memoria. Busca libros primero.");
            return;
        }
        System.out.println("""
                Seleccione idioma:
                  1.- Español
                  2.- Inglés
                """);
        System.out.print("> ");
        Integer opt = leerEntero();
        if (opt == null) return;

        String code = switch (opt) { case 1 -> "es"; case 2 -> "en"; default -> null; };
        if (code == null) { System.out.println("Opción inválida."); return; }

        var filtrados = librosRegistrados.stream()
                .filter(l -> l.getIdiomas() != null && l.getIdiomas().contains(code))
                .toList();

        if (filtrados.isEmpty()) {
            System.out.println("No hay libros registrados en ese idioma (en memoria).");
            return;
        }

        System.out.println("\n Libros en idioma '" + code + "':");
        int i = 1;
        for (Libro l : filtrados) {
            System.out.printf(" %d) %s — Idiomas: %s%n",
                    i++, l.getTitulo(), String.join(", ", l.getIdiomas()));
        }
        System.out.println();
    }

    
    private Integer leerEntero() {
        String txt = teclado.nextLine().trim();
        try { return Integer.parseInt(txt); }
        catch (NumberFormatException e) { System.out.println("Número inválido."); return null; }
    }
    private static String nullSafe(Object o) { return (o == null) ? "¿?" : o.toString(); }
}
