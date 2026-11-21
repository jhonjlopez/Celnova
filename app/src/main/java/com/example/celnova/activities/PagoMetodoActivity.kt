package com.example.celnova.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.celnova.R
import com.example.celnova.utils.Checkout
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PagoMetodoActivity : AppCompatActivity() {

    private var metodoSeleccionado = "Tarjeta"

    private class Opcion(
        val card: MaterialCardView,
        val chevron: ImageView,
        val icono: ImageView,
        val textos: LinearLayout,
        val nombre: String
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pago_metodo)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val opciones = listOf(
            Opcion(
                findViewById(R.id.cardPagoMetodoTarjeta),
                findViewById(R.id.imgPagoMetodoTarjetaChevron),
                findViewById(R.id.imgPagoMetodoTarjetaIcono),
                findViewById(R.id.linearLayoutPagoMetodoTarjetaTexto),
                "Tarjeta"
            ),
            Opcion(
                findViewById(R.id.cardPagoMetodoPse),
                findViewById(R.id.imgPagoMetodoPseChevron),
                findViewById(R.id.imgPagoMetodoPseIcono),
                findViewById(R.id.linearLayoutPagoMetodoPseTexto),
                "PSE"
            ),
            Opcion(
                findViewById(R.id.cardPagoMetodoBilleteras),
                findViewById(R.id.imgPagoMetodoBilleterasChevron),
                findViewById(R.id.imgPagoMetodoBilleterasIcono),
                findViewById(R.id.linearLayoutPagoMetodoBilleterasTexto),
                "Billeteras"
            ),
            Opcion(
                findViewById(R.id.cardPagoMetodoWompi),
                findViewById(R.id.imgPagoMetodoWompiChevron),
                findViewById(R.id.imgPagoMetodoWompiIcono),
                findViewById(R.id.linearLayoutPagoMetodoWompiTexto),
                "Wompi"
            )
        )

        val fondoSeleccionadoRes = R.drawable.bg_opcion_seleccionada
        val fondoNormalRes = R.drawable.bg_opcion_normal
        val circuloBlancoRes = R.drawable.bg_circulo_blanco
        val colorBlanco = ContextCompat.getColor(this, R.color.backgroundSecundario)
        val colorTitulo = ContextCompat.getColor(this, R.color.txtTitulo)
        val colorSub = ContextCompat.getColor(this, R.color.txtTextoSecundario)
        val colorSubSeleccion = ContextCompat.getColor(this, R.color.backgroundSecundario)

        fun marcarSeleccionado(seleccionada: Opcion) {
            metodoSeleccionado = seleccionada.nombre
            opciones.forEach { opcion ->
                val sel = opcion === seleccionada
                opcion.card.setBackground(ContextCompat.getDrawable(this, if (sel) fondoSeleccionadoRes else fondoNormalRes))
                opcion.chevron.visibility = if (sel) View.VISIBLE else View.INVISIBLE
                opcion.icono.setBackground(ContextCompat.getDrawable(this, circuloBlancoRes))

                val titulo = opcion.textos.getChildAt(0) as? TextView
                val sub = opcion.textos.getChildAt(1) as? TextView
                titulo?.setTextColor(if (sel) colorBlanco else colorTitulo)
                sub?.setTextColor(if (sel) colorSubSeleccion else colorSub)
            }
        }

        opciones.forEach { opcion ->
            opcion.card.setOnClickListener { marcarSeleccionado(opcion) }
        }

        findViewById<ImageView>(R.id.imgPagoMetodoAtras).setOnClickListener { finish() }

        val btnPagar = findViewById<MaterialButton>(R.id.btnPagoMetodoPagar)
        btnPagar.setOnClickListener {
            btnPagar.isEnabled = false
            procesarPago(btnPagar)
        }
    }

    private fun procesarPago(btnPagar: MaterialButton) {
        btnPagar.isEnabled = false

        val cupon = intent.getStringExtra(Checkout.EXTRA_CUPON).orEmpty()
        val cuponValido = cupon.equals(Checkout.CUPON_OFERTON10, ignoreCase = true)
        val subtotal = intent.getDoubleExtra(Checkout.EXTRA_SUBTOTAL, 1699000.0)
        val totales = Checkout.calcular(subtotal, cuponValido)
        val direccion = intent.getStringExtra(Checkout.EXTRA_DIRECCION).orEmpty().ifBlank { "Carrera 7 # 72-41, Bogotá D.C." }
        val pais = intent.getStringExtra(Checkout.EXTRA_PAIS).orEmpty().ifBlank { "Colombia" }
        val ordenId = "CN-${(10000..99999).random()}"
        val fecha = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale("es", "CO")).format(Date())

        startActivity(Intent(this, PagoConfirmacionActivity::class.java).apply {
            putExtra(Checkout.EXTRA_ORDEN_ID, ordenId)
            putExtra(Checkout.EXTRA_TOTAL, totales.total)
            putExtra(Checkout.EXTRA_DESCUENTO, totales.descuento)
            putExtra(Checkout.EXTRA_METODO, metodoSeleccionado)
            putExtra(Checkout.EXTRA_DIRECCION, direccion)
            putExtra(Checkout.EXTRA_PAIS, pais)
            putExtra(Checkout.EXTRA_CODIGO_POSTAL, intent.getStringExtra(Checkout.EXTRA_CODIGO_POSTAL).orEmpty().ifBlank { "110111" })
            putExtra(Checkout.EXTRA_CUPON, if (cuponValido) Checkout.CUPON_OFERTON10 else "")
            putExtra(Checkout.EXTRA_FECHA, fecha)
        })
        finish()
    }

    private fun restaurarBoton(btnPagar: MaterialButton) {
        btnPagar.isEnabled = true
        btnPagar.text = "Pagar"
    }
}
