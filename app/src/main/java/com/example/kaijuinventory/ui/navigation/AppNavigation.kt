package com.example.kaijuinventory.ui.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.kaijuinventory.ui.screens.DetalleProductoScreen
import com.example.kaijuinventory.ui.screens.FormularioProductoScreen
import com.example.kaijuinventory.ui.screens.ListaProductosScreen
import com.example.kaijuinventory.viewmodel.InventarioViewModel

@Composable
fun AppNavigation(viewModel: InventarioViewModel) {
    val navController = rememberNavController()
    val argumentoId = listOf(navArgument("id") { type = NavType.LongType })

    NavHost(
        navController = navController,
        startDestination = "lista",
        enterTransition = { slideInHorizontally { it } + fadeIn() },
        exitTransition = { fadeOut() },
        popEnterTransition = { fadeIn() },
        popExitTransition = { slideOutHorizontally { it } + fadeOut() }
    ) {
        composable("lista") {
            ListaProductosScreen(
                viewModel = viewModel,
                onAgregar = { navController.navigate("formulario/0") },
                onVerProducto = { id -> navController.navigate("detalle/$id") }
            )
        }
        composable("formulario/{id}", arguments = argumentoId) { entrada ->
            FormularioProductoScreen(
                viewModel = viewModel,
                productoId = entrada.arguments?.getLong("id") ?: 0L,
                onVolver = { navController.popBackStack() }
            )
        }
        composable("detalle/{id}", arguments = argumentoId) { entrada ->
            DetalleProductoScreen(
                viewModel = viewModel,
                productoId = entrada.arguments?.getLong("id") ?: 0L,
                onVolver = { navController.popBackStack() },
                onEditar = { id -> navController.navigate("formulario/$id") }
            )
        }
    }
}
