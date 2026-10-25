package com.example.kaijuinventory.ui.components

import android.graphics.BitmapFactory
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.kaijuinventory.model.Producto
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatearPrecio(precio: Int): String =
    NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CL")).format(precio)

fun formatearFecha(fecha: Long): String =
    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(fecha))

@Composable
fun CampoTexto(
    valor: String,
    onCambio: (String) -> Unit,
    etiqueta: String,
    error: String?,
    numerico: Boolean = false
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        label = { Text(etiqueta) },
        isError = error != null,
        supportingText = if (error != null) {
            { Text(error) }
        } else null,
        trailingIcon = {
            if (error != null) {
                Icon(Icons.Default.Warning, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
            } else if (valor.isNotBlank()) {
                Icon(Icons.Default.CheckCircle, contentDescription = "Correcto", tint = MaterialTheme.colorScheme.primary)
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (numerico) KeyboardType.Number else KeyboardType.Text
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun ChipStock(producto: Producto) {
    // El color cambia con animación cuando el producto pasa a stock bajo
    val color by animateColorAsState(
        targetValue = if (producto.stockBajo) MaterialTheme.colorScheme.errorContainer
        else MaterialTheme.colorScheme.tertiaryContainer,
        label = "colorStock"
    )
    Surface(color = color, shape = RoundedCornerShape(8.dp)) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (producto.stockBajo) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = "Stock bajo",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text("Stock: ${producto.stock}", style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun FotoProducto(ruta: String?, tamano: Dp) {
    val imagen = remember(ruta) { ruta?.let { BitmapFactory.decodeFile(it)?.asImageBitmap() } }
    val forma = Modifier
        .size(tamano)
        .clip(RoundedCornerShape(12.dp))

    if (imagen != null) {
        Image(
            bitmap = imagen,
            contentDescription = "Foto del producto",
            contentScale = ContentScale.Crop,
            modifier = forma
        )
    } else {
        Box(
            modifier = forma.background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.ShoppingCart,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}
