package com.example.kaijuinventory.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kaijuinventory.model.Movimiento
import com.example.kaijuinventory.model.Producto
import com.example.kaijuinventory.model.TipoMovimiento
import com.example.kaijuinventory.ui.components.CampoTexto
import com.example.kaijuinventory.ui.components.ChipStock
import com.example.kaijuinventory.ui.components.FotoProducto
import com.example.kaijuinventory.ui.components.formatearFecha
import com.example.kaijuinventory.ui.components.formatearPrecio
import com.example.kaijuinventory.validation.Validaciones
import com.example.kaijuinventory.viewmodel.InventarioViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleProductoScreen(
    viewModel: InventarioViewModel,
    productoId: Long,
    onVolver: () -> Unit,
    onEditar: (Long) -> Unit
) {
    val productos by viewModel.productos.collectAsState()
    val movimientos by viewModel.movimientos.collectAsState()
    val producto = productos.find { it.id == productoId }

    var confirmarEliminar by remember { mutableStateOf(false) }
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(productoId) { viewModel.cargarMovimientos(productoId) }

    if (producto == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(producto.nombre) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { onEditar(producto.id) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                    }
                    IconButton(onClick = { confirmarEliminar = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { DatosProducto(producto) }
            item {
                AnimatedVisibility(visible = producto.stockBajo) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                            Text("Stock bajo: hay que reponer este producto")
                        }
                    }
                }
            }
            item { FormularioMovimiento(producto, viewModel, snackbar) }
            item { Text("Historial de movimientos", style = MaterialTheme.typography.titleMedium) }
            if (movimientos.isEmpty()) {
                item { Text("Todavía no hay movimientos", style = MaterialTheme.typography.bodyMedium) }
            }
            items(movimientos, key = { it.id }) { movimiento ->
                MovimientoItem(movimiento, modifier = Modifier.animateItem())
            }
        }
    }

    if (confirmarEliminar) {
        AlertDialog(
            onDismissRequest = { confirmarEliminar = false },
            title = { Text("Eliminar producto") },
            text = { Text("Se eliminará ${producto.nombre} y todo su historial.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmarEliminar = false
                    viewModel.eliminarProducto(producto.id)
                    onVolver()
                }) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { confirmarEliminar = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun DatosProducto(producto: Producto) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FotoProducto(ruta = producto.foto, tamano = 96.dp)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Código: ${producto.codigo}")
                Text("Categoría: ${producto.categoria}")
                Text("Precio: ${formatearPrecio(producto.precio)}")
                Text("Stock mínimo: ${producto.stockMinimo}")
                ChipStock(producto)
            }
        }
    }
}

@Composable
private fun FormularioMovimiento(
    producto: Producto,
    viewModel: InventarioViewModel,
    snackbar: SnackbarHostState
) {
    var tipo by rememberSaveable { mutableStateOf(TipoMovimiento.ENTRADA) }
    var cantidad by rememberSaveable { mutableStateOf("") }
    var intento by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val error = Validaciones.validarCantidad(cantidad, tipo == TipoMovimiento.SALIDA, producto.stock)

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Registrar movimiento", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TipoMovimiento.entries.forEach { opcion ->
                    FilterChip(
                        selected = tipo == opcion,
                        onClick = { tipo = opcion },
                        label = { Text(opcion.texto) }
                    )
                }
            }
            CampoTexto(
                valor = cantidad,
                onCambio = { cantidad = it },
                etiqueta = "Cantidad",
                error = if (intento || cantidad.isNotEmpty()) error else null,
                numerico = true
            )
            Button(
                onClick = {
                    intento = true
                    if (error == null) {
                        viewModel.registrarMovimiento(producto, tipo, cantidad.toInt())
                        val mensaje = "${tipo.texto} de $cantidad unidades registrada"
                        cantidad = ""
                        intento = false
                        scope.launch { snackbar.showSnackbar(mensaje) }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrar")
            }
        }
    }
}

@Composable
private fun MovimientoItem(movimiento: Movimiento, modifier: Modifier = Modifier) {
    val esEntrada = movimiento.tipo == TipoMovimiento.ENTRADA
    ListItem(
        modifier = modifier,
        headlineContent = { Text("${movimiento.tipo.texto} de ${movimiento.cantidad} unidades") },
        supportingContent = { Text(formatearFecha(movimiento.fecha)) },
        leadingContent = {
            Icon(
                if (esEntrada) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = movimiento.tipo.texto,
                tint = if (esEntrada) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
            )
        }
    )
}
