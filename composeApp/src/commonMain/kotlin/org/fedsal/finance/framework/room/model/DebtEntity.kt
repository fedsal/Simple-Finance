package org.fedsal.finance.framework.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.fedsal.finance.domain.models.Category
import org.fedsal.finance.domain.models.Debt
import org.fedsal.finance.domain.models.PaymentMethod
import org.fedsal.finance.domain.models.PaymentMethodType

@Entity(tableName = "debts")
data class DebtEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val amount: Double,
    val date: String,
    val category: Category,
    val installments: Int,
    val paymentMethodId: Int,
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
        paymentMethod = PaymentMethod(
            id = paymentMethodId,
            name = "",
            type = PaymentMethodType.CREDIT,
            iconId = "",
            color = ""
        ),
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
        paymentMethodId = paymentMethod.id,
        description = description
    )
}
