
package com.taushan.slowrise

import android.media.MediaPlayer
import android.media.AudioAttributes
import android.os.Bundle
import android.app.AlarmManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.content.Context
import android.app.TimePickerDialog
import java.util.Calendar
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taushan.slowrise.ui.theme.SlowriseTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SlowriseTheme {
                SlowriseScreen()
            }
        }
    }
}

@Composable
fun SlowriseScreen() {
    val background = Color(0xFF11111B)
    val cardColor = Color(0xFF202033)
    val accent = Color(0xFFB49AFF)

    val context = LocalContext.current
    val alarmManager = remember {
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }


    val preferences = remember {
        context.getSharedPreferences("slowrise_settings", Context.MODE_PRIVATE)
    }
    val scope = rememberCoroutineScope()

    var isPlaying by remember { mutableStateOf(false) }
    val calendar = Calendar.getInstance()

    var alarmHour by remember {
        mutableIntStateOf(preferences.getInt("alarm_hour", 8))
    }

    var alarmMinute by remember {
        mutableIntStateOf(preferences.getInt("alarm_minute", 0))
    }

    var showTimePicker by remember { mutableStateOf(false) }
    var alarmEnabled by remember {
        mutableStateOf(
            preferences.getBoolean("alarm_enabled", false)
        )
    }
    val exactAlarmPermissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (
                Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
                alarmManager.canScheduleExactAlarms()
            ) {
                alarmEnabled = true

                preferences.edit()
                    .putBoolean("alarm_enabled", true)
                    .apply()

                AlarmScheduler.schedule(
                    context,
                    alarmHour,
                    alarmMinute
                )
            }
        }
    var volumePercent by remember { mutableIntStateOf(0) }
    var fadeJob by remember { mutableStateOf<Job?>(null) }

    LaunchedEffect(showTimePicker) {
        if (showTimePicker) {
            TimePickerDialog(
                context,
                { _, hour, minute ->
                    alarmHour = hour
                    alarmMinute = minute

                    preferences.edit()
                        .putInt("alarm_hour", hour)
                        .putInt("alarm_minute", minute)
                        .apply()

                    if (alarmEnabled) {
                        AlarmScheduler.schedule(context, hour, minute)
                    }

                    showTimePicker = false
                },
                alarmHour,
                alarmMinute,
                true
            ).apply {
                setOnDismissListener {
                    showTimePicker = false
                }
            }.show()
        }
    }
    val player = remember {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()

        MediaPlayer.create(
            context,
            R.raw.slowrise_audio,
            audioAttributes,
            0
        )
    }

    DisposableEffect(player) {
        player.setOnCompletionListener {
            isPlaying = false
            fadeJob?.cancel()
            volumePercent = 0
        }

        onDispose {
            fadeJob?.cancel()
            player.release()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "SLOWRISE",
                    color = accent,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Доброе утро",
                    color = Color.White,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = "Просыпайся постепенно",
                    color = Color.LightGray,
                    fontSize = 15.sp
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = cardColor
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(26.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "ВРЕМЯ ПРОБУЖДЕНИЯ",
                        color = Color.LightGray,
                        fontSize = 12.sp,
                        letterSpacing = 2.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "%02d:%02d".format(alarmHour, alarmMinute),
                        modifier = Modifier.clickable {
                            showTimePicker = true
                        },
                        color = Color.White,
                        fontSize = 64.sp,
                        fontWeight = FontWeight.Light
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Будильник",
                            color = Color.White,
                            fontSize = 16.sp
                        )

                        Switch(
                            checked = alarmEnabled,

                            onCheckedChange = { enabled ->
                                if (enabled) {
                                    if (
                                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                                        !alarmManager.canScheduleExactAlarms()
                                    ) {
                                        val intent = Intent(
                                            Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                                            Uri.parse("package:${context.packageName}")
                                        )
                                        exactAlarmPermissionLauncher.launch(intent)
                                    } else {
                                        alarmEnabled = true

                                        preferences.edit()
                                            .putBoolean("alarm_enabled", true)
                                            .apply()

                                        AlarmScheduler.schedule(
                                            context,
                                            alarmHour,
                                            alarmMinute
                                        )
                                    }
                                } else {
                                    alarmEnabled = false

                                    preferences.edit()
                                        .putBoolean("alarm_enabled", false)
                                        .apply()

                                    AlarmScheduler.cancel(context)

                                    val stopIntent = Intent(context, AudioService::class.java)
                                    context.stopService(stopIntent)
                                }
                            }

                        )
                    }
                }
            }

            Column {
                Text(
                    text = "Плавное пробуждение",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Громкость: $volumePercent%",
                    color = accent,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                LinearProgressIndicator(
                    progress = { volumePercent / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = accent,
                    trackColor = cardColor
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (isPlaying) {
                            fadeJob?.cancel()
                            player.pause()
                            isPlaying = false
                        } else {
                            if (player.currentPosition >= player.duration) {
                                player.seekTo(0)
                            }

                            volumePercent = 5
                            player.setVolume(0.05f, 0.05f)
                            player.start()
                            isPlaying = true

                            fadeJob = scope.launch {
                                for (step in 1..100) {
                                    delay(300)
                                    val volume = 0.05f + (0.95f * step / 100f)
                                    player.setVolume(volume, volume)
                                    volumePercent = (volume * 100).toInt()
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accent
                    )
                ) {
                    Text(
                        text = if (isPlaying) "Пауза" else "Воспроизвести",
                        color = Color(0xFF11111B),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
