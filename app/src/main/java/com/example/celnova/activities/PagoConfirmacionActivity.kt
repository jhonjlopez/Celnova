package com.example.celnova.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.celnova.R
import com.example.celnova.utils.Checkout
import com.example.celnova.utils.Formato

class PagoConfirmacionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pago_confirmacion)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val ordenId = intent.getStringExtra(Checkout.EXTRA_ORDEN_ID).orEmpty()
        val total = intent.getDoubleExtra(Checkout.EXTRA_TOTAL, 0.0)
        val descuento = intent.getDoubleExtra(Checkout.EXTRA_DESCUENTO, 0.0)
        val cupon = intent.getStringExtra(Checkout.EXTRA_CUPON).orEmpty()
        val direccion = intent.getStringExtra(Checkout.EXTRA_DIRECCION).orEmpty()
        val pais = intent.getStringExtra(Checkout.EXTRA_PAIS).orEmpty()
        val codigoPostal = intent.getStringExtra(Checkout.EXTRA_CODIGO_POSTAL).orEmpty()
        val fecha = intent.getStringExtra(Checkout.EXTRA_FECHA).orEmpty()

        val metodo = intent.getStringExtra(Checkout.EXTRA_METODO)
            ?: intent.getStringExtra("metodoPago")
            ?: "Tarjeta"
        val metodoTexto = when (metodo) {
            "PSE" -> "Bancolombia PSE"
            "Billeteras" -> "Nequi / Daviplata"
            "Wompi" -> "Wompi"
            else -> "Tarjeta de Crédito"
        }
        findViewById<TextView>(R.id.txtPagoConfirmacionMetodo).text = metodoTexto

        val numeroOrden = "#CN-${ordenId.take(6).uppercase().ifBlank { "90458" }}"
        findViewById<TextView>(R.id.txtPagoConfirmacionCn90458).text = numeroOrden
        findViewById<TextView>(R.id.txtPagoConfirmacionCus88392019482).text =
            "CUS-${ordenId.filter { it.isDigit() }.take(11).ifBlank { "88392019482" }}"
        findViewById<TextView>(R.id.txtPagoConfirmacionTotal).text = Formato.cop(total)
        findViewById<TextView>(R.id.txtPagoConfirmacionTuOrdenCn90458HaSidoProcesadaExitosament).text =
            "Tu orden $numeroOrden ha sido procesada exitosamente con factura electrónica DIAN."
        findViewById<TextView>(R.id.txtPagoConfirmacionCalle721034Apto502).text =
            direccion.ifBlank { "Dirección registrada" }
        findViewById<TextView>(R.id.txtPagoConfirmacionBogotaDCColombia).text =
            listOf(pais, codigoPostal).filter { it.isNotBlank() }.joinToString(" • ").ifBlank { "Colombia" }
        findViewById<TextView>(R.id.txtPagoConfirmacionHoy0342Pm).text = fecha.ifBlank { "Hoy" }

        if (cupon.equals(Checkout.CUPON_OFERTON10, ignoreCase = true) && descuento > 0.0) {
            findViewById<TextView>(R.id.txtPagoConfirmacionFacturaElectraNicaFe4895RemitidaATuCorre).text =
                "Factura Electrónica $numeroOrden remitida a tu correo. Cupón oferton10: " +
                    "ahorraste ${Formato.cop(descuento)}."
        }

        findViewById<ImageView>(R.id.imgPagoConfirmacionAtras).setOnClickListener { irAInicio() }
        findViewById<Button>(R.id.btnPagoConfirmacionAceptar).setOnClickListener { irAInicio() }
        findViewById<Button>(R.id.btnPagoConfirmacionDescargar).setOnClickListener {
            Toast.makeText(this, "Comprobante de la orden $numeroOrden", Toast.LENGTH_SHORT).show()
        }
    }

    private fun irAInicio() {
        startActivity(Intent(this, HomeActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        })
        finish()
    }
}
