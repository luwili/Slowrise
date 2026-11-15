package com.taushan.slowrise

import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoundPickerSheet(
    selectedSound: AlarmSound,
    onSoundSelected: (AlarmSound) -> Unit,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val background = Color(0xFF161622)
    val cardColor = Color(0xFF202033)
    val selectedCardColor = Color(0xFF2D2A4A)
    val accent = Color(0xFFB49AFF)
    val textPrimary = Color.White
    val textSecondary = Color(0xFFA5A5BA)

    var previewPlayingId by remember { mutableStateOf<String?>(null) }
    var previewPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            previewPlayer?.stop()
            previewPlayer?.release()
            previewPlayer = null
        }
    }

    fun stopPreview() {
        try {
            if (previewPlayer?.isPlaying == true) {
                previewPlayer?.stop()
            }
        } catch (_: Exception) {
        }
        previewPlayer?.release()
        previewPlayer = null
        previewPlayingId = null
    }

    fun playPreview(sound: AlarmSound) {
        if (previewPlayingId == sound.id) {
            stopPreview()
            return
        }

        stopPreview()

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()

        try {
            val p = MediaPlayer.create(context, sound.resId, audioAttributes, 0)
            if (p != null) {
                p.isLooping = true
                p.setVolume(0.8f, 0.8f)
                p.start()
                previewPlayer = p
                previewPlayingId = sound.id
            } else {
                previewPlayer = null
                previewPlayingId = null
            }
        } catch (_: Exception) {
            previewPlayer = null
            previewPlayingId = null
        }
    }

    ModalBottomSheet(
        onDismissRequest = {
            stopPreview()
            onDismissRequest()
        },
        sheetState = sheetState,
        containerColor = background,
        tonalElevation = 8.dp,
        dragHandle = {
            BottomSheetDefaults.DragHandle(color = Color(0xFF45455E))
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ЗВУК БУДИЛЬНИКА",
                        color = accent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Выберите мелодию",
                        color = textPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                TextButton(
                    onClick = {
                        stopPreview()
                        onDismissRequest()
                    }
                ) {
                    Text("Готово", color = accent, fontWeight = FontWeight.Bold)
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 520.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(SoundRepository.sounds) { sound ->
                    val isSelected = sound.id == selectedSound.id
                    val isPreviewing = previewPlayingId == sound.id

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSoundSelected(sound)
                            },
                        shape = RoundedCornerShape(18.dp),
                        color = if (isSelected) selectedCardColor else cardColor,
                        border = if (isSelected) BorderStroke(1.5.dp, accent) else null
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .background(
                                        if (isSelected) accent.copy(alpha = 0.2f) else Color(0xFF2B2B40),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = sound.emoji,
                                    fontSize = 22.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = sound.title,
                                        modifier = Modifier.weight(1f),
                                        color = textPrimary,
                                        fontSize = 15.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        modifier = Modifier.widthIn(max = 82.dp),
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFF33334D)
                                    ) {
                                        Text(
                                            text = sound.category,
                                            color = accent,
                                            fontSize = 10.sp,
                                            maxLines = 1,
                                            softWrap = false,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = sound.description,
                                    color = textSecondary,
                                    fontSize = 12.sp,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            IconButton(
                                onClick = { playPreview(sound) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Text(
                                    text = if (isPreviewing) "⏸" else "▶",
                                    color = if (isPreviewing) accent else Color.White,
                                    fontSize = 16.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(4.dp))

                            RadioButton(
                                selected = isSelected,
                                onClick = { onSoundSelected(sound) },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = accent,
                                    unselectedColor = Color(0xFF555570)
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
