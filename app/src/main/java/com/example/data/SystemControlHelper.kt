package com.example.data

import android.app.Activity
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.provider.AlarmClock
import android.provider.CalendarContract
import android.provider.MediaStore
import android.provider.Settings
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object SystemControlHelper {

    suspend fun loadInstalledApps(context: Context): List<InstalledApp> = withContext(Dispatchers.IO) {
        val pm = context.packageManager
        val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val resolveInfos = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.queryIntentActivities(mainIntent, PackageManager.ResolveInfoFlags.of(0L))
        } else {
            @Suppress("DEPRECATION")
            pm.queryIntentActivities(mainIntent, 0)
        }

        val selfPackage = context.packageName
        val seenPackages = HashSet<String>()
        val results = ArrayList<InstalledApp>(resolveInfos.size)

        for (info in resolveInfos) {
            val pkg = info.activityInfo?.packageName ?: continue
            if (pkg == selfPackage || !seenPackages.add(pkg)) continue

            val label = runCatching { info.loadLabel(pm).toString().trim() }
                .getOrDefault(pkg)
                .ifBlank { pkg }

            val appInfo = info.activityInfo.applicationInfo
            val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
            val category = classifyApp(appInfo, pkg, label)
            val bitmap = runCatching { drawableToImageBitmap(info.loadIcon(pm)) }.getOrNull()

            results.add(
                InstalledApp(
                    packageName = pkg,
                    label = label,
                    category = category,
                    icon = bitmap,
                    isSystemApp = isSystem
                )
            )
        }

        results.sortedBy { it.label.lowercase() }
    }

    private fun classifyApp(
        appInfo: ApplicationInfo,
        pkg: String,
        label: String
    ): AppCategory {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            when (appInfo.category) {
                ApplicationInfo.CATEGORY_SOCIAL -> return AppCategory.SOCIAL
                ApplicationInfo.CATEGORY_AUDIO,
                ApplicationInfo.CATEGORY_VIDEO,
                ApplicationInfo.CATEGORY_GAME -> return AppCategory.MEDIA
                ApplicationInfo.CATEGORY_IMAGE -> return AppCategory.CREATIVITY
                ApplicationInfo.CATEGORY_PRODUCTIVITY,
                ApplicationInfo.CATEGORY_NEWS -> return AppCategory.PRODUCTIVITY
                ApplicationInfo.CATEGORY_MAPS -> return AppCategory.UTILITIES
                ApplicationInfo.CATEGORY_ACCESSIBILITY -> return AppCategory.SYSTEM
            }
        }
        val key = "$pkg ${label.lowercase()}"
        return when {
            key.contains("messag") || key.contains("chat") || key.contains("dialer") ||
                key.contains("phone") || key.contains("contacts") || key.contains("mail") ||
                key.contains("social") || key.contains("whatsapp") || key.contains("telegram") -> AppCategory.SOCIAL
            key.contains("music") || key.contains("youtube") || key.contains("video") ||
                key.contains("spotify") || key.contains("player") || key.contains("tv") ||
                key.contains("game") -> AppCategory.MEDIA
            key.contains("camera") || key.contains("photo") || key.contains("gallery") ||
                key.contains("draw") || key.contains("edit") -> AppCategory.CREATIVITY
            key.contains("docs") || key.contains("drive") || key.contains("calendar") ||
                key.contains("notes") || key.contains("keep") || key.contains("office") ||
                key.contains("chrome") || key.contains("browser") -> AppCategory.PRODUCTIVITY
            key.contains("settings") || key.contains("setup") || key.contains("system") -> AppCategory.SYSTEM
            key.contains("calc") || key.contains("clock") || key.contains("files") ||
                key.contains("map") || key.contains("weather") || key.contains("tool") -> AppCategory.UTILITIES
            else -> AppCategory.OTHER
        }
    }

    private fun drawableToImageBitmap(drawable: Drawable): ImageBitmap? {
        if (drawable is BitmapDrawable && drawable.bitmap != null) {
            val bmp = drawable.bitmap
            val scaled = if (bmp.width > 128 || bmp.height > 128) {
                Bitmap.createScaledBitmap(bmp, 128, 128, true)
            } else {
                bmp
            }
            return scaled.asImageBitmap()
        }
        val size = 128
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap.asImageBitmap()
    }

    fun launchInstalledApp(context: Context, packageName: String): Boolean {
        return try {
            val intent = context.packageManager.getLaunchIntentForPackage(packageName) ?: return false
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            true
        } catch (_: Exception) {
            false
        }
    }

    fun openAppInfo(context: Context, packageName: String) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        safeStartActivity(context, intent)
    }

    fun openWifiSettings(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val panelIntent = Intent(Settings.Panel.ACTION_WIFI).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            if (safeStartActivity(context, panelIntent)) return
        }
        safeStartActivity(
            context,
            Intent(Settings.ACTION_WIFI_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    fun openBluetoothSettings(context: Context) {
        safeStartActivity(
            context,
            Intent(Settings.ACTION_BLUETOOTH_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    fun openDisplaySettings(context: Context) {
        safeStartActivity(
            context,
            Intent(Settings.ACTION_DISPLAY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    fun openSoundSettings(context: Context) {
        safeStartActivity(
            context,
            Intent(Settings.ACTION_SOUND_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    fun openAirplaneSettings(context: Context) {
        safeStartActivity(
            context,
            Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    fun openHomeLauncherSettings(context: Context) {
        val intent = Intent(Settings.ACTION_HOME_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (!safeStartActivity(context, intent)) {
            openSystemSettings(context)
        }
    }

    fun openNotificationSettings(context: Context) {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            }
        } else {
            Intent(Settings.ACTION_SETTINGS)
        }
        safeStartActivity(context, intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    fun openWriteSettingsPermission(context: Context) {
        val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS).apply {
            data = Uri.parse("package:${context.packageName}")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        if (!safeStartActivity(context, intent)) {
            openDisplaySettings(context)
        }
    }

    fun openSystemSettings(context: Context) {
        safeStartActivity(
            context,
            Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    fun openDialer(context: Context): Boolean =
        safeStartActivity(context, Intent(Intent.ACTION_DIAL).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))

    fun openMessages(context: Context): Boolean {
        val messaging = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_MESSAGING)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (safeStartActivity(context, messaging)) return true
        val smsFallback = Intent(Intent.ACTION_VIEW, Uri.parse("sms:")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return safeStartActivity(context, smsFallback)
    }

    fun openBrowser(context: Context, query: String? = null): Boolean {
        val url = if (query.isNullOrBlank()) {
            "https://www.google.com"
        } else {
            "https://www.google.com/search?q=${Uri.encode(query)}"
        }
        return safeStartActivity(
            context,
            Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    fun openCamera(context: Context): Boolean {
        val stillCamera = Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (safeStartActivity(context, stillCamera)) return true
        return safeStartActivity(
            context,
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    fun openGallery(context: Context): Boolean {
        val gallery = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_GALLERY)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (safeStartActivity(context, gallery)) return true
        val viewImages = Intent(Intent.ACTION_VIEW).apply {
            type = "image/*"
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return safeStartActivity(context, viewImages)
    }

    fun openClockAlarms(context: Context): Boolean =
        safeStartActivity(
            context,
            Intent(AlarmClock.ACTION_SHOW_ALARMS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )

    fun openCalendar(context: Context): Boolean {
        val calIntent = Intent(Intent.ACTION_VIEW, CalendarContract.CONTENT_URI.buildUpon().appendPath("time").build())
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (safeStartActivity(context, calIntent)) return true
        return safeStartActivity(
            context,
            Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_CALENDAR)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    fun setTorchEnabled(context: Context, enabled: Boolean): Boolean {
        return try {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager ?: return false
            val torchCameraId = cameraManager.cameraIdList.firstOrNull { id ->
                val chars = cameraManager.getCameraCharacteristics(id)
                chars.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
            } ?: return false
            cameraManager.setTorchMode(torchCameraId, enabled)
            true
        } catch (_: Exception) {
            false
        }
    }

    fun applyBrightness(activity: Activity?, context: Context, fraction: Float) {
        val clamped = fraction.coerceIn(0.05f, 1.0f)
        activity?.let { act ->
            val attrs = act.window.attributes
            attrs.screenBrightness = clamped
            act.window.attributes = attrs
        }
        if (Settings.System.canWrite(context)) {
            runCatching {
                val intValue = (clamped * 255).toInt().coerceIn(10, 255)
                Settings.System.putInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS, intValue)
            }
        }
    }

    fun setMusicVolume(context: Context, fraction: Float) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return
        val max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val target = (fraction.coerceIn(0f, 1f) * max).toInt().coerceIn(0, max)
        runCatching {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, target, 0)
        }
    }

    fun readDeviceTelemetry(context: Context, currentWindowBrightness: Float?): DeviceTelemetry {
        val batteryStatus = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, 100) ?: 100
        val batteryPercent = if (level >= 0 && scale > 0) {
            ((level * 100f) / scale).toInt().coerceIn(0, 100)
        } else {
            val bm = context.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager
            bm?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)?.takeIf { it in 0..100 } ?: 85
        }
        val status = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
            status == BatteryManager.BATTERY_STATUS_FULL

        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val caps = cm?.activeNetwork?.let { cm.getNetworkCapabilities(it) }
        val wifiConnected = caps?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
        val cellularConnected = caps?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true
        val wifiLabel = when {
            wifiConnected -> "Connected"
            cellularConnected -> "Cellular"
            else -> "Offline"
        }

        val btEnabled = try {
            val btManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
            btManager?.adapter?.isEnabled == true
        } catch (_: SecurityException) {
            false
        } catch (_: Exception) {
            false
        }

        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
        val maxVol = audioManager?.getStreamMaxVolume(AudioManager.STREAM_MUSIC)?.coerceAtLeast(1) ?: 15
        val curVol = audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC) ?: 8
        val volumeFraction = curVol.toFloat() / maxVol.toFloat()
        val ringerLabel = when (audioManager?.ringerMode) {
            AudioManager.RINGER_MODE_SILENT -> "Silent"
            AudioManager.RINGER_MODE_VIBRATE -> "Vibrate"
            else -> "Ring"
        }

        val systemBrightness = runCatching {
            Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS) / 255f
        }.getOrDefault(0.72f)
        val brightness = (currentWindowBrightness?.takeIf { it in 0f..1f } ?: systemBrightness).coerceIn(0.05f, 1f)

        val statFs = StatFs(Environment.getDataDirectory().path)
        val totalGb = statFs.totalBytes / (1024.0 * 1024.0 * 1024.0)
        val freeGb = statFs.availableBytes / (1024.0 * 1024.0 * 1024.0)

        val model = "${Build.MANUFACTURER.replaceFirstChar { it.uppercase() }} ${Build.MODEL}".trim()

        return DeviceTelemetry(
            batteryPercent = batteryPercent,
            isCharging = isCharging,
            wifiConnected = wifiConnected,
            wifiLabel = wifiLabel,
            bluetoothEnabled = btEnabled,
            ringerModeLabel = ringerLabel,
            brightnessFraction = brightness,
            volumeFraction = volumeFraction,
            maxVolume = maxVol,
            deviceModel = model,
            androidVersion = Build.VERSION.RELEASE ?: "Android",
            sdkInt = Build.VERSION.SDK_INT,
            freeStorageGb = freeGb,
            totalStorageGb = totalGb
        )
    }

    private fun safeStartActivity(context: Context, intent: Intent): Boolean {
        return try {
            context.startActivity(intent)
            true
        } catch (_: Exception) {
            false
        }
    }
}
