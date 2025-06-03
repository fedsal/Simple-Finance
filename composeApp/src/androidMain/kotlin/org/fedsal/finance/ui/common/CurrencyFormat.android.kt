package org.fedsal.finance.ui.common

actual fun formatCurrency(amount: Int, currencyCode: String): String {
    val numberFormatter = java.text.NumberFormat.getCurrencyInstance().apply {
        currency = java.util.Currency.getInstance(currencyCode)
        maximumFractionDigits = 0
    }
    return numberFormatter.format(amount)
}
