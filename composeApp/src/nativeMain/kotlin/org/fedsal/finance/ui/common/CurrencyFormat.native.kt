package org.fedsal.finance.ui.common

import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterCurrencyStyle

actual fun formatCurrency(amount: Int, currencyCode: String): String {
    val formatter = NSNumberFormatter().apply {
        numberStyle = NSNumberFormatterCurrencyStyle
        this.currencyCode = currencyCode
        maximumFractionDigits = 0u
    }
    return formatter.stringFromNumber(NSNumber(amount)) ?: "$amount $currencyCode"
}
