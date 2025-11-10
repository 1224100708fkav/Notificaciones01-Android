package com.ejemplo.notificacionesapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService

class AppNotificationManager(private val context: Context) {

    private val notificationManager: NotificationManager =
        context.getSystemService()!!


    companion object {
        const val CHANNEL_ID = "estudio_recordatorios_channel"
        const val CHANNEL_NAME = "Recordatorios de Estudio"
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH // IMPORTANTE
            ).apply {
                description = "Canal para notificaciones motivacionales y recordatorios."
                setShowBadge(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendNotification(id: Int, title: String, message: String) {

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            id,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )


        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()


        notificationManager.notify(id, notification)
    }

    fun getRandomMotivationalMessage(): Pair<String, String> {
        val messages = listOf(
            "¡Hora de brillar! 🌟" to "Tu cerebro está listo para absorber conocimiento, no lo detengas.",
            "Pausa Activa 🧘" to "¿Llevas más de una hora? Levántate y estira las piernas un momento.",
            "¡Meta Cumplida! 🎉" to "Has completado una sesión. ¡Felicidades! Sigue así, el esfuerzo vale la pena.",
            "Un paso a la vez 🚶" to "No te abrumes, concéntrate solo en la tarea que tienes enfrente.",
            "Recordatorio 🤓" to "No olvides revisar el código fuente para aprender más de este manual."
        )
        return messages.random()
    }
}