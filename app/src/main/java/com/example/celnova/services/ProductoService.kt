package com.example.celnova.services

import com.example.celnova.CelnovaFirebase
import com.example.celnova.models.Producto
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue

object ProductoService {
    const val COLECCION = "products"

    fun categorias(): List<String> =
        listOf("Otros", "Móviles", "Accesorios", "Laptops", "Balanzas", "Vehículos")

    fun listarActivos(onSuccess: (List<Producto>) -> Unit, onError: (Exception) -> Unit) {
        CelnovaFirebase.db.collection(COLECCION)
            .whereEqualTo("estado", "activo")
            .get()
            .addOnSuccessListener { snaps -> onSuccess(snaps.map { it.toProducto() }) }
            .addOnFailureListener { onError(it) }
    }

    fun buscar(termino: String, onSuccess: (List<Producto>) -> Unit, onError: (Exception) -> Unit) {
        val q = termino.trim().lowercase()
        CelnovaFirebase.db.collection(COLECCION).get()
            .addOnSuccessListener { snaps ->
                onSuccess(
                    snaps.map { it.toProducto() }.filter {
                        it.nombre.lowercase().contains(q) ||
                            it.descripcion.lowercase().contains(q)
                    }
                )
            }
            .addOnFailureListener { onError(it) }
    }

    fun getPorId(id: String, onSuccess: (Producto) -> Unit, onError: (Exception) -> Unit) {
        CelnovaFirebase.db.collection(COLECCION).document(id).get()
            .addOnSuccessListener { snap ->
                if (snap.exists()) onSuccess(snap.toProducto())
                else onError(NoSuchElementException("Producto no encontrado"))
            }
            .addOnFailureListener { onError(it) }
    }

    fun crear(
        producto: Producto,
        imagenBytes: ByteArray? = null,
        onSuccess: (String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val db = CelnovaFirebase.db
        val ref = db.collection(COLECCION).document()
        val docId = ref.id

        fun guardar(imagenUrl: String) {
            val datos = mapOf(
                "id" to docId,
                "nombre" to producto.nombre,
                "descripcion" to producto.descripcion,
                "precio" to producto.precio,
                "categoria" to producto.categoria,
                "stock" to producto.stock,
                "imagenUrl" to imagenUrl,
                "vendedor" to producto.vendedor,
                "estado" to "activo",
                "createdAt" to FieldValue.serverTimestamp()
            )
            ref.set(datos)
                .addOnSuccessListener { onSuccess(docId) }
                .addOnFailureListener { onError(it) }
        }

        if (imagenBytes != null && imagenBytes.isNotEmpty()) {
            StorageService.uploadBytes(StorageService.refProducto(docId), imagenBytes)
                .addOnSuccessListener { uri -> guardar(uri.toString()) }
                .addOnFailureListener { onError(it) }
        } else {
            guardar("")
        }
    }

    fun actualizar(producto: Producto, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        CelnovaFirebase.db.collection(COLECCION).document(producto.id).update(
            mapOf(
                "nombre" to producto.nombre,
                "descripcion" to producto.descripcion,
                "precio" to producto.precio,
                "categoria" to producto.categoria,
                "stock" to producto.stock,
                "estado" to producto.estado
            )
        )
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    fun eliminar(id: String, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        CelnovaFirebase.db.collection(COLECCION).document(id).delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    private fun DocumentSnapshot.toProducto(): Producto = Producto(
        id = id,
        nombre = getString("nombre") ?: "",
        descripcion = getString("descripcion") ?: "",
        precio = getDouble("precio") ?: 0.0,
        categoria = getString("categoria") ?: "Otros",
        stock = getLong("stock")?.toInt() ?: 0,
        imagenUrl = getString("imagenUrl") ?: "",
        vendedor = getString("vendedor") ?: "",
        estado = getString("estado") ?: "activo",
        createdAt = getTimestamp("createdAt")?.toDate()?.time ?: 0L
    )
}


