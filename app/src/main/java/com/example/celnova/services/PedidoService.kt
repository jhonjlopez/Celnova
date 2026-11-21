package com.example.celnova.services

import com.example.celnova.CelnovaFirebase
import com.example.celnova.models.CarritoItem
import com.example.celnova.models.Pedido
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue

object PedidoService {
    const val COLECCION = "pedidos"

    fun crear(
        cliente: String,
        items: List<CarritoItem>,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val total = items.sumOf { it.precio * it.cantidad }
        CelnovaFirebase.db.collection(COLECCION).add(
            mapOf(
                "cliente" to cliente,
                "items" to items,
                "total" to total,
                "estado" to "pendiente",
                "createdAt" to FieldValue.serverTimestamp()
            )
        )
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    fun crearCheckout(
        cliente: String,
        items: List<CarritoItem>,
        subtotal: Double,
        descuento: Double,
        total: Double,
        cupon: String,
        direccion: String,
        metodoPago: String,
        onSuccess: (String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val referencia = CelnovaFirebase.db.collection(COLECCION).document()
        referencia.set(
            mapOf(
                "cliente" to cliente,
                "items" to items,
                "subtotal" to subtotal,
                "descuento" to descuento,
                "cupon" to cupon,
                "direccion" to direccion,
                "metodoPago" to metodoPago,
                "total" to total,
                "estado" to "confirmado",
                "createdAt" to FieldValue.serverTimestamp()
            )
        )
            .addOnSuccessListener { onSuccess(referencia.id) }
            .addOnFailureListener { onError(it) }
    }

    fun listar(cliente: String, onSuccess: (List<Pedido>) -> Unit, onError: (Exception) -> Unit) {
        CelnovaFirebase.db.collection(COLECCION).whereEqualTo("cliente", cliente).get()
            .addOnSuccessListener { snaps -> onSuccess(snaps.map { it.toPedido(cliente) }) }
            .addOnFailureListener { onError(it) }
    }

    private fun DocumentSnapshot.toPedido(cliente: String): Pedido = Pedido(
        cliente = getString("cliente") ?: cliente,
        items = itemsDeSnapshot(),
        total = getDouble("total") ?: 0.0,
        estado = getString("estado") ?: "pendiente",
        createdAt = getTimestamp("createdAt")?.toDate()?.time ?: 0L,
        subtotal = getDouble("subtotal") ?: 0.0,
        descuento = getDouble("descuento") ?: 0.0,
        cupon = getString("cupon") ?: "",
        direccion = getString("direccion") ?: "",
        metodoPago = getString("metodoPago") ?: ""
    )

    private fun DocumentSnapshot.itemsDeSnapshot(): List<CarritoItem> {
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
