package org.fedsal.finance.ui.common

fun Double.formatDecimal(): String {
    val rounded = (this * 100).toLong()
    val integerPart = (rounded / 100).toString()
    val decimalRaw = (rounded % 100).toInt()

    val formattedInteger = buildString {
        integerPart.reversed().chunked(3).joinToString(".").reversed().forEach { append(it) }
    }

    return if (decimalRaw == 0) formattedInteger
    else {
        val decimalPart = decimalRaw.toString().padStart(2, '0')
        "$formattedInteger,$decimalPart"
    }

}
