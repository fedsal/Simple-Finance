package org.fedsal.finance.domain.models

data class PaymentMethod (
    val id: Int,
    val name: String,
    val type: PaymentMethodType,
    val iconId: String,
    val color: String,
)

enum class PaymentMethodType {
    CASH, CREDIT, LOAN
}
