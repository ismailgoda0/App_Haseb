package com.homecash.core.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.homecash.MainActivity
import com.homecash.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/** Local in-app history plus Android system notifications with sound, vibration and lockscreen badges. */
data class AppNotification(
    val id: String,
    val title: String,
    val message: String,
    val category: String,
    val createdAt: Long,
    val read: Boolean
) {
    fun timeLabel(): String = SimpleDateFormat("dd/MM  HH:mm", Locale.forLanguageTag("ar-EG")).format(Date(createdAt))
}

object AppNotificationCenter {
    private const val PREFS = "homecash_notification_history"
    private const val KEY_ITEMS = "items"
    const val CHANNEL_ID = "homecash_operations_channel"
    private const val MAX_ITEMS = 100
    @Volatile private var appContext: Context? = null
    private val _items = MutableStateFlow<List<AppNotification>>(emptyList())
    val items: StateFlow<List<AppNotification>> = _items

    fun initialize(context: Context) {
        val app = context.applicationContext
        appContext = app
        createChannel(app)
        _items.value = readItems(app)
    }

    private fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttr = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()

            val channel = NotificationChannel(
                CHANNEL_ID,
                "إشعارات المعاملات وتدقيق المحافظ (HomeCash)",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "تنبيهات فورية عند وصول أو إرسال كاش وتدقيق الفروقات"
                enableLights(true)
                lightColor = android.graphics.Color.BLUE
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 150, 100, 150)
                setSound(soundUri, audioAttr)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            manager?.createNotificationChannel(channel)
        }
    }

    fun post(title: String, message: String, category: String = "عملية") {
        val context = appContext ?: return
        val item = AppNotification(UUID.randomUUID().toString(), title, message, category, System.currentTimeMillis(), false)
        val updated = (listOf(item) + _items.value).take(MAX_ITEMS)
        _items.value = updated
        saveItems(context, updated)

        val granted = Build.VERSION.SDK_INT < 33 || ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        if (granted && NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                putExtra("open_notifications", true)
            }
            val pending = PendingIntent.getActivity(context, 1107, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setContentIntent(pending)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setVibrate(longArrayOf(0, 150, 100, 150))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .build()

            try {
                NotificationManagerCompat.from(context).notify(item.id.hashCode(), notification)
            } catch (_: SecurityException) {}
        }
    }

    fun markAllRead() {
        val context = appContext ?: return
        val updated = _items.value.map { it.copy(read = true) }
        _items.value = updated
        saveItems(context, updated)
    }

    private fun readItems(context: Context): List<AppNotification> {
        val sp = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val raw = sp.getString(KEY_ITEMS, null) ?: return emptyList()
        val list = mutableListOf<AppNotification>()
        return try {
            val arr = JSONArray(raw)
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                list.add(
                    AppNotification(
                        o.getString("id"),
                        o.getString("title"),
                        o.getString("message"),
                        o.optString("category", "عام"),
                        o.getLong("createdAt"),
                        o.optBoolean("read", true)
                    )
                )
            }
            list
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun saveItems(context: Context, items: List<AppNotification>) {
        val sp = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val arr = JSONArray()
        items.forEach { itm ->
            arr.put(
                JSONObject().apply {
                    put("id", itm.id)
                    put("title", itm.title)
                    put("message", itm.message)
                    put("category", itm.category)
                    put("createdAt", itm.createdAt)
                    put("read", itm.read)
                }
            )
        }
        sp.edit().putString(KEY_ITEMS, arr.toString()).apply()
    }
}
