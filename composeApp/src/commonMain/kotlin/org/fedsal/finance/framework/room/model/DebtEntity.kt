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
    val categoryId: Long,
    val installments: Int,
    val paidInstallments: Int = 0,
    val paymentMethodId: Int,
    val description: String,
    val expenseId: Long? = null
)

fun DebtEntity.toDomain(): Debt {
    return Debt(
        id = id,
        title = title,
        amount = amount,
        date = date,
        category = Category(id = categoryId.toInt()),
        installments = installments,
        paidInstallments = paidInstallments,
        paymentMethod = PaymentMethod(
            id = paymentMethodId,
            name = "",
            type = PaymentMethodType.CREDIT,
            iconId = "",
            color = ""
        ),
        description = description,
        expenseId = expenseId
    )
}

fun Debt.toEntity(): DebtEntity {
    return DebtEntity(
        id = id,
        title = title,
        amount = amount,
        date = date,
        categoryId = category.id.toLong(),
        installments = installments,
        paidInstallments = paidInstallments,
        paymentMethodId = paymentMethod.id,
        description = description,
        expenseId = expenseId
    )
}
