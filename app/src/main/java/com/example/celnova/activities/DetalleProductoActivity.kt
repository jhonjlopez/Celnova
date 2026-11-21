package com.example.celnova.activities

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.celnova.R
import com.example.celnova.utils.ComprasHelper
import com.example.celnova.utils.Formato
import kotlin.math.roundToInt

class DetalleProductoActivity : AppCompatActivity() {

    private var cantidad = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_producto)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        val productoId = intent.getStringExtra(EXTRA_ID).orEmpty()
        val nombre = intent.getStringExtra(EXTRA_NOMBRE).orEmpty().ifBlank { "Producto Celnova" }
        val precio = intent.getDoubleExtra(EXTRA_PRECIO, 0.0)
        val precioOriginal = intent.getDoubleExtra(EXTRA_PRECIO_ORIGINAL, 0.0)
        val icono = intent.getStringExtra(EXTRA_ICONO).orEmpty()
        val categoria = intent.getStringExtra(EXTRA_CATEGORIA).orEmpty().ifBlank { "Tecnología" }
        val rating = intent.getStringExtra(EXTRA_RATING).orEmpty().ifBlank { "4.8 (125)" }
        val descripcion = intent.getStringExtra(EXTRA_DESCRIPCION).orEmpty().ifBlank {
            "Descubre el $nombre en Celnova: rendimiento, diseño y calidad respaldados por garantía oficial. " +
                "Compra segura y envío gratis a todo el país."
        }

        findViewById<ImageView>(R.id.imgDetalleProducto).setImageResource(iconoDrawable(icono))
        findViewById<TextView>(R.id.txtDetalleTitulo).text = nombre
        findViewById<TextView>(R.id.txtDetalleCategoria).text = categoria
        findViewById<TextView>(R.id.txtDetalleRating).text = rating
        findViewById<TextView>(R.id.txtDetalleDescripcion).text = descripcion
        findViewById<TextView>(R.id.txtDetallePrecio).text = Formato.cop(precio)

        val txtOriginal = findViewById<TextView>(R.id.txtDetallePrecioOriginal)
        val cardBadge = findViewById<View>(R.id.cardDetalleBadge)
        if (precioOriginal > precio && precioOriginal > 0.0) {
            txtOriginal.text = Formato.corto(precioOriginal)
            txtOriginal.paintFlags = txtOriginal.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            val porcentaje = (((precioOriginal - precio) / precioOriginal) * 100).roundToInt()
            findViewById<TextView>(R.id.txtDetalleBadge).text = "-$porcentaje%"
            cardBadge.visibility = View.VISIBLE
            txtOriginal.visibility = View.VISIBLE
        } else {
            cardBadge.visibility = View.GONE
            txtOriginal.visibility = View.GONE
        }

        val txtCantidad = findViewById<TextView>(R.id.txtDetalleCantidad)
        fun actualizarCantidad() {
            txtCantidad.text = cantidad.toString()
        }
        findViewById<ImageView>(R.id.imgDetalleMenos).setOnClickListener {
            if (cantidad > 1) { cantidad--; actualizarCantidad() }
        }
        findViewById<ImageView>(R.id.imgDetalleMas).setOnClickListener {
            if (cantidad < 10) { cantidad++; actualizarCantidad() }
        }

        findViewById<ImageView>(R.id.imgDetalleAtras).setOnClickListener { finish() }
        findViewById<ImageView>(R.id.imgDetalleCarrito).setOnClickListener {
            startActivity(Intent(this, CarritoActivity::class.java))
        }

        findViewById<View>(R.id.btnDetalleAgregar).setOnClickListener {
            ComprasHelper.agregarAlCarrito(
                activity = this,
                productoId = productoId,
                nombre = nombre,
                precio = precio,
                icono = icono,
                cantidad = cantidad,
                irAlCarrito = true
            )
        }

        findViewById<View>(R.id.btnDetalleSeguirComprando).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            })
            finish()
        }
    }

    private fun iconoDrawable(nombre: String): Int {
        if (nombre.isBlank()) return R.drawable.icono_celular
        val id = resources.getIdentifier(nombre, "drawable", packageName)
        return if (id != 0) id else R.drawable.icono_celular
    }

    companion object {
        const val EXTRA_ID = "detalle_producto_id"
        const val EXTRA_NOMBRE = "detalle_producto_nombre"
        const val EXTRA_PRECIO = "detalle_producto_precio"
        const val EXTRA_PRECIO_ORIGINAL = "detalle_producto_precio_original"
        const val EXTRA_ICONO = "detalle_producto_icono"
        const val EXTRA_CATEGORIA = "detalle_producto_categoria"
        const val EXTRA_RATING = "detalle_producto_rating"
        const val EXTRA_DESCRIPCION = "detalle_producto_descripcion"
    }
}
