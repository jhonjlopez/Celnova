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

        // 1. Obligatoriedad (Campo no vacío)
        if (texto.isEmpty()) return false

        // 2. Longitud mínima y máxima (5 a 100 caracteres)
        if (texto.length < 5 || texto.length > 100) return false

        // 3. Bloqueo de espacios múltiples seguidos ("  ")
        if (texto.contains("  ")) return false

        // 4. Sanitización/Seguridad (No etiquetas HTML ni script)
        if (texto.contains("<") || texto.contains(">") || texto.contains("script", ignoreCase = true)) return false

        // 5. Estructura de Nombre y Apellido (Mínimo 2 palabras)
        val palabras = texto.split("\\s+".toRegex())
        if (palabras.size < 2) return false

        // 6. Longitud mínima por palabra (mínimo 2 caracteres)
        if (palabras.any { it.length < 2 }) return false

        // 7. Filtro de palabras reservadas / falsas (Admin, Test, Null, etc.)
        if (palabras.any { it.lowercase() in palabrasReservadas }) return false

        // 8. Anti-Texto basura (3 o más letras iguales seguidas ej: "xxxx", "Jhonnn")
        val tieneRepeticionBasura = "(.)\\1{2,}".toRegex().containsMatchIn(texto.lowercase())
        if (tieneRepeticionBasura) return false

        // 9. Anti-Teclado aleatorio (4 o más consonantes seguidas ej: "jsjd", "asdfgh")
        val tieneConsonantesRaras = "[bcdfghjklmnñpqrstvwxyzBCDFGHJKLMNÑPQRSTVWXYZ]{4,}".toRegex().containsMatchIn(texto)
        if (tieneConsonantesRaras) return false

        // 10. Caracteres permitidos (Letras, tildes, eñes, diacríticos europeos, apóstrofes y guiones)
        val patronCaracteres = "^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜçÇøØæÆåÅßäöÄÖ'\\-\\s]+$".toRegex()
        if (!patronCaracteres.matches(texto)) return false

        // 10. Formato de Nombre Propio (Mayúscula al inicio de cada palabra principal)
        for (palabra in palabras) {
            if (palabra.lowercase() !in particulasValidas) {
                if (!palabra[0].isUpperCase()) return false
            }
        }

        return true
    }
}
