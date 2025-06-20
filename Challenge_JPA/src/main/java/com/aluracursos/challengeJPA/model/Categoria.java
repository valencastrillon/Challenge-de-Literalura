package com.aluracursos.challengeJPA.model;

public enum Categoria {
    ESPANOL("es", "español"),
    INGLES("en", "inglés"),
    FRANCES("fr", "francés"),
    PORTUGUES("pt", "portugués"), // Renombrado para evitar guiones
    DESCONOCIDO("un", "desconocido"); // Para manejar idiomas no mapeados
    private String categoriaAbreviatura;
    private String categoriaEspanol;
    Categoria(String abreviatura, String nombreEspanol) {
        this.categoriaAbreviatura = abreviatura;
        this.categoriaEspanol = nombreEspanol;
    }
    public String getCategoriaAbreviatura() { return categoriaAbreviatura; }
    public String getCategoriaEspanol() { return categoriaEspanol; }

    public static Categoria fromAbreviatura(String text) {
        for (Categoria categoria : Categoria.values()) {
            if (categoria.categoriaAbreviatura.equalsIgnoreCase(text)) {
                return categoria;
            }
        }
        return DESCONOCIDO;
    }

    public static Categoria fromEspanol(String text) {
        for (Categoria categoria : Categoria.values()) {
            if (categoria.categoriaEspanol.equalsIgnoreCase(text)) {
                return categoria;
            }
        }
        return DESCONOCIDO;
    }
}