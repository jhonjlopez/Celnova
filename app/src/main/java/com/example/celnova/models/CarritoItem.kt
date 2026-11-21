package com.example.celnova.models

data class CarritoItem(
    val productoId: String = "",
    val nombre: String = "",
    val precio: Double = 0.0,
    val cantidad: Int = 1,
    val imagenUrl: String = "",
    val icono: String = ""
)
