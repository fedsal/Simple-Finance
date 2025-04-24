package org.fedsal.finance.data.expense

import org.fedsal.finance.domain.models.Expense

interface ExpenseLocalDataSource {
    suspend fun create(expense: Expense)
    suspend fun read(): List<Expense>
    suspend fun update(expense: Expense)
    suspend fun delete(id: Int)
}