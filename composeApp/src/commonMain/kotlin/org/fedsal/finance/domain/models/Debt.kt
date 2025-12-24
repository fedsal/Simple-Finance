package org.fedsal.finance.domain.models

data class Debt(
    val id: Int = 0,
    val title: String = "",
    val amount: Double = 0.0,
    val date: String = "",
    val category: Category = Category(),
    val installments: Int = 0,
    val paidInstallments: Int = 0,
    val paymentMethod: PaymentMethod = PaymentMethod(),
    val description: String = "",
    val expenseId: Long? = null
)
