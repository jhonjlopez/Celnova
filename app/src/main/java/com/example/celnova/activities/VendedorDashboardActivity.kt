package com.example.celnova.activities

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.celnova.R
import com.example.celnova.services.AuthService
import com.google.android.material.bottomnavigation.BottomNavigationView

class VendedorDashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vendedor_dashboard)

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

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationVendedor)
        bottomNav.itemIconTintList = navColorStateList
        bottomNav.itemTextColor = navColorStateList
        bottomNav.itemActiveIndicatorColor = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.navIndicadorFondo))
        bottomNav.selectedItemId = R.id.nav_vendedor_ventas

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_vendedor_ventas -> true
                R.id.nav_vendedor_catalogo -> {
                    val intent = Intent(this, VendedorCatalogoActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_vendedor_pedidos -> {
                    val intent = Intent(this, VendedorPedidosActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_vendedor_ajustes -> true
                else -> false
            }
        }

        findViewById<com.google.android.material.button.MaterialButton>(R.id.btnVendedorCerrarSesion).setOnClickListener {
            AuthService.logout()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}




