package com.example.celnova.services

import android.content.Context
import android.content.SharedPreferences

object SesionService {

    private const val PREFS = "celnova_sesion"
    private const val KEY_EMAIL = "email"

    private lateinit var prefs: SharedPreferences

    fun inicializar(context: Context) {
        prefs = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    }

    fun guardar(email: String) {
        if (::prefs.isInitialized) {
            prefs.edit().putString(KEY_EMAIL, email.trim().lowercase()).apply()
        }
    }

    fun obtener(): String? =
        if (::prefs.isInitialized) prefs.getString(KEY_EMAIL, null)?.takeIf { it.isNotBlank() } else null

    fun cerrar() {
        if (::prefs.isInitialized) prefs.edit().remove(KEY_EMAIL).apply()
    }
}
