package com.example.celnova.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.celnova.R
import com.example.celnova.utils.Checkout
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Locale
import java.util.concurrent.Executors

class PagoEnvioActivity : AppCompatActivity() {

    private lateinit var etPagoEnvioPais: EditText
    private lateinit var etPagoEnvioDireccion: EditText
    private lateinit var etPagoEnvioCodigoPostal: EditText
    private lateinit var btnUbicacionActual: View

    private val executor = Executors.newSingleThreadExecutor()

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    companion object {
        private const val CODIGO_PERMISO_UBICACION = 201
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pago_envio)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etPagoEnvioPais = findViewById(R.id.etPagoEnvioPais)
        etPagoEnvioDireccion = findViewById(R.id.etPagoEnvioDireccion)
        etPagoEnvioCodigoPostal = findViewById(R.id.etPagoEnvioCodigoPostal)
        btnUbicacionActual = findViewById(R.id.btnPagoEnvioUbicacionActual)

        findViewById<ImageView>(R.id.imgPagoEnvioAtras).setOnClickListener { finish() }

        btnUbicacionActual.setOnClickListener { obtenerUbicacionActual() }

        findViewById<Button>(R.id.btnPagoEnvioContinuar).setOnClickListener {
            val pais = etPagoEnvioPais.text.toString().trim()
            val direccion = etPagoEnvioDireccion.text.toString().trim()
            val codigoPostal = etPagoEnvioCodigoPostal.text.toString().trim()

            if (pais.isBlank() || direccion.isBlank() || codigoPostal.isBlank()) {
                Toast.makeText(this, "Completa país, dirección y código postal", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            startActivity(Intent(this, PagoMetodoActivity::class.java).apply {
                putExtra(Checkout.EXTRA_SUBTOTAL, intent.getDoubleExtra(Checkout.EXTRA_SUBTOTAL, 0.0))
                putExtra(Checkout.EXTRA_DESCUENTO, intent.getDoubleExtra(Checkout.EXTRA_DESCUENTO, 0.0))
                putExtra(Checkout.EXTRA_TOTAL, intent.getDoubleExtra(Checkout.EXTRA_TOTAL, 0.0))
                putExtra(Checkout.EXTRA_CUPON, intent.getStringExtra(Checkout.EXTRA_CUPON).orEmpty())
                putExtra(Checkout.EXTRA_PAIS, pais)
                putExtra(Checkout.EXTRA_DIRECCION, direccion)
                putExtra(Checkout.EXTRA_CODIGO_POSTAL, codigoPostal)
            })
        }
    }

    private fun obtenerUbicacionActual() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                CODIGO_PERMISO_UBICACION
            )
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { ubicacion ->
                if (ubicacion == null) {
                    Toast.makeText(this, "Ubicación no disponible. Activa los servicios de ubicación.", Toast.LENGTH_SHORT).show()
                } else {
                    llenarDireccionDesdeUbicacion(ubicacion.latitude, ubicacion.longitude)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al obtener la ubicación", Toast.LENGTH_SHORT).show()
            }
    }

    private fun llenarDireccionDesdeUbicacion(latitud: Double, longitud: Double) {
        executor.execute {
            val geocoder = Geocoder(this, Locale.getDefault())
            try {
                val listaDirecciones = geocoder.getFromLocation(latitud, longitud, 1)
                runOnUiThread {
                    if (!listaDirecciones.isNullOrEmpty()) {
                        val direccion = listaDirecciones[0]
                        val lineas = mutableListOf<String>()
                        var indice = 0
                        while (true) {
                            val linea = direccion.getAddressLine(indice) ?: break
                            lineas.add(linea)
                            indice++
                        }
                        etPagoEnvioDireccion.setText(lineas.joinToString(", "))
                        etPagoEnvioPais.setText(direccion.adminArea ?: direccion.countryName ?: "")
                        etPagoEnvioCodigoPostal.setText(direccion.postalCode ?: "")
                        Toast.makeText(this, "Dirección asignada desde tu ubicación: ${lineas.firstOrNull() ?: ""}", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "No se pudo obtener la dirección exacta", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread { Toast.makeText(this, "El geocodificador no está disponible", Toast.LENGTH_SHORT).show() }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CODIGO_PERMISO_UBICACION && grantResults.any { it == PackageManager.PERMISSION_GRANTED }) {
            obtenerUbicacionActual()
        }
    }
}

