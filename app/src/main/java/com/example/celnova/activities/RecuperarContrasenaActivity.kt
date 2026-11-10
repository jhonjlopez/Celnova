package com.example.celnova.activities

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import com.example.celnova.R
import com.example.celnova.utils.ValidadorCorreo
import com.google.android.material.card.MaterialCardView
import com.google.i18n.phonenumbers.PhoneNumberUtil

class RecuperarContrasenaActivity : AppCompatActivity() {

    private var esModoSms = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_recuperar_contrasena)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val txtIrloginActivity1 = findViewById<TextView>(R.id.txtRecuperarContrasenaIniciaSesion)
        txtIrloginActivity1.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        val imgLogo = findViewById<ImageView>(R.id.imgRecuperarContrasenaLogo)
        imgLogo.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        val layoutAtras = findViewById<LinearLayout>(R.id.linearLayoutRecuperarContrasenaAtrasIconoTitulo)
        val imgAtras = findViewById<ImageView>(R.id.imgRecuperarContrasenaIconoAtras)
        val txtAtras = findViewById<TextView>(R.id.txtRecuperarContrasenaAtras)

        val accionAtras = View.OnClickListener { finish() }
        layoutAtras.setOnClickListener(accionAtras)
        imgAtras.setOnClickListener(accionAtras)
        txtAtras.setOnClickListener(accionAtras)

        val cardBotonCorreo = findViewById<MaterialCardView>(R.id.cardRecuperarContrasenaCorreo)
        val cardBotonSms = findViewById<MaterialCardView>(R.id.cardRecuperarContrasenaSms)
        val txtEtiquetaCampo = findViewById<TextView>(R.id.txtRecuperarLabelCorreo)
        val etCampoTexto = findViewById<EditText>(R.id.etRecuperarContrasenaCorreo)
        val imgIconoCampo = findViewById<ImageView>(R.id.imgRecuperarContrasenaCorreoIconoRegistrado)
        val txtInstruccionesAbajo = findViewById<TextView>(R.id.txtRecuperarContrasenaInstrucciones)
        val btnRestablecer = findViewById<Button>(R.id.btnRecuperarContrasenaRestablecer)

        val txtTabCorreo = findViewById<TextView>(R.id.txtRecuperarContrasenaCorreo)
        val imgTabCorreoIcono = findViewById<ImageView>(R.id.imgRecuperarContrasenaCorreoIcono)
        val txtTabSms = findViewById<TextView>(R.id.txtRecuperarContrasenaSms)
        val imgTabSmsIcono = findViewById<ImageView>(R.id.imgRecuperarContrasenaSmsIcono)

        val colorActivo = ContextCompat.getColor(this, R.color.brandSecondario)
        val colorInactivoIcono = ContextCompat.getColor(this, R.color.iconSecundario)
        val colorInactivoTexto = ContextCompat.getColor(this, R.color.txtTexto)
        val colorTituloTexto = ContextCompat.getColor(this, R.color.txtTitulo)

        val noSpaceFilter = android.text.InputFilter { source, start, end, _, _, _ ->
            for (i in start until end) {
                if (Character.isWhitespace(source[i])) {
                    return@InputFilter ""
                }
            }
            null
        }

        val smsFirstDigitFilter = android.text.InputFilter { source, start, end, dest, dstart, dend ->
            if (dstart == 0 && source.isNotEmpty() && source[0] != '3') {
                return@InputFilter ""
            }
            null
        }

        val smsFilters = arrayOf(smsFirstDigitFilter, noSpaceFilter, android.text.InputFilter.LengthFilter(10))
        val correoFilters = arrayOf(noSpaceFilter, android.text.InputFilter.LengthFilter(254))

        etCampoTexto.filters = correoFilters

        fun validarFormularioRecuperar(): Boolean {
            val input = etCampoTexto.text.toString().trim()
            if (input.isEmpty()) return false
            if (esModoSms) {
                if (input.length < 10) return false
            } else {
                if (!ValidadorCorreo.esCorreoValido(input.lowercase())) return false
            }
            return true
        }

        fun actualizarEstadoBotonRestablecer() {
            val esValido = validarFormularioRecuperar()
            btnRestablecer.alpha = if (esValido) 1.0f else 0.6f
        }

        etCampoTexto.doAfterTextChanged { actualizarEstadoBotonRestablecer() }
        actualizarEstadoBotonRestablecer()

        cardBotonSms.setOnClickListener {
            esModoSms = true
            etCampoTexto.setText("")
            etCampoTexto.filters = smsFilters

            txtEtiquetaCampo.text = "Número de celular registrado"
            etCampoTexto.hint = "300 123 4567"
            etCampoTexto.inputType = InputType.TYPE_CLASS_NUMBER
            imgIconoCampo.setImageResource(R.drawable.icono_telefono)
            txtInstruccionesAbajo.text = "Te llegará un enlace de seguridad por SMS para restablecer la contraseña."

            cardBotonSms.setCardBackgroundColor(ContextCompat.getColor(this, R.color.backgroundCardSecundario))
            txtTabSms.setTextColor(colorTituloTexto)
            imgTabSmsIcono.imageTintList = ColorStateList.valueOf(colorActivo)

            cardBotonCorreo.setCardBackgroundColor(Color.TRANSPARENT)
            txtTabCorreo.setTextColor(colorInactivoTexto)
            imgTabCorreoIcono.imageTintList = ColorStateList.valueOf(colorInactivoIcono)
            actualizarEstadoBotonRestablecer()
        }

        cardBotonCorreo.setOnClickListener {
            esModoSms = false
            etCampoTexto.setText("")
            etCampoTexto.filters = correoFilters

            txtEtiquetaCampo.text = "Correo electrónico registrado"
            etCampoTexto.hint = "ejemplo@gmail.com"
            etCampoTexto.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            imgIconoCampo.setImageResource(R.drawable.icono_email)
            txtInstruccionesAbajo.text = "Te llegará un enlace de seguridad para restablecer la contraseña."

            cardBotonCorreo.setCardBackgroundColor(ContextCompat.getColor(this, R.color.backgroundCardSecundario))
            txtTabCorreo.setTextColor(colorTituloTexto)
            imgTabCorreoIcono.imageTintList = ColorStateList.valueOf(colorActivo)

            cardBotonSms.setCardBackgroundColor(Color.TRANSPARENT)
            txtTabSms.setTextColor(colorInactivoTexto)
            imgTabSmsIcono.imageTintList = ColorStateList.valueOf(colorInactivoIcono)
            actualizarEstadoBotonRestablecer()
        }

        btnRestablecer.setOnClickListener {
            val input = etCampoTexto.text.toString().trim()

            if (esModoSms) {
                if (input.isEmpty()) {
                    etCampoTexto.requestFocus()
                    Toast.makeText(this, "Ingresa tu número de celular", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (input.length < 10) {
                    etCampoTexto.requestFocus()
                    Toast.makeText(this, "El celular debe tener 10 dígitos", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                try {
                    val phoneUtil = PhoneNumberUtil.getInstance()
                    val numberProto = phoneUtil.parse(input, "CO")
                    val isValid = phoneUtil.isValidNumber(numberProto)

                    if (!isValid) {
                        etCampoTexto.requestFocus()
                        Toast.makeText(this, "Número de celular inválido", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                } catch (e: Exception) {
                    etCampoTexto.requestFocus()
                    Toast.makeText(this, "Número de celular inválido", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            } else {
                val correo = input.lowercase()
                if (correo.isEmpty()) {
                    etCampoTexto.requestFocus()
                    Toast.makeText(this, "Ingresa tu correo electrónico", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (!ValidadorCorreo.esCorreoValido(correo)) {
                    etCampoTexto.requestFocus()
                    Toast.makeText(this, "Ingresa un correo electrónico válido", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

        }

    }
}
