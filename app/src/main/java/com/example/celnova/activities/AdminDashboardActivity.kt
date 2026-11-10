package com.example.celnova.activities

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.celnova.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class AdminDashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

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

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationAdmin)
        bottomNav.itemIconTintList = navColorStateList
        bottomNav.itemTextColor = navColorStateList
        bottomNav.itemActiveIndicatorColor = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.navIndicadorFondo))
        bottomNav.selectedItemId = R.id.nav_admin_panel

        fun abrirUsuarios() {
            val intent = Intent(this, AdminUsuariosActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
        }

        fun abrirProductos() {
            val intent = Intent(this, AdminProductosActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
        }

        fun abrirAjustes() {
            val intent = Intent(this, AdminAjustesActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
        }

        findViewById<com.google.android.material.card.MaterialCardView>(R.id.cardAdminIconoPerfilContainer)?.setOnClickListener {
            startActivity(Intent(this, AdminPerfilActivity::class.java))
        }

        findViewById<com.google.android.material.card.MaterialCardView>(R.id.cardAdminKpi1)?.setOnClickListener {
            abrirUsuarios()
        }

        findViewById<com.google.android.material.card.MaterialCardView>(R.id.cardAdminAccion2)?.setOnClickListener {
            abrirUsuarios()
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_admin_panel -> true
                R.id.nav_admin_usuarios -> {
                    abrirUsuarios()
                    true
                }
                R.id.nav_admin_productos -> {
                    abrirProductos()
                    true
                }
                R.id.nav_admin_ajustes -> {
                    abrirAjustes()
                    true
                }
                R.id.nav_admin_perfil -> {
                    startActivity(Intent(this, AdminPerfilActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
}
