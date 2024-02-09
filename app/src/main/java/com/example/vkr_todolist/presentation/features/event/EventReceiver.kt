package com.example.vkr_todolist.presentation.features.event

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.vkr_todolist.R
import com.example.vkr_todolist.presentation.main.MainActivity
import com.example.vkr_todolist.presentation.utils.Constants

class EventReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val eventId = intent?.getIntExtra(Constants.EVENT_ID, 0)
        val eventTitle = intent?.getStringExtra(Constants.EVENT_TITLE)
        val description = context?.getString(R.string.notification_desc)

        val notificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val tapResultIntent = Intent(context, MainActivity::class.java)
        tapResultIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            0,
            tapResultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder =
            NotificationCompat.Builder(context, Constants.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(eventTitle)
                .setContentText(description)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

        notificationManager.notify(eventId!!, notificationBuilder.build())
    }
}