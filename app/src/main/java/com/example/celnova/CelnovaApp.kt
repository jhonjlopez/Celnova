package com.example.celnova

import android.app.Application
import com.example.celnova.services.AuthService
import com.example.celnova.services.SesionService

class CelnovaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        CelnovaFirebase.inicializar()
        SesionService.inicializar(this)
        AuthService.ensureSeeded()
    }
}
