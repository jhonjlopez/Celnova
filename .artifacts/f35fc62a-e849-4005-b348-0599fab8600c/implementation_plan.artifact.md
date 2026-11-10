# Implementación de Indicador Dinámico de Fortaleza de Contraseña en Registro

## Resumen del Problema
El usuario desea que en la pantalla de registro ([RegistroActivity.kt](file:///C:/Users/Jhon zafeon/AndroidStudioProjects/Celnova/app/src/main/java/com/example/celnova/activities/RegistroActivity.kt) y [activity_registro.xml](file:///C:/Users/Jhon zafeon/AndroidStudioProjects/Celnova/app/src/main/res/layout/activity_registro.xml)), el indicador de fortaleza de contraseña evalúe dinámicamente lo que escribe el usuario y muestre:
- **3 barras verdes** y texto **"Segura"** cuando sea fuerte.
- **2 barras amarillas** y texto **"Media"** cuando sea moderada.
- **1 barra roja** y texto **"Débil"** cuando sea baja.
- **0 barras / vacío** cuando esté vacío.

## User Review Required

> [!IMPORTANT]
> Revisar la disposición de las 3 barras indicadoras de contraseña y el TextView de estado en [activity_registro.xml](file:///C:/Users/Jhon zafeon/AndroidStudioProjects/Celnova/app/src/main/res/layout/activity_registro.xml).

## Proposed Changes

### UI Components & Layout
#### [MODIFY] [activity_registro.xml](file:///C:/Users/Jhon zafeon/AndroidStudioProjects/Celnova/app/src/main/res/layout/activity_registro.xml)
- Añadir el campo de entrada de contraseña (`etRegistroContrasena`), el botón de alternar visibilidad (`btnRegistroTogglePassword`), las 3 barras indicadoras de nivel y el texto dinámico de estado (`txtRegistroContrasenaSegura`).

### Activity Logic
#### [NEW / MODIFY] [RegistroActivity.kt](file:///C:/Users/Jhon zafeon/AndroidStudioProjects/Celnova/app/src/main/java/com/example/celnova/activities/RegistroActivity.kt)
- Implementar un `TextWatcher` en el campo de contraseña para evaluar en tiempo real:
  - Longitud $\ge$ 8 caracteres.
  - Presencia de mayúsculas/minúsculas.
  - Presencia de números y símbolos especiales.
- Actualizar dinámicamente el color de las 3 barras y el texto de estado ("Débil", "Media", "Segura").

## Verification Plan

### Automated Tests
- Compilación del proyecto (`gradle_build app:assembleDebug`).

### Manual Verification
- Desplegar en emulador o dispositivo y verificar el comportamiento al escribir diferentes contraseñas.
