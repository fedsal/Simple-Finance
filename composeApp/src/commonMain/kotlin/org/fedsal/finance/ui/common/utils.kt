package org.fedsal.finance.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import org.fedsal.finance.domain.models.AppIcons

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
        }
    } catch (e: IllegalArgumentException) {
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
    val parts = input.split("/") // expecting "dd/MM/yyyy"
    val day = parts[0].padStart(2, '0')
    val month = parts[1].padStart(2, '0')
    val year = parts[2]
    return "$year-$month-$day" // returns "yyyy-MM-dd"
}
