package org.fedsal.finance.data.debt

import kotlinx.coroutines.flow.Flow
import org.fedsal.finance.domain.models.Debt

interface DebtLocalDataSource {
    // CRUd
    suspend fun create(debt: Debt)
    suspend fun getAllDebts(): List<Debt>
    suspend fun update(debt: Debt)
    suspend fun delete(debt: Debt)
    suspend fun getDebtById(debtId: Long): Debt?
    fun getDebtsByPaymentMethod(paymentMethodId: Int): Flow<List<Debt>>

    suspend fun deleteByExpenseId(expenseId: Long)
}
