package com.example.kaijuinventory.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kaijuinventory.data.KaijuDbHelper
import com.example.kaijuinventory.model.Movimiento
import com.example.kaijuinventory.model.Producto
import com.example.kaijuinventory.model.TipoMovimiento
import com.example.kaijuinventory.nativo.Notificaciones
import com.example.kaijuinventory.repository.InventarioRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InventarioViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = InventarioRepository(KaijuDbHelper(application))

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos.asStateFlow()

    private val _movimientos = MutableStateFlow<List<Movimiento>>(emptyList())
    val movimientos: StateFlow<List<Movimiento>> = _movimientos.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _productos.value = repository.obtenerProductos()
        }
    }

    fun buscarProducto(id: Long): Producto? = _productos.value.find { it.id == id }

    fun cargarMovimientos(productoId: Long) {
        _movimientos.value = emptyList()
        viewModelScope.launch(Dispatchers.IO) {
            _movimientos.value = repository.obtenerMovimientos(productoId)
        }
    }

    // alTerminar recibe null si se guardó, o el mensaje de error
    fun guardarProducto(producto: Producto, alTerminar: (String?) -> Unit) {
        viewModelScope.launch {
            val error = withContext(Dispatchers.IO) {
                if (repository.existeCodigo(producto.codigo, producto.id)) {
                    "Ya existe un producto con ese código"
                } else {
                    repository.guardarProducto(producto)
                    _productos.value = repository.obtenerProductos()
                    null
                }
            }
            alTerminar(error)
        }
    }

    fun eliminarProducto(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.eliminarProducto(id)
            _productos.value = repository.obtenerProductos()
        }
    }

    fun registrarMovimiento(producto: Producto, tipo: TipoMovimiento, cantidad: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val nuevoStock = repository.registrarMovimiento(producto, tipo, cantidad)
            _productos.value = repository.obtenerProductos()
            _movimientos.value = repository.obtenerMovimientos(producto.id)

            if (nuevoStock <= producto.stockMinimo) {
                Notificaciones.avisarStockBajo(getApplication<Application>(),producto.nombre, nuevoStock)
            }
        }
    }
}
