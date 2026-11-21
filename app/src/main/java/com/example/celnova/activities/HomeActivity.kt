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

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home)
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

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationHome)

        bottomNav.itemIconTintList = navColorStateList
        bottomNav.itemTextColor = navColorStateList
        bottomNav.itemActiveIndicatorColor = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.navIndicadorFondo))
        bottomNav.selectedItemId = R.id.nav_inicio

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_inicio -> true
                R.id.nav_categorias -> {
                    startActivity(Intent(this, CategoriasActivity::class.java))
                    @Suppress("DEPRECATION")
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_buscar -> {
                    startActivity(Intent(this, BuscarActivity::class.java))
                    @Suppress("DEPRECATION")
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_carrito -> {
                    startActivity(Intent(this, CarritoActivity::class.java))
                    @Suppress("DEPRECATION")
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_perfil -> {
                    startActivity(Intent(this, PerfilActivity::class.java))
                    @Suppress("DEPRECATION")
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                else -> false
            }
        }

        configurarProductos()
    }

    private fun configurarProductos() {
        ComprasHelper.abrirDetalle(
            this, findViewById(R.id.cardHomeOferta1), "home_oferta_1",
            R.id.txtHomeOferta1Titulo, R.id.txtHomeOferta1Precio, R.id.txtHomeOferta1PrecioOriginal,
            icono = "icono_telefono", categoria = "Smartphones", rating = "4.9 (1,290)"
        )
        ComprasHelper.abrirDetalle(
            this, findViewById(R.id.cardHomeOferta2), "home_oferta_2",
            R.id.txtHomeOferta2Titulo, R.id.txtHomeOferta2Precio, R.id.txtHomeOferta2PrecioOriginal,
            icono = "icono_audifono", categoria = "Audio & Hi-Fi", rating = "4.6 (890)"
        )

        ComprasHelper.abrirDetalle(
            this, findViewById(R.id.cardHomeMasVendidosItem1), "home_mas_vendido_1",
            R.id.txtHomeMasVendidosItem1Titulo, R.id.txtHomeMasVendidosItem1Precio,
            icono = "icono_laptop", categoria = "Tablets", rating = "4.8 (640)"
        )
        ComprasHelper.abrirDetalle(
            this, findViewById(R.id.cardHomeMasVendidosItem2), "home_mas_vendido_2",
            R.id.txtHomeMasVendidosItem2Titulo, R.id.txtHomeMasVendidosItem2Precio,
            icono = "icono_telefono", categoria = "Accesorios", rating = "4.8 (310)"
        )

        val nuevoDetalle = View.OnClickListener {
            ComprasHelper.abrirDetalle(
                this, findViewById(R.id.cardHomeNuevosCard), "home_nuevo_1",
                R.id.txtHomeNuevosCardTitulo, R.id.txtHomeNuevosPrecio,
                icono = "icono_laptop", categoria = "Laptops", rating = "4.9 (85)"
            )
        }
        findViewById<View>(R.id.cardHomeNuevosCard).setOnClickListener(nuevoDetalle)
        findViewById<View>(R.id.btnHomeNuevosEspecificaciones).setOnClickListener(nuevoDetalle)

        findViewById<View>(R.id.btnHomeOferta1Anadir).setOnClickListener {
            agregarDirecto("home_oferta_1", R.id.txtHomeOferta1Titulo, R.id.txtHomeOferta1Precio, "icono_telefono")
        }
        findViewById<View>(R.id.btnHomeOferta2Anadir).setOnClickListener {
            agregarDirecto("home_oferta_2", R.id.txtHomeOferta2Titulo, R.id.txtHomeOferta2Precio, "icono_audifono")
        }
        findViewById<View>(R.id.cardHomeMasVendidosItem1AddBtn).setOnClickListener {
            agregarDirecto("home_mas_vendido_1", R.id.txtHomeMasVendidosItem1Titulo, R.id.txtHomeMasVendidosItem1Precio, "icono_laptop")
        }
        findViewById<View>(R.id.cardHomeMasVendidosItem2AddBtn).setOnClickListener {
            agregarDirecto("home_mas_vendido_2", R.id.txtHomeMasVendidosItem2Titulo, R.id.txtHomeMasVendidosItem2Precio, "icono_telefono")
        }
    }

    private fun agregarDirecto(id: String, tituloId: Int, precioId: Int, icono: String) {
        val nombre = findViewById<TextView>(tituloId).text.toString().trim()
        val precio = findViewById<TextView>(precioId).text.toString().filter { it.isDigit() }.toDoubleOrNull() ?: 0.0
        ComprasHelper.agregarAlCarrito(this, id, nombre, precio, icono)
    }
}
