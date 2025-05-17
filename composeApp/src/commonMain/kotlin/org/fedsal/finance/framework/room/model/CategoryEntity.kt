package org.fedsal.finance.framework.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.fedsal.finance.domain.models.Category

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val budget: Double,
    val iconId: String,
    val color: String
)

fun CategoryEntity.toDomain(): Category {
    return Category(
        id = id,
        title = title,
        budget = budget,
        iconId = iconId,
        color = color
    )
}

fun Category.toEntity(): CategoryEntity {
    return CategoryEntity(
        id = id,
        title = title,
        budget = budget,
        iconId = iconId,
        color = color
    )
}
