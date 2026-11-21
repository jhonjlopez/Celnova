package com.example.celnova

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

object CelnovaFirebase {
    private const val EMULATOR_HOST = "localhost"
    private const val AUTH_EMULATOR_PORT = 9099
    private const val FIRESTORE_EMULATOR_PORT = 8080
    private const val STORAGE_EMULATOR_PORT = 9199

    const val USE_EMULATOR = false

    @Volatile
    private var inicializado = false

    fun inicializar() {
        if (inicializado) return
        inicializado = true
        if (USE_EMULATOR) {
            try {
                FirebaseAuth.getInstance().useEmulator(EMULATOR_HOST, AUTH_EMULATOR_PORT)
            } catch (_: Exception) {}
            try {
                FirebaseFirestore.getInstance().useEmulator(EMULATOR_HOST, FIRESTORE_EMULATOR_PORT)
            } catch (_: Exception) {}
            try {
                FirebaseStorage.getInstance().useEmulator(EMULATOR_HOST, STORAGE_EMULATOR_PORT)
            } catch (_: Exception) {}
        }
    }

    val auth: FirebaseAuth get() = FirebaseAuth.getInstance()
    val db: FirebaseFirestore get() = FirebaseFirestore.getInstance()
    val storage: FirebaseStorage get() = FirebaseStorage.getInstance()
}
