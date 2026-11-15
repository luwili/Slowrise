
package com.taushan.slowrise

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.media.AudioAttributes
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.SystemClock
import androidx.core.app.NotificationCompat

class AudioService : Service() {

    private var player: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())

    private var startTime = 0L

    private val fadeRunnable = object : Runnable {
        override fun run() {
            val elapsed = SystemClock.elapsedRealtime() - startTime

            val progress = (elapsed / 1_800_000f).coerceIn(0f, 1f)
            val volume = 0.05f + 0.95f * progress

            player?.setVolume(volume, volume)

            if (progress < 1f) {
                handler.postDelayed(this, 1000)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        if (intent?.action == "STOP_ALARM") {
            stopSelf()
            return START_NOT_STICKY
        }

        startForeground(2001, createNotification())

        if (player == null) {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()

            val selectedSound = SoundRepository.getSelectedSound(this)

            player = MediaPlayer.create(
                this,
                selectedSound.resId,
                audioAttributes,
                0
            )

            player?.isLooping = true
            player?.setVolume(0.05f, 0.05f)
            player?.start()

            startTime = SystemClock.elapsedRealtime()
            handler.post(fadeRunnable)
        }

        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "slowrise_alarm",
                "Slowrise Alarm",
                NotificationManager.IMPORTANCE_LOW
            )

            val manager =
                getSystemService(NotificationManager::class.java)

            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val selectedSound = SoundRepository.getSelectedSound(this)
        val subtitle = "Плавное пробуждение • ${selectedSound.emoji} ${selectedSound.title}"

        val stopIntent = Intent(this, AudioService::class.java).apply {
            action = "STOP_ALARM"
        }

        val stopPendingIntent = android.app.PendingIntent.getService(
            this,
            2002,
            stopIntent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or
                    android.app.PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, "slowrise_alarm")
            .setContentTitle("Slowrise")
            .setContentText(subtitle)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setOngoing(true)
            .addAction(
                android.R.drawable.ic_media_pause,
                "Остановить",
                stopPendingIntent
            )
            .build()
    }

    override fun onDestroy() {
        handler.removeCallbacks(fadeRunnable)

        try {
            if (player?.isPlaying == true) {
                player?.stop()
            }
        } catch (_: Exception) {
        }
        player?.release()
        player = null

        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
