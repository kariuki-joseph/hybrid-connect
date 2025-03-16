package com.example.hybridconnect

import android.annotation.SuppressLint
import android.app.Activity
import android.app.role.RoleManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.provider.Telephony
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.example.hybridconnect.domain.services.SocketService
import com.example.hybridconnect.domain.services.interfaces.MainActivityInterface
import com.example.hybridconnect.presentation.navigation.NavGraph
import com.example.hybridconnect.presentation.theme.HybridConnectTheme
import com.example.hybridconnect.presentation.ui.components.GlobalSnackbarHost
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity(), MainActivityInterface {
    @Inject
    lateinit var socketService: SocketService

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
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
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
        if (!areWeTheDefaultMessagingApp()) {
            requestDefaultSmsAppSelection()
        } else {
            requestSmsPermissions()
        }
    }

    @SuppressLint("BatteryLife")
    private fun requestBatterOptimizationExemption() {
        val packageName = packageName
        val pm = getSystemService(PowerManager::class.java)
        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
        }
    }

    private fun requestSmsPermissions() {
        val permissions = mutableListOf(
            android.Manifest.permission.READ_SMS,
            android.Manifest.permission.RECEIVE_SMS,
            android.Manifest.permission.READ_PHONE_STATE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(android.Manifest.permission.POST_NOTIFICATIONS)
        }

        if (permissions.any {
                ContextCompat.checkSelfPermission(
                    this,
                    it
                ) != PackageManager.PERMISSION_GRANTED
            }) {
            ActivityCompat.requestPermissions(this, permissions.toTypedArray(), 0)
        }
    }

    override fun areWeTheDefaultMessagingApp(): Boolean {
        val packageName = application.packageName;
        val smsPackage = Telephony.Sms.getDefaultSmsPackage(application)
        return packageName == smsPackage
    }

    override fun requestDefaultSmsAppSelection() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            val roleManager = application.getSystemService(RoleManager::class.java)
            if (roleManager.isRoleAvailable(RoleManager.ROLE_SMS)) {
                if (roleManager.isRoleHeld(RoleManager.ROLE_SMS)) {
                    Toast.makeText(
                        application,
                        "Hybrid Connect set as default SMS App",
                        Toast.LENGTH_SHORT
                    ).show()
                    application.startActivity(Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS))
                } else {
                    requestRoleLauncher.launch(roleManager.createRequestRoleIntent(RoleManager.ROLE_SMS))
                }
            }
        } else {
            val intent = Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT)
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, application.packageName)
            application.startActivity(intent)
        }
    }

    private val requestRoleLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                Toast.makeText(
                    applicationContext,
                    "Hybrid Connect set as default.",
                    Toast.LENGTH_SHORT
                ).show()
                requestSmsPermissions() // Proceed to request other permissions
            } else {
                Toast.makeText(
                    applicationContext,
                    "Default SMS role not granted!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
}