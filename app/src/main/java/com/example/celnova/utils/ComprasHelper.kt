package com.example.celnova.utils

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.celnova.activities.CarritoActivity
import com.example.celnova.activities.DetalleProductoActivity
import com.example.celnova.models.CarritoItem
import com.example.celnova.services.AuthService
import com.example.celnova.services.CarritoService

object ComprasHelper {

    fun abrirDetalle(
        activity: Activity,
        tarjeta: View,
        productoId: String,
        tituloId: Int,
        precioId: Int,
        precioOriginalId: Int? = null,
        icono: String,
        categoria: String,
        rating: String = "4.8 (125)",
        descripcion: String = ""
    ) {
        val nombre = tarjeta.findViewById<TextView>(tituloId)?.text?.toString()?.trim().orEmpty()
        val precio = precioDe(tarjeta, precioId)
        val original = precioOriginalId?.let { precioDe(tarjeta, it) } ?: 0.0

        val intent = Intent(activity, DetalleProductoActivity::class.java).apply {
            putExtra(DetalleProductoActivity.EXTRA_ID, productoId)
            putExtra(DetalleProductoActivity.EXTRA_NOMBRE, nombre)
            putExtra(DetalleProductoActivity.EXTRA_PRECIO, precio)
            putExtra(DetalleProductoActivity.EXTRA_PRECIO_ORIGINAL, original)
            putExtra(DetalleProductoActivity.EXTRA_ICONO, icono)
            putExtra(DetalleProductoActivity.EXTRA_CATEGORIA, categoria)
            putExtra(DetalleProductoActivity.EXTRA_RATING, rating)
            putExtra(DetalleProductoActivity.EXTRA_DESCRIPCION, descripcion)
        }
        tarjeta.setOnClickListener { activity.startActivity(intent) }
    }

    fun agregarAlCarrito(
        activity: Activity,
        productoId: String,
        nombre: String,
        precio: Double,
        icono: String,
        cantidad: Int = 1,
        irAlCarrito: Boolean = true
    ) {
        val cliente = AuthService.currentEmail()
        if (cliente == null) {
            Toast.makeText(activity, "Inicia sesión para agregar productos al carrito", Toast.LENGTH_SHORT).show()
            return
        }

        val id = productoId.ifBlank { "prod_${nombre.hashCode()}" }
        val item = CarritoItem(
            productoId = id,
            nombre = nombre,
            precio = precio,
            cantidad = cantidad.coerceAtLeast(1),
            icono = icono
        )

        CarritoService.addItem(
            cliente,
            item,
            onSuccess = {
                if (irAlCarrito) {
                    activity.startActivity(Intent(activity, CarritoActivity::class.java))
                } else {
                    Toast.makeText(activity, "Producto agregado al carrito", Toast.LENGTH_SHORT).show()
                }
            },
            onError = {
                Toast.makeText(activity, "No se pudo agregar al carrito: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun precioDe(vista: View, id: Int): Double {
        val texto = vista.findViewById<TextView>(id)?.text?.toString().orEmpty()
        return texto.filter { it.isDigit() }.toDoubleOrNull() ?: 0.0
    }
}
