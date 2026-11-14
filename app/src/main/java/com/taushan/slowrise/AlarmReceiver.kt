package com.taushan.slowrise

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {

        val preferences = context.getSharedPreferences(
            "slowrise_settings",
            Context.MODE_PRIVATE
        )

        val alarmEnabled = preferences.getBoolean("alarm_enabled", false)

        if (!alarmEnabled) return

        val hour = preferences.getInt("alarm_hour", 8)
        val minute = preferences.getInt("alarm_minute", 0)

        // Сразу планируем будильник на следующий день
        AlarmScheduler.schedule(context, hour, minute)

        // Запускаем воспроизведение
        val serviceIntent = Intent(context, AudioService::class.java)
        ContextCompat.startForegroundService(context, serviceIntent)
    }
}