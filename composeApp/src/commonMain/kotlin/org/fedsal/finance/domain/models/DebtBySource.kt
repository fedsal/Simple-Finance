package org.fedsal.finance.domain.models

data class DebtBySource(
    val source: PaymentMethod,
    val debtsList: List<Debt>,
    val totalDebt: Double,
)
