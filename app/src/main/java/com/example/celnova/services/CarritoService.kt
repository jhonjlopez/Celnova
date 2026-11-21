package com.example.celnova.services

import com.example.celnova.CelnovaFirebase
import com.example.celnova.models.CarritoItem
import com.google.firebase.firestore.DocumentSnapshot

object CarritoService {
    const val COLECCION = "carritos"
    private val memoryCache = mutableMapOf<String, MutableList<CarritoItem>>()

    fun listar(cliente: String, onSuccess: (List<CarritoItem>) -> Unit, onError: (Exception) -> Unit) {
        if (memoryCache.containsKey(cliente)) {
            onSuccess(memoryCache[cliente]!!)
        }

        CelnovaFirebase.db.collection(COLECCION).document(cliente).get()
            .addOnSuccessListener { snap ->
                val items = snap.toItems()
                memoryCache[cliente] = items.toMutableList()
                onSuccess(items)
            }
            .addOnFailureListener {
                if (!memoryCache.containsKey(cliente)) {
                    onError(it)
                }
            }
    }

    fun addItem(cliente: String, item: CarritoItem, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        val currentList = memoryCache.getOrPut(cliente) { mutableListOf() }
        val idx = currentList.indexOfFirst { it.productoId == item.productoId }
        if (idx >= 0) {
            val original = currentList[idx]
            currentList[idx] = original.copy(cantidad = original.cantidad + item.cantidad)
        } else {
            currentList.add(item)
        }
        onSuccess()

        guardarItems(cliente, currentList)
            .addOnSuccessListener {}
            .addOnFailureListener {}
    }

    fun actualizarCantidad(
        cliente: String,
        productoId: String,
        cantidad: Int,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val currentList = memoryCache.getOrPut(cliente) { mutableListOf() }
        val idx = currentList.indexOfFirst { it.productoId == productoId }
        if (idx >= 0) {
            currentList[idx] = currentList[idx].copy(cantidad = cantidad)
        }
        onSuccess()

        guardarItems(cliente, currentList)
            .addOnSuccessListener {}
            .addOnFailureListener {}
    }

    fun eliminarItem(cliente: String, productoId: String, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        val currentList = memoryCache.getOrPut(cliente) { mutableListOf() }
        val iterator = currentList.iterator()
        while (iterator.hasNext()) {
            if (iterator.next().productoId == productoId) {
                iterator.remove()
            }
        }
        onSuccess()

        guardarItems(cliente, currentList)
            .addOnSuccessListener {}
            .addOnFailureListener {}
    }

    fun vaciar(cliente: String, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        memoryCache[cliente] = mutableListOf()
        onSuccess()

        guardarItems(cliente, emptyList())
            .addOnSuccessListener {}
            .addOnFailureListener {}
    }

    private fun guardarItems(cliente: String, items: List<CarritoItem>) =
        CelnovaFirebase.db.collection(COLECCION).document(cliente).set(mapOf("items" to items))

    private fun DocumentSnapshot.toItems(): List<CarritoItem> {
        val raw = get("items") as? List<*> ?: return emptyList()
        return raw.mapNotNull { m ->
            (m as? Map<*, *>)?.let { map ->
                CarritoItem(
                    productoId = (map["productoId"] as? String) ?: "",
                    nombre = (map["nombre"] as? String) ?: "",
                    precio = (map["precio"] as? Number)?.toDouble() ?: 0.0,
                    cantidad = (map["cantidad"] as? Number)?.toInt() ?: 1,
                    imagenUrl = (map["imagenUrl"] as? String) ?: "",
                    icono = (map["icono"] as? String) ?: ""
                )
            }
        }
    }
}
