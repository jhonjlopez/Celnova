package com.example.celnova.utils

object Checkout {

    const val CUPON_OFERTON10 = "oferton10"

    const val EXTRA_SUBTOTAL = "checkout_subtotal"
    const val EXTRA_DESCUENTO = "checkout_descuento"
    const val EXTRA_TOTAL = "checkout_total"
    const val EXTRA_CUPON = "checkout_cupon"
    const val EXTRA_DIRECCION = "checkout_direccion"
    const val EXTRA_PAIS = "checkout_pais"
    const val EXTRA_CODIGO_POSTAL = "checkout_codigo_postal"
    const val EXTRA_METODO = "checkout_metodo"
    const val EXTRA_ORDEN_ID = "checkout_orden_id"
    const val EXTRA_FECHA = "checkout_fecha"

    data class Totales(
        val subtotal: Double,
        val descuento: Double,
        val impuestos: Double,
        val total: Double
    )

    fun calcular(subtotal: Double, cuponAplicado: Boolean): Totales {
        val descuento = if (cuponAplicado) subtotal * 0.10 else 0.0
        val base = subtotal - descuento
        val impuestos = base - (base / 1.19)
        return Totales(subtotal, descuento, impuestos, base)
    }
}
