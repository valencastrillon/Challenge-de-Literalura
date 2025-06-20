package com.aluracursos.challengeJPA.repository;

import com.aluracursos.challengeJPA.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AutorRepository extends JpaRepository<Autor, Long> {
    Optional<Autor> findByNombre(String nombre);
    Optional<Autor> findByNombreIgnoreCase(String nombre);

    @Query("SELECT a FROM Autor a WHERE a.fechaNacimiento <= :anio AND (a.fechaFallecimiento >= :anio OR a.fechaFallecimiento IS NULL)")
    List<Autor> findAutoresVivosEnAnio(Integer anio);
}