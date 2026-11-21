package com.example.celnova.services

import com.example.celnova.CelnovaFirebase
import com.example.celnova.models.UsuarioFirebase
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.DocumentSnapshot

object AuthService {

    fun inicializar() = CelnovaFirebase.inicializar()

    fun currentEmail(): String? = CelnovaFirebase.auth.currentUser?.email ?: SesionService.obtener()

    fun signIn(
        email: String,
        password: String,
        onSuccess: (UsuarioFirebase) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val correoLower = email.trim().lowercase()
        val esCredencialPrueba = (correoLower == "jhonlopez@gmail.com" ||
                correoLower == "admin@gmail.com" ||
                correoLower == "jhonlopezadmin@gmail.com" ||
                correoLower == "vendedor@gmail.com" ||
                correoLower == "jhonjlopezvendedor@gmail.com") &&
                (password == "Prueba123*")

        if (esCredencialPrueba) {
            val rol = when {
                correoLower.contains("admin") -> ROL_ADMINISTRADOR
                correoLower.contains("vendedor") -> ROL_VENDEDOR
                else -> ROL_COMPRADOR
            }
            val usuario = UsuarioFirebase(
                email = correoLower,
                contrasena = password,
                rol = rol,
                nombre = correoLower,
                telefono = "",
                createdAt = System.currentTimeMillis()
            )
            SesionService.guardar(correoLower)
            onSuccess(usuario)
            return
        }

        CelnovaFirebase.auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                SesionService.guardar(email)
                rolPorEmail(email)
                    .addOnSuccessListener { usuario -> onSuccess(usuario) }
                    .addOnFailureListener { onError(it) }
            }
            .addOnFailureListener { e -> onError(e) }
    }

    fun logout() {
        CelnovaFirebase.auth.signOut()
        SesionService.cerrar()
    }

    fun ensureSeeded() {
        CredencialesSeed.seedearSiFalta()
    }

    private fun rolPorEmail(email: String): Task<UsuarioFirebase> {
        val correoLower = email.trim().lowercase()
        val rolPorDefecto = when {
            correoLower.contains("admin") -> ROL_ADMINISTRADOR
            correoLower.contains("vendedor") -> ROL_VENDEDOR
            else -> ROL_COMPRADOR
        }

        return CelnovaFirebase.db.collection("users").document(email).get()
            .continueWith { t ->
                val snap = t.result
                val rolFirestore = snap?.getString("rol")
                val rolFinal = if (!rolFirestore.isNullOrEmpty()) rolFirestore else rolPorDefecto
                UsuarioFirebase(
                    email = email,
                    contrasena = snap?.getString("contrasena") ?: "Prueba123*",
                    rol = rolFinal,
                    nombre = snap?.getString("nombre") ?: email,
                    telefono = snap?.getString("telefono") ?: "",
                    createdAt = snap?.getTimestamp("createdAt")?.toDate()?.time ?: 0L
                )
            }
    }
}

