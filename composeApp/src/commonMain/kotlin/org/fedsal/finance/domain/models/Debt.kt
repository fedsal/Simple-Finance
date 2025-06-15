package org.fedsal.finance.domain.models

data class Debt(
    val id: Int = 0,
    val title: String,
    val amount: Double,
    val date: String,
    val category: Category,
    val installments: Int,
    val paidInstallments: Int = 0,
    val paymentMethod: PaymentMethod,
    val description: String,
)
