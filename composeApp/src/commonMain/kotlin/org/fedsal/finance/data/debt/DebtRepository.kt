package org.fedsal.finance.data.debt

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Month
import org.fedsal.finance.domain.models.Debt

class DebtRepository(
    private val localDataSource: DebtLocalDataSource,
) {
    suspend fun createDebt(debt: Debt) {
        localDataSource.create(debt)
    }

    suspend fun getAllDebts(): List<Debt> {
        return localDataSource.getAllDebts()
    }

    suspend fun update(debt: Debt) {
        localDataSource.update(debt)
    }

    suspend fun delete(debt: Debt) {
        localDataSource.delete(debt)
    }

    suspend fun getDebtsByPaymentMethod(
        paymentMethodId: Int,
        month: Month,
        year: Int
    ): Flow<List<Debt>> {
        return localDataSource.getDebtsByPaymentMethod(paymentMethodId, month, year)
    }
}
