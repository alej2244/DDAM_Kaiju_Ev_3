package com.example.kaijuinventory.nativo

import android.content.Context
import android.graphics.Bitmap
import java.io.File

object Fotos {

    // Guarda la foto tomada con la cámara en el almacenamiento interno de la app
    fun guardar(context: Context, bitmap: Bitmap): String {
        val archivo = File(context.filesDir, "producto_${System.currentTimeMillis()}.jpg")
        archivo.outputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it) }
        return archivo.absolutePath
    }
}
