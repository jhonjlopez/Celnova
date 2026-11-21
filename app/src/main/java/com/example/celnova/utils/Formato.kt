package com.example.celnova.utils

import java.text.NumberFormat
import java.util.Locale

object Formato {

    private val numero: NumberFormat
        get() = NumberFormat.getNumberInstance(Locale("es", "CO")).apply {
            maximumFractionDigits = 0
            minimumFractionDigits = 0
        }

    fun cop(valor: Double): String = "$ ${numero.format(valor)} COP"

    fun corto(valor: Double): String = "$${numero.format(valor)}"
}
