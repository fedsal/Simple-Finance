package org.fedsal.finance.domain.models

data class Category (
    val id: Int,
    val title: String,
    val budget: Double,
    val iconId: String,
    val color: String
)
