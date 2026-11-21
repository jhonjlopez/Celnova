package com.example.celnova.services

import android.net.Uri
import com.example.celnova.CelnovaFirebase
import com.example.celnova.R
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

object StorageService {

    fun refProducto(id: String): StorageReference =
        CelnovaFirebase.storage.reference
            .child("productos")
            .child("$id.jpg")

    fun uploadBytes(ref: StorageReference, bytes: ByteArray): UploadTask =
        ref.putBytes(bytes)
}

