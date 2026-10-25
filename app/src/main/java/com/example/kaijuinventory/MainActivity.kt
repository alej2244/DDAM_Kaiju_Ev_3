package com.example.kaijuinventory

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import com.example.kaijuinventory.ui.theme.KaijuInventoryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KaijuInventoryTheme {
                Text("Kaiju Inventario")
            }
        }
    }
}
