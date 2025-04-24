package org.fedsal.finance.domain.models

data class Expense(
    val id: Int,
    val title: String,
    val amount: Double,
    val date: String,
    val description: String,
    val category: Category,
    val paymentMethod: PaymentMethod
)
