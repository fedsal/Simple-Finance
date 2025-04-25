package org.fedsal.finance.framework.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.fedsal.finance.domain.models.Category
import org.fedsal.finance.domain.models.Expense
import org.fedsal.finance.domain.models.PaymentMethod

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val amount: Double,
    val date: String,
    val description: String,
    val category: Category,
    val paymentMethod: PaymentMethod
)

fun ExpenseEntity.toDomain(): Expense {
    return Expense(
        id = id,
        title = title,
        amount = amount,
        date = date,
        description = description,
        category = category,
        paymentMethod = paymentMethod
    )
}

fun Expense.toEntity(): ExpenseEntity {
    return ExpenseEntity(
        title = title,
        amount = amount,
        date = date,
        description = description,
        category = category,
        paymentMethod = paymentMethod
    )
}
