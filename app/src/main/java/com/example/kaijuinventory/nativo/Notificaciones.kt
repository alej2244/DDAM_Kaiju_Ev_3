package com.example.kaijuinventory.nativo

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.kaijuinventory.R

object Notificaciones {

    private const val CANAL_STOCK = "stock_bajo"

    fun avisarStockBajo(context: Context, nombreProducto: String, stock: Int) {
        // Desde Android 13 se necesita el permiso concedido por el usuario
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(CANAL_STOCK, "Stock bajo", NotificationManager.IMPORTANCE_DEFAULT)
            context.getSystemService(NotificationManager::class.java).createNotificationChannel(canal)
        }

        val notificacion = NotificationCompat.Builder(context, CANAL_STOCK)
            .setSmallIcon(R.drawable.ic_alerta_stock)
            .setContentTitle("Stock bajo")
            .setContentText("$nombreProducto quedó con $stock unidades")
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(nombreProducto.hashCode(), notificacion)
    }
}
