package org.fedsal.finance.framework.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.fedsal.finance.domain.models.Category
import org.fedsal.finance.domain.models.UserCategory

@Entity(tableName = "user_categories")
data class UserCategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String = "",
    val budget: Double = 0.0,
    val iconId: String = "",
    val color: String = "",
)

fun UserCategoryEntity.toDomain(): UserCategory {
    return UserCategory(
        id = id,
        title = title,
        budget = budget,
        iconId = iconId,
        color = color,
    )
}

fun UserCategory.toEntity(): UserCategoryEntity {
    return UserCategoryEntity(
        id = id,
        title = title,
        budget = budget,
        iconId = iconId,
        color = color,
    )
}

