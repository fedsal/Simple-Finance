package org.fedsal.finance.framework.room.datasource

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Month
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

    override fun read(): Flow<List<Expense>> {
        return expenseDao.readAll().map { outer -> outer.map { it.toDomain() } }
    }

    override suspend fun update(expense: Expense) {
        expenseDao.update(expense.toEntity())
    }

    override suspend fun delete(expense: Expense) {
        expenseDao.delete(expense.toEntity())
    }

    override suspend fun getById(id: Long): Expense {
        return expenseDao.getById(id)?.toDomain()
            ?: throw NoSuchElementException("Expense with id $id not found")
    }

    override fun getExpensesByCategory(
        categoryId: Int,
        month: Month,
        year: Int
    ): Flow<List<Expense>> {
        val paddedMonth = (month.ordinal + 1).toString().padStart(2, '0')
        return expenseDao.getExpensesByCategory(
            categoryId,
            paddedMonth,
            year.toString()
        ).map { expenseEntities -> expenseEntities.map { it.toDomain() } }
    }
}
