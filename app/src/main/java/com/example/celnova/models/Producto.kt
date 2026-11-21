package com.example.celnova.models

data class Producto(
    val id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val precio: Double = 0.0,
    val categoria: String = "Otros",
    val stock: Int = 0,
    val imagenUrl: String = "",
    val vendedor: String = "",
    val estado: String = "activo",
    val createdAt: Long = 0L
)
