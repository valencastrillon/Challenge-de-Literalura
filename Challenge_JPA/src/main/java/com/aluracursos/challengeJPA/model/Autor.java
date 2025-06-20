package com.aluracursos.challengeJPA.model;

import jakarta.persistence.*;

@Entity
@Table(name = "autores")
public class Autor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private Integer fechaNacimiento;
    private Integer fechaFallecimiento;

    public Autor() {}

    public Autor(DatosAutor datosAutor) {
        this.nombre = datosAutor.nombre();
        this.fechaNacimiento = datosAutor.fechaNacimiento();
        this.fechaFallecimiento = datosAutor.fechaFallecimiento();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Integer getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(Integer fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    public Integer getFechaFallecimiento() { return fechaFallecimiento; }
    public void setFechaFallecimiento(Integer fechaFallecimiento) { this.fechaFallecimiento = fechaFallecimiento; }

    @Override
    public String toString() {
        String fechaNacimientoStr = (fechaNacimiento != null) ? fechaNacimiento.toString() : "N/A";
        String fechaFallecimientoStr = (fechaFallecimiento != null) ? fechaFallecimiento.toString() : "N/A";

        String plantillaMensaje = """
            ----- AUTOR -----
            Nombre: %s
            Fecha de Nacimiento: %s
            Fecha de Fallecimiento: %s
            -----------------
            """;
        return String.format(plantillaMensaje, nombre, fechaNacimientoStr, fechaFallecimientoStr);
    }
}
