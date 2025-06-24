package org.fedsal.finance.ui.common

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

object DateManager {
    private val currentInstant = Clock.System.todayIn(
        TimeZone.currentSystemDefault()
    )
    val year = currentInstant.year
    private val _selectedMonth: MutableStateFlow<Month> = MutableStateFlow(
        currentInstant.month
    )
    val selectedMonth: StateFlow<Month> get() = _selectedMonth

    fun incrementMonth() {
        val currentMonth = _selectedMonth.value.ordinal
        val newMonth = if (currentMonth == 0) {
            Month.entries.first()
        } else {
            Month.entries[currentMonth + 1]
        }
        _selectedMonth.value = newMonth
    }

    fun decrementMonth() {
        val currentMonth = _selectedMonth.value.ordinal
        val newMonth = if (currentMonth == Month.entries.lastIndex) {
            Month.entries.last()
        } else {
            Month.entries[currentMonth -1]
        }
        _selectedMonth.value = newMonth
    }

    fun getCurrentDate(): String {
        val day = if (currentInstant.dayOfMonth < 10) { "0${currentInstant.dayOfMonth}" } else {
            currentInstant.dayOfMonth.toString()
        }
        val month = if (currentInstant.monthNumber < 10) { "0${currentInstant.monthNumber}" } else {
            currentInstant.monthNumber.toString()
        }
        return "$day$month"
    }
}
