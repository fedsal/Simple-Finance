package org.fedsal.finance.ui.common

import kotlinx.datetime.LocalDate
import java.util.Calendar
import java.util.Locale
import java.text.SimpleDateFormat

actual fun getLocalizedMonthName(date: LocalDate, locale: String): String {
    val localeObject = Locale.forLanguageTag(locale)
    val calendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, date.year)
        set(Calendar.MONTH, date.monthNumber - 1) // Calendar es 0-based
        set(Calendar.DAY_OF_MONTH, 1)
    }

    val formatter = SimpleDateFormat("MMMM", localeObject)
    return formatter.format(calendar.time)
}


actual fun getCurrentLocale(): String {
    return Locale.getDefault().toLanguageTag()
}
