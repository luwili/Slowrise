package com.taushan.slowrise

import android.app.AlarmManager
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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

    var selectedSound by remember {
        mutableStateOf(SoundRepository.getSelectedSound(context))
    }
    var showSoundPicker by remember { mutableStateOf(false) }

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
    var player by remember { mutableStateOf<MediaPlayer?>(null) }

    // Reinitialize player when selectedSound changes
    LaunchedEffect(selectedSound) {
        fadeJob?.cancel()
        if (isPlaying) {
            isPlaying = false
            volumePercent = 0
        }
        try {
            if (player?.isPlaying == true) {
                player?.stop()
            }
        } catch (_: Exception) {
        }
        player?.release()

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()

        try {
            val newPlayer = MediaPlayer.create(
                context,
                selectedSound.resId,
                audioAttributes,
                0
            )
            newPlayer?.setOnCompletionListener {
                isPlaying = false
                fadeJob?.cancel()
                volumePercent = 0
            }
            player = newPlayer
        } catch (_: Exception) {
            player = null
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            fadeJob?.cancel()
            try {
                if (player?.isPlaying == true) {
                    player?.stop()
                }
            } catch (_: Exception) {
            }
            player?.release()
            player = null
        }
    }

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

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Column {
                Text(
                    text = "SLOWRISE",
                    color = accent,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

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

            // Alarm Time Card
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
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "ВРЕМЯ ПРОБУЖДЕНИЯ",
                        color = Color.LightGray,
                        fontSize = 12.sp,
                        letterSpacing = 2.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "%02d:%02d".format(alarmHour, alarmMinute),
                        modifier = Modifier.clickable {
                            showTimePicker = true
                        },
                        color = Color.White,
                        fontSize = 60.sp,
                        fontWeight = FontWeight.Light
                    )

                    Spacer(modifier = Modifier.height(14.dp))

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

            // Sound Selector Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showSoundPicker = true },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = cardColor
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .background(
                                    accent.copy(alpha = 0.15f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = selectedSound.emoji,
                                fontSize = 24.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = "МЕЛОДИЯ БУДИЛЬНИКА",
                                color = Color.LightGray,
                                fontSize = 11.sp,
                                letterSpacing = 2.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = selectedSound.title,
                                color = Color.White,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${selectedSound.category} • ${selectedSound.description}",
                                color = accent,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF2E2E46)
                    ) {
                        Text(
                            text = "Выбрать",
                            color = accent,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            // Preview Volume & Test Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = cardColor
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Тест плавного пробуждения",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${selectedSound.emoji} ${selectedSound.title}",
                                color = Color.LightGray,
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Text(
                            text = "$volumePercent%",
                            color = accent,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    LinearProgressIndicator(
                        progress = { volumePercent / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = accent,
                        trackColor = Color(0xFF2C2C40)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            val curPlayer = player ?: return@Button
                            if (isPlaying) {
                                fadeJob?.cancel()
                                try {
                                    curPlayer.pause()
                                } catch (_: Exception) {
                                }
                                isPlaying = false
                            } else {
                                try {
                                    if (curPlayer.currentPosition >= curPlayer.duration) {
                                        curPlayer.seekTo(0)
                                    }

                                    volumePercent = 5
                                    curPlayer.setVolume(0.05f, 0.05f)
                                    curPlayer.start()
                                    isPlaying = true

                                    fadeJob = scope.launch {
                                        for (step in 1..100) {
                                            delay(300)
                                            val volume = 0.05f + (0.95f * step / 100f)
                                            curPlayer.setVolume(volume, volume)
                                            volumePercent = (volume * 100).toInt()
                                        }
                                    }
                                } catch (_: Exception) {
                                    isPlaying = false
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = accent
                        )
                    ) {
                        Text(
                            text = if (isPlaying) "Пауза" else "Воспроизвести тест",
                            color = Color(0xFF11111B),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showSoundPicker) {
        SoundPickerSheet(
            selectedSound = selectedSound,
            onSoundSelected = { sound ->
                selectedSound = sound
                SoundRepository.setSelectedSound(context, sound)
            },
            onDismissRequest = {
                showSoundPicker = false
            }
        )
    }
}
