package com.aluracursos.challengeJPA.repository;

import com.aluracursos.challengeJPA.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

@Repository
public interface LibroRepository extends JpaRepository<Libro,Long> {
    Optional<Libro> findByTituloContainsIgnoreCase(String nombreLibro);
    List<Libro> findByIdiomasContains(String idioma);
}