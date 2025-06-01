package org.fedsal.finance.domain.models

object DefaultPaymentMethods {
    val CASH = PaymentMethod(
        name = "Efectivo",
        iconId = AppIcons.CASH.name,
        type = PaymentMethodType.CASH,
        color = "#FF00A300"
    )

    val CREDIT_CARD = PaymentMethod(
        name = "Credito",
        iconId = AppIcons.CARD.name,
        type = PaymentMethodType.CREDIT,
        color = "#FF00A300"
    )
}
