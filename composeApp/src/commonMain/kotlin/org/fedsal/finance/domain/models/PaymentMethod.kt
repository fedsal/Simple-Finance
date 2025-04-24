package org.fedsal.finance.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class PaymentMethod (
    val id: Int,
    val name: String,
    val type: PaymentMethodType,
    val iconId: String,
    val color: String,
)

@Serializable
enum class PaymentMethodType {
    CASH, CREDIT, LOAN
}
