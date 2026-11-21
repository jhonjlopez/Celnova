package com.example.celnova.utils

object ValidadorNombre {

    private val palabrasReservadas = setOf(
        "admin", "administrator", "test", "user", "null", "undefined", "prueba", "root", "system"
    )

    private val particulasValidas = setOf("de", "del", "la", "las", "los", "van", "von", "da", "di")

    fun capitalizar(nombreCompleto: String): String {
        if (nombreCompleto.isEmpty()) return ""
        val endsWithSpace = nombreCompleto.endsWith(" ")
        val palabras = nombreCompleto.trimStart().split("\\s+".toRegex())
        val resultado = palabras.joinToString(" ") { palabra ->
            if (palabra.lowercase() in particulasValidas) {
                palabra.lowercase()
            } else {
                palabra.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            }
        }
        return if (endsWithSpace && !resultado.endsWith(" ")) "$resultado " else resultado
    }

    fun esNombreValido(nombreCompleto: String): Boolean {
        val texto = nombreCompleto.trim()

        if (texto.isEmpty()) return false

        if (texto.length < 5 || texto.length > 100) return false

        if (texto.contains("  ")) return false

        if (texto.contains("<") || texto.contains(">") || texto.contains("script", ignoreCase = true)) return false

        val palabras = texto.split("\\s+".toRegex())
        if (palabras.size < 2) return false

        if (palabras.any { it.length < 2 }) return false

        if (palabras.any { it.lowercase() in palabrasReservadas }) return false

        val tieneRepeticionBasura = "(.)\\1{2,}".toRegex().containsMatchIn(texto.lowercase())
        if (tieneRepeticionBasura) return false

        val tieneConsonantesRaras = "[bcdfghjklmnñpqrstvwxyzBCDFGHJKLMNÑPQRSTVWXYZ]{4,}".toRegex().containsMatchIn(texto)
        if (tieneConsonantesRaras) return false

        val patronCaracteres = "^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜçÇøØæÆåÅßäöÄÖ'\\-\\s]+$".toRegex()
        if (!patronCaracteres.matches(texto)) return false

        for (palabra in palabras) {
            if (palabra.lowercase() !in particulasValidas) {
                if (!palabra[0].isUpperCase()) return false
            }
        }

        return true
    }
}
