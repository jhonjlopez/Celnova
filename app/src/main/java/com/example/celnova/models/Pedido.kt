package com.example.celnova.models

data class Pedido(
    val cliente: String = "",
    val items: List<CarritoItem> = emptyList(),
    val total: Double = 0.0,
    val estado: String = "pendiente",
    val createdAt: Long = 0L,
    val subtotal: Double = 0.0,
    val descuento: Double = 0.0,
    val cupon: String = "",
    val direccion: String = "",
    val metodoPago: String = ""
)
