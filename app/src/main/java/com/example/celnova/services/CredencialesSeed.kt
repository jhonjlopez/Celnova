package com.example.celnova.services

import com.example.celnova.models.UsuarioFirebase
import com.example.celnova.CelnovaFirebase
import com.google.firebase.firestore.FieldValue

object CredencialesSeed {

    private val USUARIOS_SEED = listOf(
        UsuarioFirebase(
            email = "jhonlopez@gmail.com",
            contrasena = "Prueba123*",
            rol = ROL_COMPRADOR,
            nombre = "Comprador"
        ),
        UsuarioFirebase(
            email = "admin@gmail.com",
            contrasena = "Prueba123*",
            rol = ROL_ADMINISTRADOR,
            nombre = "Administrador"
        ),
        UsuarioFirebase(
            email = "vendedor@gmail.com",
            contrasena = "Prueba123*",
            rol = ROL_VENDEDOR,
            nombre = "Vendedor"
        )
    )

    fun seedearSiFalta() {
        for (usuario in USUARIOS_SEED) {
            crearSiFalta(usuario)
        }
    }

    private fun crearSiFalta(usuario: UsuarioFirebase) {
        val db = CelnovaFirebase.db

        db.collection("users").document(usuario.email).get()
            .addOnSuccessListener { snap ->
                if (!snap.exists()) {
                    db.collection("users").document(usuario.email).set(
                        mapOf(
                            "email" to usuario.email,
                            "contrasena" to usuario.contrasena,
                            "rol" to usuario.rol,
                            "nombre" to usuario.nombre,
                            "createdAt" to FieldValue.serverTimestamp()
                        )
                    )
                } else if (snap.getString("rol").isNullOrEmpty()) {
                    db.collection("users").document(usuario.email).update("rol", usuario.rol)
                }
            }
            .addOnFailureListener { }

        CelnovaFirebase.auth.createUserWithEmailAndPassword(usuario.email, usuario.contrasena)
            .addOnSuccessListener { result ->
                db.collection("users").document(usuario.email).update("uid", result.user?.uid ?: "")
            }
            .addOnFailureListener { }
    }
}

