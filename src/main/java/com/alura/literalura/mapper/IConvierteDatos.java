package com.alura.literalura.mapper;

public interface IConvierteDatos {
    <T> T obtenerDatos(String json, Class<T> clase);
}
