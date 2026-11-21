package com.example.celnova.activities

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.celnova.R
import com.example.celnova.utils.ComprasHelper
import com.google.android.material.bottomnavigation.BottomNavigationView

class BuscarActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buscar)

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

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationBuscar)
        bottomNav.itemIconTintList = navColorStateList
        bottomNav.itemTextColor = navColorStateList
        bottomNav.itemActiveIndicatorColor = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.navIndicadorFondo))
        bottomNav.selectedItemId = R.id.nav_buscar

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_inicio -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_categorias -> {
                    startActivity(Intent(this, CategoriasActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_buscar -> true
                R.id.nav_carrito -> {
                    startActivity(Intent(this, CarritoActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_perfil -> {
                    startActivity(Intent(this, PerfilActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }

        configurarSugeridos()
    }

    private fun configurarSugeridos() {
        val productos = listOf(
            Sugerido(
                R.id.cardBuscarSugerido1, R.id.btnBuscarSugerido1Add,
                R.id.txtBuscarSugerido1Titulo, R.id.txtBuscarSugerido1Precio,
                R.id.txtBuscarSugerido1PrecioOriginal, "buscar_sugerido_1", "icono_telefono",
                "Smartphones", "4.9 (1,290)"
            ),
            Sugerido(
                R.id.cardBuscarSugerido2, R.id.btnBuscarSugerido2Add,
                R.id.txtBuscarSugerido2Titulo, R.id.txtBuscarSugerido2Precio,
                R.id.txtBuscarSugerido2PrecioOriginal, "buscar_sugerido_2", "icono_audifono",
                "Audio & Hi-Fi", "4.6 (890)"
            ),
            Sugerido(
                R.id.cardBuscarSugerido3, R.id.btnBuscarSugerido3Add,
                R.id.txtBuscarSugerido3Titulo, R.id.txtBuscarSugerido3Precio,
                R.id.txtBuscarSugerido3PrecioOriginal, "buscar_sugerido_3", "icono_categorias",
                "Accesorios", "4.8 (210)"
            ),
            Sugerido(
                R.id.cardBuscarSugerido4, R.id.btnBuscarSugerido4Add,
                R.id.txtBuscarSugerido4Titulo, R.id.txtBuscarSugerido4Precio,
                R.id.txtBuscarSugerido4PrecioOriginal, "buscar_sugerido_4", "icono_laptop",
                "Laptops", "4.9 (85)"
            )
        )

        productos.forEach { p ->
            ComprasHelper.abrirDetalle(
                this, findViewById(p.tarjeta), p.id,
                p.titulo, p.precio, p.precioOriginal,
                icono = p.icono, categoria = p.categoria, rating = p.rating
            )
            findViewById<View>(p.botonAnadir).setOnClickListener {
                val nombre = findViewById<TextView>(p.titulo).text.toString().trim()
                val precio = findViewById<TextView>(p.precio).text.toString().filter { it.isDigit() }.toDoubleOrNull() ?: 0.0
                ComprasHelper.agregarAlCarrito(this, p.id, nombre, precio, p.icono)
            }
        }
    }

    private data class Sugerido(
        val tarjeta: Int,
        val botonAnadir: Int,
        val titulo: Int,
        val precio: Int,
        val precioOriginal: Int,
        val id: String,
        val icono: String,
        val categoria: String,
        val rating: String
    )
}
