package org.fedsal.finance.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Category (
    val id: Int = 0,
    val userCategoryId: Int = 0,
    val title: String = "",
    val budget: Double = 0.0,
    val iconId: String = "",
    val color: String = "",
    val date: String = ""
)

fun Category.toUserCategory() = UserCategory(
    id = userCategoryId,
    title = title,
    budget = budget,
    iconId = iconId,
    color = color
)
