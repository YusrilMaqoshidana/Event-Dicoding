package id.usereal.eventdicoding.utils

import java.text.SimpleDateFormat
import java.util.Locale

class FormatDate {
    fun formatNotificationDateTime(input: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.getDefault())
        val date = inputFormat.parse(input)
        return date?.let { outputFormat.format(it) } ?: ""
    }
}