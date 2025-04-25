package org.fedsal.finance.framework.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.fedsal.finance.domain.models.Debt

@Entity(tableName = "debts")
data class DebtEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val amount: Double,
    val date: String,
    val installments: Int,
    val description: String,
)

fun DebtEntity.toDomain(): Debt {
    return Debt(
        id = id,
        name = name,
        amount = amount,
        date = date,
        installments = installments,
        description = description
    )
}

fun Debt.toEntity(): DebtEntity {
    return DebtEntity(
        name = name,
        amount = amount,
        date = date,
        installments = installments,
        description = description
    )
}
