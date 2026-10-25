package com.example.kaijuinventory.ui.screens

import android.content.ActivityNotFoundException
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.kaijuinventory.model.Producto
import com.example.kaijuinventory.nativo.Fotos
import com.example.kaijuinventory.ui.components.CampoTexto
import com.example.kaijuinventory.ui.components.FotoProducto
import com.example.kaijuinventory.validation.Validaciones
import com.example.kaijuinventory.viewmodel.InventarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioProductoScreen(
    viewModel: InventarioViewModel,
    productoId: Long,
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    val existente = remember(productoId) { viewModel.buscarProducto(productoId) }
    val esEdicion = existente != null

    var codigo by rememberSaveable { mutableStateOf(existente?.codigo ?: "") }
    var nombre by rememberSaveable { mutableStateOf(existente?.nombre ?: "") }
    var categoria by rememberSaveable { mutableStateOf(existente?.categoria ?: "") }
    var precio by rememberSaveable { mutableStateOf(existente?.precio?.toString() ?: "") }
    var stock by rememberSaveable { mutableStateOf(existente?.stock?.toString() ?: "") }
    var stockMinimo by rememberSaveable { mutableStateOf(existente?.stockMinimo?.toString() ?: "") }
    var foto by rememberSaveable { mutableStateOf(existente?.foto) }
    var intentoGuardar by rememberSaveable { mutableStateOf(false) }
    var errorCodigoRepetido by remember { mutableStateOf<String?>(null) }

    // Recurso nativo: cámara del dispositivo
    val camara = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) foto = Fotos.guardar(context, bitmap)
    }

    val errorCodigo = errorCodigoRepetido ?: Validaciones.validarCodigo(codigo)
    val errorNombre = Validaciones.validarNombre(nombre)
    val errorCategoria = Validaciones.validarCategoria(categoria)
    val errorPrecio = Validaciones.validarPrecio(precio)
    val errorStock = Validaciones.validarStock(stock)
    val errorStockMinimo = Validaciones.validarStock(stockMinimo)
    val hayErrores = listOf(errorCodigo, errorNombre, errorCategoria, errorPrecio, errorStock, errorStockMinimo)
        .any { it != null }

    // Los errores se muestran cuando el usuario escribe algo o intenta guardar
    fun mostrar(error: String?, valor: String) = if (intentoGuardar || valor.isNotEmpty()) error else null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (esEdicion) "Editar producto" else "Nuevo producto") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FotoProducto(ruta = foto, tamano = 96.dp)
                OutlinedButton(onClick = {
                    try {
                        camara.launch(null)
                    } catch (e: ActivityNotFoundException) {
                        Toast.makeText(context, "No hay una app de cámara disponible", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text(if (foto == null) "Tomar foto" else "Cambiar foto")
                }
            }

            CampoTexto(codigo, { codigo = it; errorCodigoRepetido = null }, "Código (ej: KJ-010)", mostrar(errorCodigo, codigo))
            CampoTexto(nombre, { nombre = it }, "Nombre", mostrar(errorNombre, nombre))
            CampoTexto(categoria, { categoria = it }, "Categoría", mostrar(errorCategoria, categoria))
            CampoTexto(precio, { precio = it }, "Precio (CLP)", mostrar(errorPrecio, precio), numerico = true)
            // Al editar, el stock solo cambia registrando movimientos
            if (!esEdicion) {
                CampoTexto(stock, { stock = it }, "Stock inicial", mostrar(errorStock, stock), numerico = true)
            }
            CampoTexto(stockMinimo, { stockMinimo = it }, "Stock mínimo", mostrar(errorStockMinimo, stockMinimo), numerico = true)

            AnimatedVisibility(visible = intentoGuardar && hayErrores) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Text("Revisa los campos marcados en rojo", modifier = Modifier.padding(12.dp))
                }
            }

            Button(
                onClick = {
                    intentoGuardar = true
                    if (!hayErrores) {
                        val producto = Producto(
                            id = existente?.id ?: 0,
                            codigo = codigo.trim().uppercase(),
                            nombre = nombre.trim(),
                            categoria = categoria.trim(),
                            precio = precio.toInt(),
                            stock = stock.toInt(),
                            stockMinimo = stockMinimo.toInt(),
                            foto = foto
                        )
                        viewModel.guardarProducto(producto) { error ->
                            if (error == null) onVolver() else errorCodigoRepetido = error
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text("Guardar")
            }
        }
    }
}
