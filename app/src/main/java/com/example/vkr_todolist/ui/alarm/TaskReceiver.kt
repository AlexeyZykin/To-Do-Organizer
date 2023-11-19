package com.example.vkr_todolist.ui.alarm

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.example.vkr_todolist.R
import com.example.vkr_todolist.ui.main.MainActivity
import com.example.vkr_todolist.ui.task.TaskFragment
import com.example.vkr_todolist.utils.Constants


class TaskReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val taskId = intent?.getIntExtra(Constants.TASK_ID, 0)
        val title = intent?.getStringExtra(Constants.TASK_TITLE)
        var description = intent?.getStringExtra(Constants.TASK_DESCRIPTION)
        if(description=="")
            description=context?.getString(R.string.notification_desc)

        val notificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val tapResultIntent = Intent(context, MainActivity::class.java)
        tapResultIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent: PendingIntent = getActivity( context,0,tapResultIntent,FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE)

//        val pendingIntent = NavDeepLinkBuilder(context)
//            .setGraph(R.navigation.nav_graph)
//            .setDestination(R.id.taskFragment)
//            .setArguments(Bundle().apply {
//                putInt(Constants.TASK_ID, taskId!!)
//            })
//            .createPendingIntent()


        // Создаем уведомление
        val notificationBuilder =
        NotificationCompat.Builder(context, Constants.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        // Отображаем уведомление
        notificationManager.notify(taskId!!, notificationBuilder.build())
    }
}