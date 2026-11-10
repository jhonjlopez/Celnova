package com.example.celnova.activities

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Paint
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.celnova.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class AdminProductosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_productos)

        val precioAnterior = findViewById<TextView>(R.id.txtAdminProducto2PrecioAnterior)
        precioAnterior.paintFlags = precioAnterior.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

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

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationAdminProductos)
        bottomNav.itemIconTintList = navColorStateList
        bottomNav.itemTextColor = navColorStateList
        bottomNav.itemActiveIndicatorColor = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.navIndicadorFondo))
        bottomNav.selectedItemId = R.id.nav_admin_productos

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_admin_panel -> {
                    val intent = Intent(this, AdminDashboardActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_admin_usuarios -> {
                    val intent = Intent(this, AdminUsuariosActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_admin_productos -> true
                R.id.nav_admin_ajustes -> {
                    val intent = Intent(this, AdminAjustesActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_admin_perfil -> {
                    startActivity(Intent(this, AdminPerfilActivity::class.java))
                    true
                }
                else -> false
            }
        }

        findViewById<com.google.android.material.card.MaterialCardView>(R.id.cardAdminProductosIconoPerfilContainer).setOnClickListener {
            startActivity(Intent(this, AdminPerfilActivity::class.java))
        }
    }
}
