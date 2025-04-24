package org.fedsal.finance.domain.models

data class Debt(
    val id: Int,
    val name: String,
    val amount: Double,
    val date: String,
    val installments: Int,
    val description: String,
)
