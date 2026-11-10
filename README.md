# CELNOVA - Aplicación Móvil Android Nativa (Tienda Virtual)

## 📱 Descripción del Proyecto
CELNOVA es un prototipo funcional de interfaz gráfica para una tienda virtual Android orientada a la comercialización de productos tecnológicos. Desarrollada en Kotlin con componentes de Material Design (Material 3), implementa los flujos de navegación completos para **Clientes**, **Vendedores** y **Administradores**, además de módulos transversales.

---

## 🚀 Características e Interfaces Desarrolladas

### 🔑 Módulo Transversal y Autenticación
- **Splash Screen:** Pantalla de bienvenida con temporizador y animación.
- **Login:** Autenticación por correo/contraseña y soporte para **Autenticación Biométrica (Huella Digital)**.
- **Registro:** Formulario con medidor de fortaleza de contraseña y validación de formato celular (Colombia).
- **Recuperación:** Recuperación mediante SMS o correo electrónico.

### 🛒 Módulo Cliente
- **Inicio (Home):** Banner promocional y productos destacados.
- **Catálogo:** Navegación por categorías tecnológicas.
- **Búsqueda:** Historial y tendencias.
- **Carrito:** Visualización de subtotal, impuestos y cálculo simulado.
- **Perfil:** Gestión de datos del usuario, pedidos y ajustes.

### 🛡️ Módulo Administrador
- **Dashboard Principal:** Indicadores KPI, gráfico de rendimiento y auditoría.
- **Gestión de Usuarios:** Listado de usuarios con edición dinámica mediante `BottomSheetDialog`.
- **Gestión de Productos:** Catálogo administrativo con precios, SKUs y estados.
- **Ajustes:** Configuración global de la tienda.
- **Perfil Profesional:** Información del administrador y sesiones activas.

---

## 🛠️ Tecnologías Utilizadas
- **Lenguaje:** Kotlin
- **Diseño de Interfaz:** XML + Material Components (Material 3)
- **SDK Mínimo:** Android 10 (API 29) | **Target SDK:** API 37
- **Librerías principales:**
  - `androidx.biometric:biometric` (Autenticación biométrica)
  - `com.googlecode.libphonenumber:libphonenumber` (Validación de teléfonos)
  - `de.hdodenhof:circleimageview` (Avatares circulares)

---

## ✒️ Autor
Proyecto desarrollado por Jhon Lopez para la asignatura **Desarrollo de Aplicaciones Móviles Nativas**.
