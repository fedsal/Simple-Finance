package org.fedsal.finance.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class UserCategory (
    val id: Int = 0,
    val title: String = "",
    val budget: Double = 0.0,
    val iconId: String = "",
    val color: String = "",
)

fun UserCategory.toCategory() = Category(
    userCategoryId = id,
    title = title,
    budget = budget,
    iconId = iconId,
    color = color
)
