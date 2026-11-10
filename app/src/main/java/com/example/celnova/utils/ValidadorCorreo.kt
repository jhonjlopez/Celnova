package com.example.celnova.utils

object ValidadorCorreo {

    fun esCorreoValido(correo: String): Boolean {
        val correoLimpio = correo.trim().lowercase()
        if (correoLimpio.isEmpty()) return false

        // 1. Validar el patrón oficial estándar de correo
        val patronEmail = android.util.Patterns.EMAIL_ADDRESS.matcher(correoLimpio).matches()
        if (!patronEmail) return false

        // 2. Separar usuario y dominio por el símbolo '@'
        val partesArroba = correoLimpio.split("@")
        if (partesArroba.size != 2) return false
        val dominio = partesArroba[1]

        // 3. REGLA ESTRICTA DE GMAIL: Si el dominio pertenece a Google, debe ser 100% "gmail.com"
        if (dominio.startsWith("gmail.")) {
            if (dominio != "gmail.com") return false
        }

        // 4. Validar extensión del dominio (.com, .co, .es, .org, etc.) -> mínimo 2 letras y solo letras
        val partesPunto = correoLimpio.split(".")
        val extension = partesPunto.lastOrNull() ?: ""

        return extension.length >= 2 && extension.all { it.isLetter() }
    }
}
