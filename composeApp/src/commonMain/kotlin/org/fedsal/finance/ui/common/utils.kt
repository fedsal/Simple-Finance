package org.fedsal.finance.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Paid
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
        }
    } catch (e: IllegalArgumentException) {
        return Icons.Filled.Paid
    }
}
