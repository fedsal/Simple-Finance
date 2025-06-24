package org.fedsal.finance.ui.common

import kotlinx.datetime.LocalDate
import platform.Foundation.*

actual fun getLocalizedMonthName(date: LocalDate, locale: String): String {
    val formatter = NSDateFormatter().apply {
        dateFormat = "MMMM"
        setLocale(NSLocale(locale))
    }

    val calendar = NSCalendar.currentCalendar
    val components = NSDateComponents().apply {
        year = date.year.toLong()
        month = date.monthNumber.toLong()
        day = 1
    }

    val nsDate = calendar.dateFromComponents(components) ?: return ""
    return formatter.stringFromDate(nsDate)
}


actual fun getCurrentLocale(): String {
    val locale = NSLocale.currentLocale
    return locale.localeIdentifier.replace('_', '-')
}
