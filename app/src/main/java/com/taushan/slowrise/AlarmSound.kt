package com.taushan.slowrise

import android.content.Context
import androidx.annotation.RawRes

data class AlarmSound(
    val id: String,
    val title: String,
    val description: String,
    val emoji: String,
    val category: String,
    @param:RawRes val resId: Int
)

object SoundRepository {
    const val PREF_KEY_SOUND_ID = "selected_sound_id"
    const val DEFAULT_SOUND_ID = "slowrise_classic"

    val sounds: List<AlarmSound> = listOf(
        AlarmSound(
            id = "slowrise_classic",
            title = "Slowrise Classic",
            description = "Оригинальная гармония пробуждения",
            emoji = "🌅",
            category = "Эмбиент",
            resId = R.raw.slowrise_audio
        ),
        AlarmSound(
            id = "sound_birds",
            title = "Утренний лес и птицы",
            description = "Пение птиц и шелест крон на рассвете",
            emoji = "🐦",
            category = "Природа",
            resId = R.raw.sound_birds
        ),
        AlarmSound(
            id = "sound_rain",
            title = "Спокойный дождь",
            description = "Мягкий медитативный шум теплого дождя",
            emoji = "🌧️",
            category = "Дождь",
            resId = R.raw.sound_rain
        ),
        AlarmSound(
            id = "sound_waves",
            title = "Океанский прибой",
            description = "Мерные умиротворяющие морские волны",
            emoji = "🌊",
            category = "Вода",
            resId = R.raw.sound_waves
        ),
        AlarmSound(
            id = "sound_stream",
            title = "Горный ручей",
            description = "Живой звонкий бег чистой воды",
            emoji = "🏞️",
            category = "Вода",
            resId = R.raw.sound_stream
        ),
        AlarmSound(
            id = "sound_fireplace",
            title = "Уютный камин",
            description = "Теплое потрескивание сухих поленьев",
            emoji = "🔥",
            category = "Уют",
            resId = R.raw.sound_fireplace
        ),
        AlarmSound(
            id = "sound_summer_night",
            title = "Летняя ночь",
            description = "Тихое стрекотание сверчков и ветерок",
            emoji = "🦗",
            category = "Природа",
            resId = R.raw.sound_summer_night
        ),
        AlarmSound(
            id = "sound_wind",
            title = "Шепот ветра",
            description = "Ласковый ветерок среди сосен и трав",
            emoji = "🍃",
            category = "Природа",
            resId = R.raw.sound_wind
        ),
        AlarmSound(
            id = "sound_warm_piano",
            title = "Нежное фортепиано",
            description = "Мягкие теплые фортепианные аккорды",
            emoji = "🎹",
            category = "Музыка",
            resId = R.raw.sound_warm_piano
        ),
        AlarmSound(
            id = "sound_lofi_beats",
            title = "Лоу-фай рассвет",
            description = "Спокойные расслабляющие ритмы для подъема",
            emoji = "🎧",
            category = "Музыка",
            resId = R.raw.sound_lofi_beats
        ),
        AlarmSound(
            id = "sound_ambient_music",
            title = "Медитативный эмбиент",
            description = "Воздушные синтезаторные переливы и покой",
            emoji = "✨",
            category = "Эмбиент",
            resId = R.raw.sound_ambient_music
        ),
        AlarmSound(
            id = "sound_acoustic_guitar",
            title = "Акустическая гитара",
            description = "Уютные переборы акустических струн",
            emoji = "🎸",
            category = "Музыка",
            resId = R.raw.sound_acoustic_guitar
        ),
        AlarmSound(
            id = "sound_forest",
            title = "Сосновый бор",
            description = "Глубокое дыхание лесной чащи и ветра",
            emoji = "🌲",
            category = "Природа",
            resId = R.raw.sound_forest
        ),
        AlarmSound(
            id = "sound_storm",
            title = "Далекая гроза",
            description = "Глухие раскаты грома и умиротворяющий ливень",
            emoji = "⛈️",
            category = "Дождь",
            resId = R.raw.sound_storm
        ),
        AlarmSound(
            id = "sound_boat",
            title = "Лодка у причала",
            description = "Тихий плеск воды и скрип канатов",
            emoji = "⛵",
            category = "Вода",
            resId = R.raw.sound_boat
        ),
        AlarmSound(
            id = "sound_train",
            title = "Поезд сквозь рассвет",
            description = "Ритмичный убаюкивающий стук колес",
            emoji = "🚂",
            category = "Эмбиент",
            resId = R.raw.sound_train
        )
    )

    fun getSoundById(id: String?): AlarmSound {
        return sounds.find { it.id == id } ?: sounds.first()
    }

    fun getSelectedSound(context: Context): AlarmSound {
        val prefs = context.getSharedPreferences("slowrise_settings", Context.MODE_PRIVATE)
        val soundId = prefs.getString(PREF_KEY_SOUND_ID, DEFAULT_SOUND_ID)
        return getSoundById(soundId)
    }

    fun setSelectedSound(context: Context, sound: AlarmSound) {
        val prefs = context.getSharedPreferences("slowrise_settings", Context.MODE_PRIVATE)
        prefs.edit().putString(PREF_KEY_SOUND_ID, sound.id).apply()
    }
}
