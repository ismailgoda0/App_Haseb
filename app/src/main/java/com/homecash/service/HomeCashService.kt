package com.homecash.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.homecash.MainActivity

/**
 * HomeCashService - Persistent Foreground Service
 * Keeps the SMS listening engine and real-time ledger monitoring alive in the background
 * across Android 12, 13, 14+ Doze Mode and power management without being killed.
 */
class HomeCashService : Service() {

    companion object {
        private const val NOTIFICATION_ID = 9901
        private const val CHANNEL_ID = "homecash_foreground_service_channel"

        fun start(context: Context) {
            val intent = Intent(context, HomeCashService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, HomeCashService::class.java)
            context.stopService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        val notification = buildForegroundNotification()
        startForeground(NOTIFICATION_ID, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "خدمة مراقبة المحافظ (HomeCash Background)",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "مراقبة وتدقيق رسائل الـ SMS للمحافظ وتحديث الأرصدة اللحظية"
                setShowBadge(false)
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            manager?.createNotificationChannel(channel)
        }
    }

    private fun buildForegroundNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setContentTitle("دفتر المحافظ — HomeCash")
            .setContentText("المراقبة اللحظية لرسائل ومحافظ الكاشير نشطة")
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
}
