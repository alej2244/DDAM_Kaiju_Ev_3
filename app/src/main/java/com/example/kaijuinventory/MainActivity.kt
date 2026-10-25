package com.example.kaijuinventory

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.example.kaijuinventory.ui.navigation.AppNavigation
import com.example.kaijuinventory.ui.theme.KaijuInventoryTheme
import com.example.kaijuinventory.viewmodel.InventarioViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: InventarioViewModel by viewModels()

    private val permisoNotificaciones =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (savedInstanceState == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permisoNotificaciones.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            KaijuInventoryTheme {
                AppNavigation(viewModel)
            }
        }
    }
}
