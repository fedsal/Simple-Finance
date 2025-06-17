package org.fedsal.finance.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class PaymentMethod (
    val id: Int = 0,
    val name: String = "",
    val type: PaymentMethodType = PaymentMethodType.CASH,
    val iconId: String = AppIcons.CASH.name,
    val color: String = AppColors.GREEN.hexString,
)

@Serializable
enum class PaymentMethodType {
    CASH, CREDIT, LOAN
}
