package com.aluracursos.challengeJPA.service;

public interface IConvierteDatos {
    <T> T obtenerDatos(String json, Class<T> clase);
}
