package com.example.celnova.activities

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import com.example.celnova.R
import com.example.celnova.utils.ValidadorCorreo

class LoginActivity : AppCompatActivity() {

    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etEmail = findViewById<EditText>(R.id.etLoginCorreo)
        val etPassword = findViewById<EditText>(R.id.etLoginContrasena)
        val btnToggle = findViewById<ImageView>(R.id.btnLoginTogglePassword)

        etEmail.setText("jhonlopez@gmail.com")
        etPassword.setText("Prueba123*")

        val noSpaceFilter = android.text.InputFilter { source, start, end, _, _, _ ->
            for (i in start until end) {
                if (Character.isWhitespace(source[i])) {
                    return@InputFilter ""
                }
            }
            null
        }

        etEmail.filters = arrayOf(noSpaceFilter)
        etPassword.filters = arrayOf(noSpaceFilter)

        val txtIrRecuperarContrasenaActivity = findViewById<TextView>(R.id.txtLoginOlvideContrasena)
        txtIrRecuperarContrasenaActivity.setOnClickListener {
            val intent = Intent(this, RecuperarContrasenaActivity::class.java)
            startActivity(intent)
        }

        val txtIrRegistroActivity = findViewById<TextView>(R.id.txtLoginRegistro)
        txtIrRegistroActivity.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }

        btnToggle.setOnClickListener {
            if (isPasswordVisible) {
                etPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or
                            InputType.TYPE_TEXT_VARIATION_PASSWORD
                btnToggle.setImageResource(R.drawable.icono_vista_cerrada)
            } else {
                etPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or
                            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                btnToggle.setImageResource(R.drawable.icono_vista_abierta)
            }

            etPassword.setSelection(etPassword.text.length)
            isPasswordVisible = !isPasswordVisible
        }

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

        val btnLogin = findViewById<Button>(R.id.btnLoginIniciarSesion)

        fun validarFormularioLogin(): Boolean {
            val correo = etEmail.text.toString().trim().lowercase()
            if (!ValidadorCorreo.esCorreoValido(correo)) return false

            val password = etPassword.text.toString()
            if (!esContrasenaValida(password)) return false

            return true
        }

        fun actualizarEstadoBotonLogin() {
            val esValido = validarFormularioLogin()
            btnLogin.alpha = if (esValido) 1.0f else 0.6f
        }

        etEmail.doAfterTextChanged { actualizarEstadoBotonLogin() }
        etPassword.doAfterTextChanged { actualizarEstadoBotonLogin() }

        actualizarEstadoBotonLogin()

        btnLogin.setOnClickListener {
            val correo = etEmail.text.toString().trim().lowercase()
            val password = etPassword.text.toString()

            if (correo.isEmpty()) {
                etEmail.requestFocus()
                Toast.makeText(this, "Ingresa tu correo electrónico", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!ValidadorCorreo.esCorreoValido(correo)) {
                etEmail.requestFocus()
                Toast.makeText(this, "Ingresa un correo electrónico válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                etPassword.requestFocus()
                Toast.makeText(this, "Ingresa tu contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!esContrasenaValida(password)) {
                val mensajeError = obtenerMensajeFaltante(password)
                etPassword.requestFocus()
                Toast.makeText(this, mensajeError, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
            val isAdmin = correo.equals("jhonlopezadmin@gmail.com", ignoreCase = true) || correo.startsWith("jhonlopezadmin", ignoreCase = true)
            val targetClass = if (isAdmin) AdminDashboardActivity::class.java else HomeActivity::class.java
            val intent = Intent(this, targetClass)
            startActivity(intent)
            finish()
        }

        val btnHuella = findViewById<com.google.android.material.card.MaterialCardView>(R.id.btnLoginHuella)
        btnHuella.setOnClickListener {
            val biometricManager = BiometricManager.from(this)
            val authStatus = biometricManager.canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK
            )

            when (authStatus) {
                BiometricManager.BIOMETRIC_SUCCESS -> {
                    val executor = ContextCompat.getMainExecutor(this)
                    val biometricPrompt = BiometricPrompt(this, executor,
                        object : BiometricPrompt.AuthenticationCallback() {
                            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                                super.onAuthenticationError(errorCode, errString)
                                Toast.makeText(applicationContext, "Error de autenticación: $errString", Toast.LENGTH_SHORT).show()
                            }

                            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                                super.onAuthenticationSucceeded(result)
                                Toast.makeText(applicationContext, "¡Inicio de sesión con huella exitoso!", Toast.LENGTH_SHORT).show()
                                val correoActual = etEmail.text.toString().trim().lowercase()
                                val isAdmin = correoActual.equals("jhonlopezadmin@gmail.com", ignoreCase = true) || correoActual.startsWith("jhonlopezadmin", ignoreCase = true)
                                val targetClass = if (isAdmin) AdminDashboardActivity::class.java else HomeActivity::class.java
                                val intent = Intent(this@LoginActivity, targetClass)
                                startActivity(intent)
                                finish()
                            }

                            override fun onAuthenticationFailed() {
                                super.onAuthenticationFailed()
                                Toast.makeText(applicationContext, "Huella no reconocida. Inténtalo de nuevo.", Toast.LENGTH_SHORT).show()
                            }
                        })

                    val promptInfo = BiometricPrompt.PromptInfo.Builder()
                        .setTitle("Autenticación biométrica")
                        .setSubtitle("Usa tu huella para iniciar sesión como jhonlopez@gmail.com")
                        .setNegativeButtonText("Cancelar")
                        .build()

                    biometricPrompt.authenticate(promptInfo)
                }
                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                    val intent = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                        Intent(android.provider.Settings.ACTION_BIOMETRIC_ENROLL).apply {
                            putExtra(
                                android.provider.Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK
                            )
                        }
                    } else {
                        Intent(android.provider.Settings.ACTION_SECURITY_SETTINGS)
                    }

                    android.app.AlertDialog.Builder(this)
                        .setTitle("Sin huella digital registrada")
                        .setMessage("No tienes ninguna huella dactilar registrada en este dispositivo. ¿Deseas registrar una ahora en la configuración del sistema?")
                        .setPositiveButton("Sí, registrar") { _, _ ->
                            try {
                                startActivity(intent)
                            } catch (e: Exception) {
                                startActivity(Intent(android.provider.Settings.ACTION_SECURITY_SETTINGS))
                            }
                        }
                        .setNegativeButton("Cancelar", null)
                        .show()
                }
                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                    Toast.makeText(this, "Este dispositivo no cuenta con sensor biométrico", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(this, "El sensor biométrico no está disponible actualmente", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
