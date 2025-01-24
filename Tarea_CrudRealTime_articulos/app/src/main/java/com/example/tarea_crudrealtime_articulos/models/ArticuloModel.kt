package com.example.tarea_crudrealtime_articulos.models

import java.io.Serializable

// Es obligatorio inicializar los valores por defecto
data class ArticuloModel (
    val nombre: String = "",
    val descripcion: String = "",
    val precio: Float = 0f
): Serializable