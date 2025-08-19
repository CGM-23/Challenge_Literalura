package com.example.Literalura.servicios;

public interface IConvierteDatos {

    <T> T obtenerDatos(String json, Class<T> clase);

}