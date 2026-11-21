package com.example.celnova.models

data class UsuarioFirebase(
    val email: String = "",
    val contrasena: String = "",
    val rol: String = "comprador",
    val nombre: String = "",
    val telefono: String = "",
    val createdAt: Long = 0L
)
