package com.aluracursos.challengeJPA.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

@Entity
@Table(name = "libros")
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String titulo;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinColumn(name = "autor_id")
    private Autor autor;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "libro_idiomas", joinColumns = @JoinColumn(name = "libro_id"))
    @Column(name = "idioma")
    private List<String> idiomas = new ArrayList<>();

    private Double numeroDeDescargas;

    public Libro(){}

    public Libro(DatosLibro datosLibro) {
        this.titulo = datosLibro.titulo();
        //this.autores = datosLibro.autores().stream().map(Autor::new).collect(Collectors.toList());
        //this.idiomas = datosLibro.idiomas();
        this.numeroDeDescargas = OptionalDouble.of(datosLibro.numeroDeDescargas())
                .orElse(0);
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public Autor getAutor() { return autor; }
    public void setAutor(Autor autor) { this.autor = autor; }
    public List<String> getIdiomas() { return idiomas; }
    public void setIdiomas(List<String> idiomas) { this.idiomas = idiomas; }
    public Double getNumeroDeDescargas() { return numeroDeDescargas; }
    public void setNumeroDeDescargas(Double numeroDeDescargas) { this.numeroDeDescargas = numeroDeDescargas; }


    @Override
    public String toString() {
        String nombreAutor = (autor != null && autor.getNombre() != null) ? autor.getNombre() : "N/A"; // Obtener nombre del objeto Autor
        String idiomasConcatenados = idiomas != null && !idiomas.isEmpty() ?
                String.join(", ", idiomas) : "N/A";

        String plantillaMensaje = """
                ----- LIBRO -----
                Titulo: %s
                Autor: %s
                Idioma: %s
                Numero de descargas: %.0f
                -----------------
                """;
        return String.format(plantillaMensaje, titulo, nombreAutor, idiomasConcatenados, numeroDeDescargas);
    }
}