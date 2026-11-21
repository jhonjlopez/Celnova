package com.example.celnova.activities

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.celnova.R
import com.example.celnova.models.CarritoItem
import com.example.celnova.services.AuthService
import com.example.celnova.services.CarritoService
import com.example.celnova.utils.Checkout
import com.example.celnova.utils.Formato
import com.google.android.material.bottomnavigation.BottomNavigationView

class CarritoActivity : AppCompatActivity() {

    private var items = emptyList<CarritoItem>()
    private var cuponAplicado = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrito)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val navColorStateList = ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_checked),
                intArrayOf(-android.R.attr.state_checked)
            ),
            intArrayOf(
                ContextCompat.getColor(this, R.color.navItemActivo),
                ContextCompat.getColor(this, R.color.navItemInactivo)
            )
        )

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationCarrito)
        bottomNav.itemIconTintList = navColorStateList
        bottomNav.itemTextColor = navColorStateList
        bottomNav.itemActiveIndicatorColor = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.navIndicadorFondo))
        bottomNav.selectedItemId = R.id.nav_carrito

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_inicio -> { irAInicio(); true }
                R.id.nav_categorias -> {
                    startActivity(Intent(this, CategoriasActivity::class.java)); finish(); true
                }
                R.id.nav_buscar -> {
                    startActivity(Intent(this, BuscarActivity::class.java)); finish(); true
                }
                R.id.nav_carrito -> true
                R.id.nav_perfil -> {
                    startActivity(Intent(this, PerfilActivity::class.java)); finish(); true
                }
                else -> false
            }
        }

        findViewById<View>(R.id.btnCarritoSeguirComprando).setOnClickListener { irAInicio() }

        findViewById<View>(R.id.btnCarritoPagar).setOnClickListener {
            if (AuthService.currentEmail() == null) {
                Toast.makeText(this, "Inicia sesión para continuar con el pago", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (items.isEmpty()) {
                Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val totales = Checkout.calcular(subtotal(), cuponAplicado)
            startActivity(Intent(this, PagoEnvioActivity::class.java).apply {
                putExtra(Checkout.EXTRA_SUBTOTAL, totales.subtotal)
                putExtra(Checkout.EXTRA_DESCUENTO, totales.descuento)
                putExtra(Checkout.EXTRA_TOTAL, totales.total)
                putExtra(Checkout.EXTRA_CUPON, if (cuponAplicado) Checkout.CUPON_OFERTON10 else "")
            })
        }

        findViewById<View>(R.id.btnCarritoAplicarCupon).setOnClickListener {
            val codigo = findViewById<EditText>(R.id.etCarritoCupon).text.toString().trim()
            val mensaje = findViewById<TextView>(R.id.txtCarritoCuponMensaje)
            if (codigo.equals(Checkout.CUPON_OFERTON10, ignoreCase = true)) {
                cuponAplicado = true
                mensaje.visibility = View.GONE
                Toast.makeText(this, "Cupón aplicado: 10% de descuento", Toast.LENGTH_SHORT).show()
            } else {
                cuponAplicado = false
                mensaje.text = "Código inválido"
                mensaje.visibility = View.VISIBLE
                Toast.makeText(this, "Código inválido", Toast.LENGTH_SHORT).show()
            }
            actualizarResumen()
        }

        findViewById<View>(R.id.imgCarritoCuponClose).setOnClickListener {
            cuponAplicado = false
            findViewById<EditText>(R.id.etCarritoCupon).text?.clear()
            findViewById<TextView>(R.id.txtCarritoCuponMensaje).visibility = View.GONE
            actualizarResumen()
        }

        refrescarCarrito()
    }

    private fun irAInicio() {
        startActivity(Intent(this, HomeActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        })
        finish()
    }

    private fun subtotal(): Double = items.sumOf { it.precio * it.cantidad }

    private fun refrescarCarrito() {
        val cliente = AuthService.currentEmail()
        if (cliente == null) {
            items = emptyList()
            updateListaItems()
            return
        }
        CarritoService.listar(
            cliente,
            onSuccess = { lista -> items = lista; updateListaItems() },
            onError = {
                Toast.makeText(this, "Error al leer el carrito: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun updateListaItems() {
        val contenedor = findViewById<LinearLayout>(R.id.linearLayoutCarritoItems)
        val vacio = findViewById<TextView>(R.id.txtCarritoItemsVacio)
        contenedor.removeAllViews()
        contenedor.addView(vacio)
        vacio.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE

        items.forEach { item ->
            val fila = LayoutInflater.from(this).inflate(R.layout.item_carrito, contenedor, false)

            val iconoId = if (item.icono.isNotBlank()) {
                resources.getIdentifier(item.icono, "drawable", packageName)
            } else 0
            fila.findViewById<ImageView>(R.id.imgItemCarrito)
                .setImageResource(if (iconoId != 0) iconoId else R.drawable.icono_celular)

            fila.findViewById<TextView>(R.id.txtItemCarritoTitulo).text = item.nombre
            fila.findViewById<TextView>(R.id.txtItemCarritoSub).text =
                "${item.cantidad} x ${Formato.corto(item.precio)}  •  Envío gratis"
            fila.findViewById<TextView>(R.id.txtItemCarritoPrecio).text =
                Formato.cop(item.precio * item.cantidad)
            fila.findViewById<TextView>(R.id.txtItemCarritoCantidad).text = item.cantidad.toString()

            fila.findViewById<View>(R.id.imgItemCarritoEliminar).setOnClickListener {
                cambiarCantidad(item, 0)
            }
            fila.findViewById<View>(R.id.imgItemCarritoMenos).setOnClickListener {
                cambiarCantidad(item, item.cantidad - 1)
            }
            fila.findViewById<View>(R.id.imgItemCarritoMas).setOnClickListener {
                cambiarCantidad(item, item.cantidad + 1)
            }

            contenedor.addView(fila)
        }

        findViewById<TextView>(R.id.txtCarritoSubtitulo).text =
            if (items.isEmpty()) "Revisa los productos que elegiste"
            else "${items.sumOf { it.cantidad }} producto(s) en tu carrito"

        actualizarResumen()
    }

    private fun cambiarCantidad(item: CarritoItem, nueva: Int) {
        val cliente = AuthService.currentEmail() ?: return
        val accion: (() -> Unit) -> Unit = { callback ->
            if (nueva <= 0) {
                CarritoService.eliminarItem(cliente, item.productoId, callback) { mostrarError(it) }
            } else {
                CarritoService.actualizarCantidad(cliente, item.productoId, nueva, callback) { mostrarError(it) }
            }
        }
        accion { refrescarCarrito() }
    }

    private fun mostrarError(e: Exception) {
        Toast.makeText(this, "No se pudo actualizar el carrito: ${e.message}", Toast.LENGTH_SHORT).show()
    }

    private fun actualizarResumen() {
        val totales = Checkout.calcular(subtotal(), cuponAplicado)
        findViewById<TextView>(R.id.txtCarritoSubtotalValor).text = Formato.cop(totales.subtotal)
        findViewById<TextView>(R.id.txtCarritoDescuentoValor).text =
            if (cuponAplicado) "- ${Formato.cop(totales.descuento)}" else "- ${Formato.cop(0.0)}"
        findViewById<TextView>(R.id.txtCarritoImpuestosValor).text = Formato.cop(totales.impuestos)
        findViewById<TextView>(R.id.txtCarritoTotalValor).text = Formato.cop(totales.total)
        findViewById<TextView>(R.id.txtCarritoTotalValorResumen).text = Formato.cop(totales.total)
        findViewById<TextView>(R.id.txtCarritoAhorroValor).text =
            if (cuponAplicado) "Ahorraste ${Formato.corto(totales.descuento)}" else ""

        findViewById<View>(R.id.cardCarritoCuponAplicado).visibility =
            if (cuponAplicado) View.VISIBLE else View.GONE
    }
}
