package com.example.celnova.activities

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.celnova.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomnavigation.BottomNavigationView

class AdminUsuariosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_usuarios)

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

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationAdminUsers)
        bottomNav.itemIconTintList = navColorStateList
        bottomNav.itemTextColor = navColorStateList
        bottomNav.itemActiveIndicatorColor = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.navIndicadorFondo))
        bottomNav.selectedItemId = R.id.nav_admin_usuarios

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_admin_panel -> {
                    val intent = Intent(this, AdminDashboardActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_admin_usuarios -> true
                R.id.nav_admin_productos -> {
                    val intent = Intent(this, AdminProductosActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
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

        findViewById<com.google.android.material.card.MaterialCardView>(R.id.cardAdminUsersIconoPerfilContainer).setOnClickListener {
            startActivity(Intent(this, AdminPerfilActivity::class.java))
        }

        fun mostrarEditarUsuario(nombre: String, correo: String, telefono: String, ciudad: String) {
            val dialog = BottomSheetDialog(this)
            val view = layoutInflater.inflate(R.layout.bottom_sheet_editar_usuario, null)
            dialog.setContentView(view)

            val etNombre = view.findViewById<EditText>(R.id.etDialogEditarNombre)
            val etCorreo = view.findViewById<EditText>(R.id.etDialogEditarCorreo)
            val etTelefono = view.findViewById<EditText>(R.id.etDialogEditarTelefono)
            val etCiudad = view.findViewById<EditText>(R.id.etDialogEditarCiudad)

            etNombre.setText(nombre)
            etCorreo.setText(correo)
            etTelefono.setText(telefono)
            etCiudad.setText(ciudad)

            val btnCerrar = view.findViewById<ImageView>(R.id.imgDialogEditarCerrar)
            val btnCancelar = view.findViewById<Button>(R.id.btnDialogEditarCancelar)
            val btnGuardar = view.findViewById<Button>(R.id.btnDialogEditarGuardar)

            btnCerrar.setOnClickListener { dialog.dismiss() }
            btnCancelar.setOnClickListener { dialog.dismiss() }
            btnGuardar.setOnClickListener {
                Toast.makeText(this, "Cambios guardados para ${etNombre.text}", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }

            dialog.show()
        }

        findViewById<Button>(R.id.btnAdminUser1Editar).setOnClickListener {
            mostrarEditarUsuario("Carlos Mendoza", "carlos.mendoza@techstore.co", "+57 312 680 4421", "Bogotá D.C.")
        }

        findViewById<Button>(R.id.btnAdminUser2Editar).setOnClickListener {
            mostrarEditarUsuario("Valentine Gómez", "valen.gomez@gmail.com", "+57 300 456 7890", "Medellín")
        }

        findViewById<Button>(R.id.btnAdminUser3Editar).setOnClickListener {
            mostrarEditarUsuario("Santiago Restrepo", "santiago.soporte@celnova.co", "+57 315 789 0123", "Cali, Valle")
        }

        findViewById<Button>(R.id.btnAdminUser4Editar).setOnClickListener {
            mostrarEditarUsuario("Impor. Andina SAS", "contacto@andinastore.co", "+57 318 901 2345", "Barranquilla, Atlántico")
        }

        findViewById<Button>(R.id.btnAdminUsersNuevo).setOnClickListener {
            mostrarEditarUsuario("", "", "+57 ", "Bogotá D.C.")
        }
    }
}
