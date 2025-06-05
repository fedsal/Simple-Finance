package org.fedsal.finance.domain.models

data class Expense(
    val id: Int = 0,
    val title: String = "",
    val amount: Double = 0.0,
    val date: String = "",
    val description: String = "",
    val category: Category = Category(),
    val paymentMethod: PaymentMethod = DefaultPaymentMethods.CASH
)
