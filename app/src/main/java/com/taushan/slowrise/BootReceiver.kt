package com.taushan.slowrise

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            val preferences = context.getSharedPreferences(
                "slowrise_settings",
                Context.MODE_PRIVATE
            )

            val alarmEnabled = preferences.getBoolean("alarm_enabled", false)

            if (alarmEnabled) {
                val hour = preferences.getInt("alarm_hour", 8)
                val minute = preferences.getInt("alarm_minute", 0)

                AlarmScheduler.schedule(context, hour, minute)
            }
        }
    }
}