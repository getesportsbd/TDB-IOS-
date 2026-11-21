package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.tdbDataStore: DataStore<Preferences> by preferencesDataStore(name = "tdb_ios_prefs")

class PreferencesRepository(private val context: Context) {

    private object Keys {
        val WALLPAPER_ID = stringPreferencesKey("wallpaper_id")
        val CUSTOM_WALLPAPER_URI = stringPreferencesKey("custom_wallpaper_uri")
        val GRID_COLUMNS = intPreferencesKey("grid_columns")
        val SHOW_ICON_LABELS = booleanPreferencesKey("show_icon_labels")
        val START_ON_LOCK_SCREEN = booleanPreferencesKey("start_on_lock_screen")
        val LIGHT_TEXT_OVER_WALLPAPER = booleanPreferencesKey("light_text_over_wallpaper")
        val HAPTIC_FEEDBACK = booleanPreferencesKey("haptic_feedback")
        val REMINDERS_SERIALIZED = stringPreferencesKey("reminders_serialized")
        val QUICK_NOTE_TEXT = stringPreferencesKey("quick_note_text")
    }

    val settingsFlow: Flow<LauncherSettings> = context.tdbDataStore.data.map { prefs ->
        val presetId = prefs[Keys.WALLPAPER_ID] ?: WallpaperPreset.AURORA.id
        val preset = WallpaperPreset.entries.firstOrNull { it.id == presetId } ?: WallpaperPreset.AURORA
        LauncherSettings(
            wallpaperPreset = preset,
            customWallpaperUri = prefs[Keys.CUSTOM_WALLPAPER_URI],
            gridColumns = (prefs[Keys.GRID_COLUMNS] ?: 4).coerceIn(4, 5),
            showIconLabels = prefs[Keys.SHOW_ICON_LABELS] ?: true,
            startOnLockScreen = prefs[Keys.START_ON_LOCK_SCREEN] ?: false,
            lightTextOverWallpaper = prefs[Keys.LIGHT_TEXT_OVER_WALLPAPER] ?: !preset.preferDarkTextOnHome,
            hapticFeedback = prefs[Keys.HAPTIC_FEEDBACK] ?: true
        )
    }

    val remindersFlow: Flow<List<ReminderItem>> = context.tdbDataStore.data.map { prefs ->
        val raw = prefs[Keys.REMINDERS_SERIALIZED]
        if (raw == null) {
            defaultReminders()
        } else if (raw.isBlank()) {
            emptyList()
        } else {
            raw.split("\n").mapNotNull { line ->
                val parts = line.split("|", limit = 3)
                if (parts.size == 3) {
                    ReminderItem(
                        id = parts[0],
                        isDone = parts[1] == "1",
                        title = parts[2]
                    )
                } else null
            }
        }
    }

    val quickNoteFlow: Flow<String> = context.tdbDataStore.data.map { prefs ->
        prefs[Keys.QUICK_NOTE_TEXT]
            ?: "Welcome to TDB iOS Experience 2.0\n\n• Swipe horizontally for App Library\n• Tap Dynamic Island to expand live controls\n• Customize wallpapers with Photo Picker"
    }

    suspend fun selectWallpaperPreset(preset: WallpaperPreset) {
        context.tdbDataStore.edit { prefs ->
            prefs[Keys.WALLPAPER_ID] = preset.id
            prefs[Keys.LIGHT_TEXT_OVER_WALLPAPER] = !preset.preferDarkTextOnHome
        }
    }

    suspend fun setCustomWallpaperUri(uriString: String) {
        context.tdbDataStore.edit { prefs ->
            prefs[Keys.WALLPAPER_ID] = WallpaperPreset.CUSTOM.id
            prefs[Keys.CUSTOM_WALLPAPER_URI] = uriString
            prefs[Keys.LIGHT_TEXT_OVER_WALLPAPER] = true
        }
    }

    suspend fun setGridColumns(columns: Int) {
        context.tdbDataStore.edit { prefs ->
            prefs[Keys.GRID_COLUMNS] = columns.coerceIn(4, 5)
        }
    }

    suspend fun setShowIconLabels(show: Boolean) {
        context.tdbDataStore.edit { prefs ->
            prefs[Keys.SHOW_ICON_LABELS] = show
        }
    }

    suspend fun setStartOnLockScreen(enabled: Boolean) {
        context.tdbDataStore.edit { prefs ->
            prefs[Keys.START_ON_LOCK_SCREEN] = enabled
        }
    }

    suspend fun setLightTextOverWallpaper(enabled: Boolean) {
        context.tdbDataStore.edit { prefs ->
            prefs[Keys.LIGHT_TEXT_OVER_WALLPAPER] = enabled
        }
    }

    suspend fun setHapticFeedback(enabled: Boolean) {
        context.tdbDataStore.edit { prefs ->
            prefs[Keys.HAPTIC_FEEDBACK] = enabled
        }
    }

    suspend fun saveReminders(items: List<ReminderItem>) {
        context.tdbDataStore.edit { prefs ->
            prefs[Keys.REMINDERS_SERIALIZED] = items.joinToString("\n") { item ->
                val safeTitle = item.title.replace("\n", " ").replace("|", "/")
                "${item.id}|${if (item.isDone) "1" else "0"}|$safeTitle"
            }
        }
    }

    suspend fun saveQuickNote(text: String) {
        context.tdbDataStore.edit { prefs ->
            prefs[Keys.QUICK_NOTE_TEXT] = text
        }
    }

    private fun defaultReminders(): List<ReminderItem> = listOf(
        ReminderItem("r1", "Try Control Center sliders", false),
        ReminderItem("r2", "Customize Home wallpaper", false),
        ReminderItem("r3", "Explore App Library folders", true)
    )
}
