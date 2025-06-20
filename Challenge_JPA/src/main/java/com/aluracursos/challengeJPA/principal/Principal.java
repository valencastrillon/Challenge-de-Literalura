package com.aluracursos.challengeJPA.principal;

import com.aluracursos.challengeJPA.model.*;
import com.aluracursos.challengeJPA.repository.AutorRepository;
import com.aluracursos.challengeJPA.repository.LibroRepository;
import com.aluracursos.challengeJPA.service.ConsumoAPI;
import com.aluracursos.challengeJPA.service.ConvierteDatos;
import com.aluracursos.challengeJPA.model.Categoria;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private static final String URL_BASE = "https://gutendex.com/books/";
    private ConvierteDatos conversor = new ConvierteDatos();

    private LibroRepository libroRepository;
    private AutorRepository autorRepository;

    private List<Libro> libros;
    private List<Autor> autores;
    private Optional<Libro> libroBuscado;

    public Principal(LibroRepository libroRepository, AutorRepository autorRepository) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
    }

    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    ------------
                    Elija la opción a través de su número
                    1-	Buscar libro por título
                    2-	Listar libros registrados
                    3-	Listar autores registrados
                    4-	Listar autores vivos en un determinado año
                    5-	Listar libros por idioma
                    0-	Salir
                    """;
            System.out.println(menu);
            try {
                opcion = teclado.nextInt();
                teclado.nextLine();

                switch (opcion) {
                    case 1:
                        buscarLibroo();
                        break;
                    case 2:
                        mostrarlibrosBuscados();
                        break;
                    case 3:
                        listarAutoresRegistrados();
                        break;
                    case 4:
                        listarAutoresVivosEnAnio();
                        break;
                    case 5:
                        buscarLibrosPorIdioma();
                        break;
                    case 0:
                        System.out.println("Finalizando aplicativo...");
                        System.out.println("Gracias por utilizar nuestros servicios.");
                        break;
                    default:
                        System.out.println("Opción invalida, favor escoger un número acorde con las opciones del menú");
                        break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Por favor, ingrese un número.");
                teclado.nextLine();
                opcion = -1;
            }
        }
    }



    private Datos getDatosDeLaApi(String busqueda) {
        var json = consumoAPI.obtenerDatos(URL_BASE + "?search=" + busqueda.replace(" ", "+"));
        try {
            return conversor.obtenerDatos(json, Datos.class);
        } catch (Exception e) {
            System.out.println("Error al procesar JSON de la API: " + e.getMessage());
            return null;
        }
    }


    @Transactional
    private void buscarLibroo() {
        System.out.println("Ingrese el nombre del libro que desea buscar");
        var tituloBusqueda = teclado.nextLine();
        Datos datosBusqueda = getDatosDeLaApi(tituloBusqueda);

        if (datosBusqueda != null && datosBusqueda.resultados() != null && !datosBusqueda.resultados().isEmpty()) {
            DatosLibro datosPrimerLibro = datosBusqueda.resultados().get(0);

            Optional<Libro> libroExistente = libroRepository.findByTituloContainsIgnoreCase(datosPrimerLibro.titulo());
            if (libroExistente.isPresent()) {
                System.out.println("El libro '" + datosPrimerLibro.titulo() + "' ya está registrado.");
                return;
            }

            Libro libro = new Libro(datosPrimerLibro);

            // Manejar la persistencia de autores para este único libro
            if (datosPrimerLibro.autores() != null && !datosPrimerLibro.autores().isEmpty()) {
                DatosAutor datosPrimerAutor = datosPrimerLibro.autores().get(0);

                Optional<Autor> autorExistente = autorRepository.findByNombreIgnoreCase(datosPrimerAutor.nombre());
                if (autorExistente.isPresent()) {
                    // Aseguramos que la entidad sea "manejada" por la transacción actual.
                    // Si ya lo está, simplemente la devuelve. Si no, la "adjunta" a la sesión.
                    // autorRepository.save() ya debería hacer esto, pero a veces el contexto
                    // puede ser sutilmente diferente.
                    Autor autorAAsignar = autorExistente.get();
                    // NO ES NECESARIO EL SIGUIENTE SAVE() AQUÍ CON @Transactional SI ES SOLO RECUPERAR
                    // Si el problema persiste, es porque autorExistente.get() no está en el estado 'manejado'
                    // de la transacción actual, incluso con @Transactional. Esto suele ser raro.
                    // Para forzarlo a ser manejado, una opción es:
                    // autorAAsignar = autorRepository.findById(autorAAsignar.getId()).orElseThrow(); // Recuperar dentro de la misma transacción
                    // o incluso:
                    // autorAAsignar = autorRepository.save(autorAAsignar); // Este es el que ya tienes y debería funcionar.

                    libro.setAutor(autorAAsignar);
                } else {
                    Autor nuevoAutor = new Autor(datosPrimerAutor);
                    libro.setAutor(nuevoAutor);
                }
            } else {
                System.out.println("Advertencia: El libro '" + datosPrimerLibro.titulo() + "' no tiene autores listados. Se asignará 'N/A'.");
                libro.setAutor(null); // O podrías tener un autor por defecto para "Desconocido"
            }

            // *** La lógica del idioma permanece igual o se adapta al nuevo cambio ***
            List<String> idiomasDelLibro = new ArrayList<>();
            if (datosPrimerLibro.idiomas() != null && !datosPrimerLibro.idiomas().isEmpty()) {
                String primerIdioma = datosPrimerLibro.idiomas().get(0);
                idiomasDelLibro.add(primerIdioma);
            } else {
                System.out.println("Advertencia: El libro '" + datosPrimerLibro.titulo() + "' no tiene idiomas listados.");
            }
            libro.setIdiomas(idiomasDelLibro); // Asignar la lista (con un solo idioma o vacía) al libro

            try {
                libroRepository.save(libro);
                System.out.println("Libro guardado con éxito: " + libro.getTitulo());
                System.out.println(libro);
            } catch (DataIntegrityViolationException e) {
                System.out.println("Error: El libro '" + libro.getTitulo() + "' ya existe en la base de datos.");
            } catch (Exception e) {
                System.out.println("Error al guardar el libro '" + libro.getTitulo() + "': " + e.getMessage());
            }

        } else {
            System.out.println("No se encontraron resultados para la búsqueda '" + tituloBusqueda + "'.");
        }
    }



    private void mostrarlibrosBuscados() {
        libros = libroRepository.findAll();
        if (libros.isEmpty()) {
            System.out.println("No hay libros registrados aún.");
        } else {
            System.out.println("--- Libros Registrados ---");
            libros.stream()
                    .sorted(Comparator.comparing(Libro::getTitulo))
                    .forEach(System.out::println);
            System.out.println("--------------------------");
        }
    }



    private void listarAutoresRegistrados() {
        autores = autorRepository.findAll();
        if (autores.isEmpty()) {
            System.out.println("No hay autores registrados aún.");
        } else {
            System.out.println("--- Autores Registrados ---");
            autores.stream()
                    .sorted(Comparator.comparing(Autor::getNombre))
                    .forEach(System.out::println);
            System.out.println("--------------------------");
        }
    }



    private void listarAutoresVivosEnAnio() {
        System.out.println("Ingrese el año vivo de autor(es) que desea buscar");
        try {
            Integer anio = teclado.nextInt();
            teclado.nextLine();

            List<Autor> autoresVivos = autorRepository.findAutoresVivosEnAnio(anio);

            if (autoresVivos.isEmpty()) {
                System.out.println("No se encontraron autores que estuvieron vivos en el año " + anio + ".");
            } else {
                System.out.println("--- Autores vivos en el año " + anio + " ---");
                autoresVivos.stream()
                        .sorted(Comparator.comparing(Autor::getNombre))
                        .forEach(System.out::println);
                System.out.println("----------------------------------------");
            }
        } catch (InputMismatchException e) {
            System.out.println("Entrada inválida. Por favor, ingrese un año válido (número entero).");
            teclado.nextLine();
        }
    }



    private void buscarLibrosPorIdioma(){
        System.out.println("""
            Ingrese el idioma para buscar los libros:
            es- español
            en- inglés
            fr- francés
            pt- portugués
            """);
        var entradaUsuario = teclado.nextLine().toLowerCase();

        Categoria categoriaSeleccionada = Categoria.fromAbreviatura(entradaUsuario);
        if (categoriaSeleccionada == Categoria.DESCONOCIDO) {
            categoriaSeleccionada = Categoria.fromEspanol(entradaUsuario);
        }
        if (categoriaSeleccionada == Categoria.DESCONOCIDO) {
            System.out.println("Idioma no reconocido o no soportado. Por favor, ingrese una de las opciones listadas.");
            return;
        }
        List<Libro> librosPorIdioma = libroRepository.findByIdiomasContains(categoriaSeleccionada.getCategoriaAbreviatura());

        if (librosPorIdioma.isEmpty()) {
            System.out.println("No se encontraron libros para el idioma: " + categoriaSeleccionada.getCategoriaEspanol());
        } else {
            System.out.println("--- Libros en " + categoriaSeleccionada.getCategoriaEspanol() + " ---");
            librosPorIdioma.stream()
                    .sorted(Comparator.comparing(Libro::getTitulo)) // Opcional: ordenar por título
                    .forEach(System.out::println);
            System.out.println("---------------------------------");
        }
    }
}