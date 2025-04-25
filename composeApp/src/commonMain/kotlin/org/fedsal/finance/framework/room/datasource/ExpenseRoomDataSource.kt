package org.fedsal.finance.framework.room.datasource

import org.fedsal.finance.data.expense.ExpenseLocalDataSource
import org.fedsal.finance.domain.models.Expense
import org.fedsal.finance.framework.room.dao.ExpenseDao
import org.fedsal.finance.framework.room.model.toDomain
import org.fedsal.finance.framework.room.model.toEntity

class ExpenseRoomDataSource(
    private val expenseDao: ExpenseDao
) : ExpenseLocalDataSource {
    override suspend fun create(expense: Expense) {
        expenseDao.create(expense.toEntity())
    }

    override suspend fun read(): List<Expense> {
        return expenseDao.readAll().map { it.toDomain() }
    }

    override suspend fun update(expense: Expense) {
        expenseDao.update(expense.toEntity())
    }

    override suspend fun delete(expense: Expense) {
        expenseDao.delete(expense.toEntity())
    }
}
