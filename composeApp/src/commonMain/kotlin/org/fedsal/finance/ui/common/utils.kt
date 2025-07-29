package org.fedsal.finance.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.fedsal.finance.domain.models.AppIcons

fun Double.formatDecimal(): String {
    val isNegative = this < 0
    val rounded = (kotlin.math.abs(this) * 100).toLong()
    val integerPart = (rounded / 100).toString()
    val decimalRaw = (rounded % 100).toInt()

    val formattedInteger = buildString {
        integerPart.reversed().chunked(3).joinToString(".").reversed().forEach { append(it) }
    }

    val result = if (decimalRaw == 0) formattedInteger
    else {
        val decimalPart = decimalRaw.toString().padStart(2, '0')
        "$formattedInteger,$decimalPart"
    }

    return if (isNegative) "-$result" else result
}

fun getIcon(name: String): ImageVector {
    try {
        val appIcon = AppIcons.valueOf(name)
        return when (appIcon) {
            AppIcons.CARD -> Icons.Outlined.CreditCard
            AppIcons.CASH -> Icons.Filled.Paid
            AppIcons.SHOPPING_CART -> Icons.Outlined.ShoppingCart
            AppIcons.PIN -> Icons.Filled.PushPin
            AppIcons.CAR -> Icons.Filled.DirectionsCar
            AppIcons.HOME -> Icons.Filled.Home
            AppIcons.SHOPPING_BAG -> Icons.Filled.ShoppingBag
            AppIcons.PERSON -> Icons.Outlined.Person
            AppIcons.BANK -> Icons.Outlined.AccountBalance
        }
    } catch (e: IllegalArgumentException) {
        e.printStackTrace()
        return Icons.Filled.Paid
    }
}


fun hexToColor(hex: String): Color {
    val cleanHex = hex.removePrefix("#")
    try {
        val argb = when (cleanHex.length) {
            6 -> { // RRGGBB → assume alpha = FF
                val r = cleanHex.substring(0, 2).toInt(16)
                val g = cleanHex.substring(2, 4).toInt(16)
                val b = cleanHex.substring(4, 6).toInt(16)
                Color(r, g, b, 255)
            }

            8 -> { // AARRGGBB
                val a = cleanHex.substring(0, 2).toInt(16)
                val r = cleanHex.substring(2, 4).toInt(16)
                val g = cleanHex.substring(4, 6).toInt(16)
                val b = cleanHex.substring(6, 8).toInt(16)
                Color(r, g, b, a)
            }

            else -> throw IllegalArgumentException("Invalid color format: $hex")
        }

        return argb
    } catch (e: Exception) {
        e.printStackTrace()
        return Color.Blue
    }
}

fun opaqueColor(color: Color, factor: Float = 0.55f): Color {
    return lerp(color, Color.Black, factor.coerceIn(0f, 1f))
}

fun convertToIso(input: String): String {
    try {
        val digits = input.filter { it.isDigit() }
        if (digits.length != 4) return ""

        val day = digits.substring(0, 2)
        val month = digits.substring(2, 4)

        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val year = now.year
        val hour = now.hour.toString().padStart(2, '0')
        val minute = now.minute.toString().padStart(2, '0')
        val second = now.second.toString().padStart(2, '0')

        return "$year-$month-${day}T$hour:$minute:$second"
    } catch (e: Exception) {
        e.printStackTrace()
        throw e
    }
}

fun convertFromIso(input: String): String {
    return try {
        val datePart = input.substringBefore('T')
        val parts = datePart.split("-")
        if (parts.size != 3) return ""

        val year = parts[0]
        val month = parts[1]
        val day = parts[2]

        "$day$month"
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}

expect fun getLocalizedMonthName(date: LocalDate, locale: String): String

expect fun getCurrentLocale(): String

object DateDefaults {
    const val DATE_MASK = "##/##"
    const val DATE_LENGTH = 4
}

object ExpenseDefaults {
    const val MAX_EXPENSE_VALUE = 10000000
}

