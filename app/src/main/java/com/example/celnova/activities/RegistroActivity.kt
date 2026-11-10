package com.example.celnova.activities

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import com.example.celnova.R
import com.example.celnova.utils.ValidadorNombre
import com.example.celnova.utils.ValidadorCorreo
import com.google.i18n.phonenumbers.PhoneNumberUtil

class RegistroActivity : AppCompatActivity() {

    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val txtIrLoginActivity = findViewById<TextView>(R.id.txtRegistroIniciaSesion)
        txtIrLoginActivity.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        val noSpaceFilter = android.text.InputFilter { source, start, end, _, _, _ ->
            for (i in start until end) {
                if (Character.isWhitespace(source[i])) {
                    return@InputFilter ""
                }
            }
            null
        }

        val etCorreo = findViewById<EditText>(R.id.etRegistroCorreo)
        etCorreo.filters = arrayOf(noSpaceFilter)

        val etPassword = findViewById<EditText>(R.id.etRegistroContrasena)
        etPassword.filters = arrayOf(noSpaceFilter)
        val btnToggle = findViewById<ImageView>(R.id.btnRegistroTogglePassword)

        btnToggle.setOnClickListener {
            if (isPasswordVisible) {
                etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                btnToggle.setImageResource(R.drawable.icono_vista_cerrada)
            } else {
                etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                btnToggle.setImageResource(R.drawable.icono_vista_abierta)
            }
            etPassword.setSelection(etPassword.text.length)
            isPasswordVisible = !isPasswordVisible
        }

        var isConfirmPasswordVisible = false
        val etConfirmPassword = findViewById<EditText>(R.id.etRegistroConfirmarContrasena)
        etConfirmPassword.filters = arrayOf(noSpaceFilter)
        val btnToggleConfirm = findViewById<ImageView>(R.id.btnRegistroTogglePasswordConfirmarContrasena)
        val imgCheckOrError = findViewById<ImageView>(R.id.imgRegistroCheckConfirmarContrasena)

        btnToggleConfirm.setOnClickListener {
            if (isConfirmPasswordVisible) {
                etConfirmPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                btnToggleConfirm.setImageResource(R.drawable.icono_vista_cerrada)
            } else {
                etConfirmPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                btnToggleConfirm.setImageResource(R.drawable.icono_vista_abierta)
            }
            etConfirmPassword.setSelection(etConfirmPassword.text.length)
            isConfirmPasswordVisible = !isConfirmPasswordVisible
        }
        
        val txtContrasenaEstado = findViewById<TextView>(R.id.txtRegistroContrasenaEstado)
        val txtConfirmarEstado = findViewById<TextView>(R.id.txtRegistroConfirmarContrasenaEstado)
        val txtPassHelper = findViewById<TextView>(R.id.txtRegistroPasswordHelper)
        val txtConfirmHelper = findViewById<TextView>(R.id.txtRegistroConfirmarPasswordHelper)

        imgCheckOrError.visibility = View.GONE
        txtContrasenaEstado.visibility = View.GONE
        txtConfirmarEstado.visibility = View.GONE
        txtPassHelper.visibility = View.GONE
        txtConfirmHelper.visibility = View.GONE

        fun evaluarFortaleza(password: String): Pair<String, Int> {
            if (password.isEmpty()) return Pair("", 0)
            val hasUpper = password.any { it.isUpperCase() }
            val hasLower = password.any { it.isLowerCase() }
            val hasDigit = password.any { it.isDigit() }
            val hasSpecial = password.any { !it.isLetterOrDigit() }
            val score = listOf(password.length >= 8, hasUpper, hasLower, hasDigit, hasSpecial).count { it }
            return when {
                score >= 5 -> Pair("Muy fuerte", ContextCompat.getColor(this, R.color.estadoExito))
                score == 4 -> Pair("Fuerte", ContextCompat.getColor(this, R.color.estadoInformacion))
                score == 3 -> Pair("Media", ContextCompat.getColor(this, R.color.estadoAdvertencia))
                else -> Pair("Débil", ContextCompat.getColor(this, R.color.estadoError))
            }
        }

        fun validarCoincidencia() {
            val pass = etPassword.text.toString()
            val confirm = etConfirmPassword.text.toString()

            if (confirm.isEmpty()) {
                imgCheckOrError.visibility = View.GONE
                txtConfirmarEstado.visibility = View.GONE
                txtConfirmHelper.visibility = View.GONE
            } else if (pass == confirm) {
                imgCheckOrError.visibility = View.VISIBLE
                imgCheckOrError.setImageResource(R.drawable.icono_check)
                imgCheckOrError.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.estadoExito))
                txtConfirmarEstado.text = "Correcto"
                txtConfirmarEstado.setTextColor(ContextCompat.getColor(this, R.color.estadoExito))
                txtConfirmarEstado.visibility = View.VISIBLE
                txtConfirmHelper.visibility = View.GONE
            } else {
                imgCheckOrError.visibility = View.VISIBLE
                imgCheckOrError.setImageResource(R.drawable.icono_error)
                imgCheckOrError.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.estadoError))
                txtConfirmarEstado.text = "No coincide"
                txtConfirmarEstado.setTextColor(ContextCompat.getColor(this, R.color.estadoError))
                txtConfirmarEstado.visibility = View.VISIBLE
                txtConfirmHelper.visibility = View.GONE
            }
        }

        val etCelular = findViewById<EditText>(R.id.etRegistroCelular)
        val etNombreCompleto = findViewById<EditText>(R.id.etRegistroNombreCompleto)
        val cbTerminos = findViewById<CheckBox>(R.id.checkboxRegistroTerminos)
        val btnRegistrarme = findViewById<Button>(R.id.btnRegistroRegistrarme)

        fun obtenerMensajeFaltante(password: String): String {
            val faltantes = mutableListOf<String>()

            if (password.length < 8) {
                val faltanCant = 8 - password.length
                faltantes.add(if (faltanCant == 1) "1 carácter más" else "$faltanCant caracteres más")
            }
            if (!password.any { it.isUpperCase() }) {
                faltantes.add("una letra mayúscula")
            }
            if (!password.any { it.isLowerCase() }) {
                faltantes.add("una letra minúscula")
            }
            if (!password.any { it.isDigit() }) {
                faltantes.add("un número")
            }
            if (!password.any { !it.isLetterOrDigit() }) {
                faltantes.add("un símbolo (#, *, -, etc.)")
            }

            if (faltantes.isEmpty()) return ""

            val textoUnido = when (faltantes.size) {
                1 -> faltantes[0]
                2 -> "${faltantes[0]} y ${faltantes[1]}"
                else -> faltantes.dropLast(1).joinToString(", ") + " y " + faltantes.last()
            }

            return "Te falta: $textoUnido."
        }

        fun esContrasenaValida(pass: String): Boolean {
            if (pass.length < 8) return false
            val hasUpper = pass.any { it.isUpperCase() }
            val hasLower = pass.any { it.isLowerCase() }
            val hasDigit = pass.any { it.isDigit() }
            val hasSpecial = pass.any { !it.isLetterOrDigit() }
            return hasUpper && hasLower && hasDigit && hasSpecial
        }

        fun validarFormularioCompleto(): Boolean {
            val nombre = etNombreCompleto.text.toString().trim()
            if (!ValidadorNombre.esNombreValido(nombre)) return false

            val correo = etCorreo.text.toString().trim().lowercase()
            if (!ValidadorCorreo.esCorreoValido(correo)) return false

            val celular = etCelular.text.toString()
            if (celular.length < 10) return false

            val pass = etPassword.text.toString()
            if (!esContrasenaValida(pass)) return false

            val confirm = etConfirmPassword.text.toString()
            if (confirm.isEmpty() || pass != confirm) return false

            if (!cbTerminos.isChecked) return false

            return true
        }

        fun actualizarEstadoBotonRegistrarme() {
            val esValido = validarFormularioCompleto()
            btnRegistrarme.alpha = if (esValido) 1.0f else 0.6f
        }

        etPassword.doAfterTextChanged { text ->
            val pass = text.toString()
            if (pass.isEmpty()) {
                txtContrasenaEstado.visibility = View.GONE
                txtPassHelper.visibility = View.GONE
            } else {
                val (estado, color) = evaluarFortaleza(pass)
                txtContrasenaEstado.text = estado
                txtContrasenaEstado.setTextColor(color)
                txtContrasenaEstado.visibility = View.VISIBLE

                if (esContrasenaValida(pass)) {
                    txtPassHelper.visibility = View.GONE
                } else {
                    txtPassHelper.text = "Debe tener mínimo 8 caracteres, mayúscula, minúscula, números y símbolo."
                    txtPassHelper.visibility = View.VISIBLE
                }
            }
            validarCoincidencia()
            actualizarEstadoBotonRegistrarme()
        }

        etConfirmPassword.doAfterTextChanged { _ ->
            validarCoincidencia()
            actualizarEstadoBotonRegistrarme()
        }

        actualizarEstadoBotonRegistrarme()

        etCelular.filters = arrayOf(
            android.text.InputFilter { source, _, _, _, dstart, _ ->
                if (dstart == 0 && source.isNotEmpty() && source[0] != '3') {
                    return@InputFilter ""
                }
                null
            },
            android.text.InputFilter.LengthFilter(10)
        )

        etNombreCompleto.doAfterTextChanged { s ->
            val textoOriginal = s?.toString() ?: ""
            if (textoOriginal.isNotEmpty()) {
                val textoCapitalizado = ValidadorNombre.capitalizar(textoOriginal)
                if (textoOriginal != textoCapitalizado) {
                    val posicionCursor = etNombreCompleto.selectionStart
                    etNombreCompleto.setText(textoCapitalizado)
                    val nuevaPosicionCursor = posicionCursor.coerceAtMost(textoCapitalizado.length)
                    etNombreCompleto.setSelection(nuevaPosicionCursor)
                }
            }
            actualizarEstadoBotonRegistrarme()
        }

        etCorreo.doAfterTextChanged { actualizarEstadoBotonRegistrarme() }
        etCelular.doAfterTextChanged { actualizarEstadoBotonRegistrarme() }
        cbTerminos.setOnCheckedChangeListener { _, _ -> actualizarEstadoBotonRegistrarme() }

        btnRegistrarme.setOnClickListener {
            val nombre = etNombreCompleto.text.toString().trim()
            if (nombre.isEmpty()) {
                etNombreCompleto.requestFocus()
                Toast.makeText(this, "Ingresa tu nombre y apellido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!ValidadorNombre.esNombreValido(nombre)) {
                etNombreCompleto.requestFocus()
                Toast.makeText(this, "Ingresa un nombre y un apellido válidos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val correo = etCorreo.text.toString().trim().lowercase()
            if (correo.isEmpty()) {
                etCorreo.requestFocus()
                Toast.makeText(this, "Ingresa tu correo electrónico", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!ValidadorCorreo.esCorreoValido(correo)) {
                etCorreo.requestFocus()
                Toast.makeText(this, "Ingresa un correo electrónico válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val celular = etCelular.text.toString()
            if (celular.isEmpty()) {
                etCelular.requestFocus()
                Toast.makeText(this, "Ingresa tu número de celular", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (celular.length < 10) {
                etCelular.requestFocus()
                Toast.makeText(this, "El celular debe tener 10 dígitos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                val phoneUtil = PhoneNumberUtil.getInstance()
                val numberProto = phoneUtil.parse(celular, "CO")
                val isValid = phoneUtil.isValidNumber(numberProto)

                if (!isValid) {
                    etCelular.requestFocus()
                    Toast.makeText(this, "Número de celular inválido", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            } catch (e: Exception) {
                etCelular.requestFocus()
                Toast.makeText(this, "Número de celular inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val pass = etPassword.text.toString()
            if (pass.isEmpty()) {
                etPassword.requestFocus()
                Toast.makeText(this, "Ingresa tu contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!esContrasenaValida(pass)) {
                val mensajeError = obtenerMensajeFaltante(pass)
                etPassword.requestFocus()
                Toast.makeText(this, mensajeError, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val confirm = etConfirmPassword.text.toString()
            if (confirm.isEmpty()) {
                etConfirmPassword.requestFocus()
                Toast.makeText(this, "Ingresa la confirmación de tu contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (pass != confirm) {
                etConfirmPassword.requestFocus()
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!cbTerminos.isChecked) {
                Toast.makeText(this, "Debes aceptar los Términos y Condiciones para continuar", Toast.LENGTH_LONG).show()
                cbTerminos.requestFocus()
                return@setOnClickListener
            }
        }
    }
}
