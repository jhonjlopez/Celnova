package com.example.celnova.utils

object ValidadorCorreo {

    fun esCorreoValido(correo: String): Boolean {
        val correoLimpio = correo.trim().lowercase()
        if (correoLimpio.isEmpty()) return false

        val patronEmail = android.util.Patterns.EMAIL_ADDRESS.matcher(correoLimpio).matches()
        if (!patronEmail) return false

        val partesArroba = correoLimpio.split("@")
        if (partesArroba.size != 2) return false
        val dominio = partesArroba[1]

        if (dominio.startsWith("gmail.")) {
            if (dominio != "gmail.com") return false
        }

        val partesPunto = correoLimpio.split(".")
        val extension = partesPunto.lastOrNull() ?: ""

        return extension.length >= 2 && extension.all { it.isLetter() }
    }
}
