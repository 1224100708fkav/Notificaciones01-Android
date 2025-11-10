package com.ejemplo.notificacionesapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.ejemplo.notificacionesapp.ui.theme.NotificacionesComposeAppTheme

class MainActivity : ComponentActivity() {
    private lateinit var notificationManager: AppNotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notificationManager = AppNotificationManager(applicationContext)

        setContent {
            NotificacionesComposeAppTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    NotificationScreen(notificationManager = notificationManager)
                }
            }
        }
    }
}

@Composable
fun NotificationScreen(notificationManager: AppNotificationManager) {
    val context = LocalContext.current
    var notificationCount by remember { mutableStateOf(0) }
    var permissionGranted by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED)
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        permissionGranted = isGranted
    }

    val checkAndRequestNotificationPermission: () -> Unit = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)) {
                PackageManager.PERMISSION_GRANTED -> {
                    permissionGranted = true
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            permissionGranted = true
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🧠 Notificaciones Inteligentes",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(32.dp))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !permissionGranted) {
            Button(onClick = checkAndRequestNotificationPermission) {
                Text("Picar para Permitir Notificaciones")
            }
            Spacer(modifier = Modifier.height(16.dp))
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Text("Permiso de Notificación: Concedido ✅")
            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(
            onClick = {
                if (permissionGranted || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                    notificationCount++
                    val (title, message) = notificationManager.getRandomMotivationalMessage()
                    notificationManager.sendNotification(
                        id = notificationCount,
                        title = title,
                        message = message
                    )
                } else {
                    checkAndRequestNotificationPermission()
                }
            },
            enabled = true
        ) {
            Text("Picar para Enviar Notificación")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Notificaciones Enviadas: $notificationCount")
    }
}