package org.fedsal.finance.framework.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.fedsal.finance.domain.models.Category
import org.fedsal.finance.domain.models.Debt
import org.fedsal.finance.domain.models.PaymentMethod

@Entity(tableName = "debts")
data class DebtEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val amount: Double,
    val date: String,
    val category: Category,
    val installments: Int,
    val paymentMethod: PaymentMethod,
    val description: String,
)

fun DebtEntity.toDomain(): Debt {
    return Debt(
        id = id,
        title = title,
        amount = amount,
        date = date,
        category = category,
        installments = installments,
        paymentMethod = paymentMethod,
        description = description
    )
}

fun Debt.toEntity(): DebtEntity {
    return DebtEntity(
        title = title,
        amount = amount,
        date = date,
        category = category,
        installments = installments,
        paymentMethod = paymentMethod,
        description = description
    )
}
