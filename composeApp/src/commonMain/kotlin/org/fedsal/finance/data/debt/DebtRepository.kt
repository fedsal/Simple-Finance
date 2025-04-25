package org.fedsal.finance.data.debt

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
}
