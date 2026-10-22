package com.homecash.core.common

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateFormatter {

    private val timeFormat = SimpleDateFormat("hh:mm a", Locale("ar"))
    private val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("ar"))
    private val fullFormat = SimpleDateFormat("dd/MM/yyyy - hh:mm a", Locale("ar"))

    fun formatTime(timestamp: Long): String {
        return timeFormat.format(Date(timestamp))
    }

    fun formatDate(timestamp: Long): String {
        return dateFormat.format(Date(timestamp))
    }

    fun formatFull(timestamp: Long): String {
        return fullFormat.format(Date(timestamp))
    }

    fun formatRelative(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        val minutes = diff / 60000
        val hours = diff / 3600000

        return when {
            minutes < 1 -> "الآن"
            minutes < 60 -> "منذ $minutes دقيقة"
            hours < 24 -> "منذ $hours ساعة"
            hours < 48 -> "أمس"
            else -> formatDate(timestamp)
        }
    }
}
