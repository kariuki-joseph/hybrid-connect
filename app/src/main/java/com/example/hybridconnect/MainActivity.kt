// MainActivity.kt
package com.example.hybridconnect

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.example.hybridconnect.data.local.database.AppDatabase
import com.example.hybridconnect.domain.services.SmsProcessor
import com.example.hybridconnect.domain.services.SocketService
import com.example.hybridconnect.presentation.navigation.NavGraph
import com.example.hybridconnect.presentation.theme.HybridConnectTheme
import com.example.hybridconnect.presentation.ui.components.GlobalSnackbarHost
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var database: AppDatabase
    @Inject lateinit var smsListener: SmsProcessor
    @Inject lateinit var socketService: SocketService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissions(this)
        socketService.connect()
        requestBatterOptimizationExemption()

        enableEdgeToEdge()
        setContent {
            HybridConnectTheme {
                Scaffold(
                    snackbarHost = { GlobalSnackbarHost() }
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier.fillMaxSize().padding(innerPadding),
                        color = MaterialTheme.colorScheme.background,
                        ) {
                        val navController = rememberNavController()
                        NavGraph(navController = navController)
                    }
                }
            }
        }
    }

    private fun requestPermissions(activity: Activity) {
        val permissions = arrayListOf(
            android.Manifest.permission.READ_SMS,
            android.Manifest.permission.RECEIVE_SMS,
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(android.Manifest.permission.POST_NOTIFICATIONS)
        }

        if (permissions.any { ContextCompat.checkSelfPermission(activity, it) != PackageManager.PERMISSION_GRANTED }) {
            ActivityCompat.requestPermissions(activity, permissions.toArray(arrayOf()), 0)
        }
    }

    @SuppressLint("BatteryLife")
    private fun requestBatterOptimizationExemption(){
        val packageName = packageName
        val pm = getSystemService(PowerManager::class.java)
        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
        }
    }
}