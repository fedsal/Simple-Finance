package org.fedsal.finance.data.debt

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Month
import org.fedsal.finance.domain.models.Debt

interface DebtLocalDataSource {
    // CRUd
    suspend fun create(debt: Debt)
    suspend fun getAllDebts(): List<Debt>
    suspend fun update(debt: Debt)
    suspend fun delete(debt: Debt)
    suspend fun getDebtById(debtId: Long): Debt?
    suspend fun getDebtsByPaymentMethod(
        paymentMethodId: Int,
        month: Month,
        year: Int
    ): Flow<List<Debt>>
}
