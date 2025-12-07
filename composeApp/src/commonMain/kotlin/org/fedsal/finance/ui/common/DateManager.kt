package org.fedsal.finance.ui.common

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

object DateManager {
    private val currentInstant = Clock.System.todayIn(
        TimeZone.currentSystemDefault()
    )

    private val _selectedMonth: MutableStateFlow<Month> = MutableStateFlow(
        currentInstant.month
    )
    val selectedMonth: StateFlow<Month> get() = _selectedMonth

    private val _selectedYear = MutableStateFlow(currentInstant.year) // Año actual
    val selectedYear: StateFlow<Int> = _selectedYear.asStateFlow()

    fun isInCurrentYear() = _selectedYear.value == currentInstant.year

    fun incrementMonth() {
        if (_selectedMonth.value == Month.DECEMBER) {
            _selectedYear.value++
            _selectedMonth.value = Month.JANUARY
        } else {
            val currentMonthOrdinal = _selectedMonth.value.ordinal
            _selectedMonth.value = Month.entries[currentMonthOrdinal + 1]
        }
    }


    fun decrementMonth() {
        if (_selectedMonth.value == Month.JANUARY) {
            _selectedYear.value--
            _selectedMonth.value = Month.DECEMBER
        } else {
            val currentMonthOrdinal = _selectedMonth.value.ordinal
            _selectedMonth.value = Month.entries[currentMonthOrdinal - 1]
        }
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
