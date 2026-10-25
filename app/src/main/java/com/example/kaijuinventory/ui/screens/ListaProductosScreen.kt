package com.example.kaijuinventory.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kaijuinventory.model.Producto
import com.example.kaijuinventory.ui.components.ChipStock
import com.example.kaijuinventory.ui.components.FotoProducto
import com.example.kaijuinventory.ui.components.formatearPrecio
import com.example.kaijuinventory.viewmodel.InventarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaProductosScreen(
    viewModel: InventarioViewModel,
    onAgregar: () -> Unit,
    onVerProducto: (Long) -> Unit
) {
    val productos by viewModel.productos.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inventario Kaiju") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAgregar) {
                Icon(Icons.Default.Add, contentDescription = "Agregar producto")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Resumen(total = productos.size, conStockBajo = productos.count { it.stockBajo })
            }
            if (productos.isEmpty()) {
                item {
                    Text(
                        "No hay productos registrados. Usa el botón + para agregar uno.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            items(productos, key = { it.id }) { producto ->
                ProductoItem(
                    producto = producto,
                    onClick = { onVerProducto(producto.id) },
                    modifier = Modifier.animateItem()
                )
            }
        }
    }
}

@Composable
private fun Resumen(total: Int, conStockBajo: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            DatoResumen("Productos", total.toString())
            DatoResumen("Con stock bajo", conStockBajo.toString())
        }
    }
}

@Composable
private fun DatoResumen(titulo: String, valor: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(valor, style = MaterialTheme.typography.titleLarge)
        Text(titulo, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun ProductoItem(producto: Producto, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(onClick = onClick, modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FotoProducto(ruta = producto.foto, tamano = 56.dp)
            Column(modifier = Modifier.weight(1f)) {
                Text(producto.nombre, style = MaterialTheme.typography.titleMedium)
                Text("${producto.codigo} · ${producto.categoria}", style = MaterialTheme.typography.bodySmall)
                Text(formatearPrecio(producto.precio), style = MaterialTheme.typography.bodyMedium)
            }
            ChipStock(producto)
        }
    }
}
