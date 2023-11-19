package com.example.vkr_todolist.ui.alarm

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.vkr_todolist.R
import com.example.vkr_todolist.data.model.PomodoroTimer
import com.example.vkr_todolist.utils.Constants

class PomodoroReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // Получаем данные из намерения (intent)
        val title = intent?.getStringExtra("title")
        val message = intent?.getStringExtra("msg")

        // Создаем уведомление
        val notificationBuilder = NotificationCompat.Builder(context!!, Constants.CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notificationBuilder.build())
    }
}