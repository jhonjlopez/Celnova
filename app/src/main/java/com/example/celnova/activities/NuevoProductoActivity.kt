package com.example.celnova.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.celnova.R
import com.example.celnova.models.Producto
import com.example.celnova.services.AuthService
import com.example.celnova.services.ProductoService
import java.io.File

class NuevoProductoActivity : AppCompatActivity() {

    private lateinit var imgFotoProducto: ImageView
    private lateinit var etNombre: EditText
    private lateinit var etDescripcion: EditText
    private lateinit var etPrecio: EditText
    private lateinit var etStock: EditText
    private lateinit var spnCategoria: Spinner
    private lateinit var btnTomarFoto: Button
    private lateinit var btnGaleria: Button
    private lateinit var btnGuardarProducto: Button

    private var imagenFile: File? = null
    private var imagenUri: Uri? = null

    private val permisoCamaraLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { otorgado ->
        if (otorgado) {
            tomarFoto()
        } else {
            Toast.makeText(this, "Necesitas el permiso de cámara para esta acción", Toast.LENGTH_SHORT).show()
        }
    }

    private val fotoLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { ok ->
        if (ok && imagenFile?.exists() == true) {
            Toast.makeText(this, "Foto capturada con éxito", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "No se pudo capturar la foto", Toast.LENGTH_SHORT).show()
        }
    }

    private val galeriaLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val uri = result.data?.data
        if (result.resultCode == Activity.RESULT_OK && uri != null) {
            imagenUri = uri
            imgFotoProducto.setImageURI(uri)
        } else {
            Toast.makeText(this, "No se seleccionó ninguna foto", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nuevo_producto)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        imgFotoProducto = findViewById(R.id.imgNuevoProductoFoto)
        etNombre = findViewById(R.id.etNuevoProductoNombre)
        etDescripcion = findViewById(R.id.etNuevoProductoDescripcion)
        etPrecio = findViewById(R.id.etNuevoProductoPrecio)
        etStock = findViewById(R.id.etNuevoProductoStock)
        spnCategoria = findViewById(R.id.spnNuevoProductoCategoria)
        btnTomarFoto = findViewById(R.id.btnNuevoProductoTomarFoto)
        btnGaleria = findViewById(R.id.btnNuevoProductoGaleria)
        btnGuardarProducto = findViewById(R.id.btnNuevoProductoGuardar)

        val categorias = ProductoService.categorias().toTypedArray()
        spnCategoria.adapter = android.widget.ArrayAdapter(this, android.R.layout.simple_spinner_item, categorias)
        spnCategoria.setSelection(0)

        findViewById<View>(R.id.imgNuevoProductoAtras).setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        btnTomarFoto.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                permisoCamaraLauncher.launch(android.Manifest.permission.CAMERA)
            } else {
                tomarFoto()
            }
        }

        btnGaleria.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galeriaLauncher.launch(intent)
        }

        btnGuardarProducto.setOnClickListener { guardarProducto() }
    }

    private fun tomarFoto() {
        imagenFile = File(cacheDir, "foto_producto_${System.currentTimeMillis()}.jpg")
        val uri = FileProvider.getUriForFile(this, "com.example.celnova.fileprovider", imagenFile!!)
        fotoLauncher.launch(uri)
    }

    private fun guardarProducto() {
        val nombre = etNombre.text.toString().trim()
        val descripcion = etDescripcion.text.toString().trim()
        val precioTexto = etPrecio.text.toString().trim()
        val stockTexto = etStock.text.toString().trim()

        if (nombre.isEmpty()) {
            etNombre.requestFocus()
            Toast.makeText(this, "Ingresa un nombre para el producto", Toast.LENGTH_SHORT).show()
            return
        }

        val precio = try {
            precioTexto.toDouble()
        } catch (e: NumberFormatException) {
            etPrecio.requestFocus()
            Toast.makeText(this, "Ingresa un precio válido", Toast.LENGTH_SHORT).show()
            return
        }

        val stock = try {
            stockTexto.toInt()
        } catch (e: NumberFormatException) {
            etStock.requestFocus()
            Toast.makeText(this, "Ingresa un stock válido", Toast.LENGTH_SHORT).show()
            return
        }

        val vendedor = AuthService.currentEmail() ?: ""
        val producto = Producto(
            nombre = nombre,
            descripcion = descripcion,
            precio = precio,
            categoria = spnCategoria.selectedItem?.toString() ?: "Otros",
            stock = stock,
            vendedor = vendedor,
            estado = "activo"
        )

        val bytes: ByteArray? = when {
            imagenFile?.exists() == true -> imagenFile!!.readBytes()
            imagenUri != null -> contentResolver.openInputStream(imagenUri!!)?.use { it.readBytes() }
            else -> null
        }

        ProductoService.crear(
            producto,
            bytes,
            onSuccess = { id ->
                Toast.makeText(this, "Producto '$nombre' creado (ID: $id)", Toast.LENGTH_SHORT).show()
                finish()
            },
            onError = { e ->
                Toast.makeText(this, "Error al crear el producto: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

