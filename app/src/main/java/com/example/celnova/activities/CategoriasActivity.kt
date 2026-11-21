package com.example.celnova.activities

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.celnova.R
import com.example.celnova.utils.ComprasHelper
import com.google.android.material.bottomnavigation.BottomNavigationView

class CategoriasActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categorias)

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

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationCategorias)
        bottomNav.itemIconTintList = navColorStateList
        bottomNav.itemTextColor = navColorStateList
        bottomNav.itemActiveIndicatorColor = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.navIndicadorFondo))
        bottomNav.selectedItemId = R.id.nav_categorias

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_inicio -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_categorias -> true
                R.id.nav_buscar -> {
                    startActivity(Intent(this, BuscarActivity::class.java))
                    finish()
                    true
                }
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

        ComprasHelper.abrirDetalle(
            this, findViewById(R.id.cardCategoriasTendencia1), "categoria_tendencia_1",
            R.id.txtCategoriasTendencia1Titulo, R.id.txtCategoriasTendencia1Precio,
            R.id.txtCategoriasTendencia1PrecioOriginal,
            icono = "icono_telefono", categoria = "Smartphones", rating = "4.9 (1,290)"
        )
        ComprasHelper.abrirDetalle(
            this, findViewById(R.id.cardCategoriasTendencia2), "categoria_tendencia_2",
            R.id.txtCategoriasTendencia2Titulo, R.id.txtCategoriasTendencia2Precio,
            R.id.txtCategoriasTendencia2PrecioOriginal,
            icono = "icono_audifono", categoria = "Audio & Hi-Fi", rating = "4.6 (890)"
        )
    }
}

