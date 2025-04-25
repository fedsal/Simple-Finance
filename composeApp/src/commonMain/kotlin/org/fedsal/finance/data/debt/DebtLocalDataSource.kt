package org.fedsal.finance.data.debt

import org.fedsal.finance.domain.models.Debt

interface DebtLocalDataSource {
    // CRUd
    suspend fun create(debt: Debt)
    suspend fun getAllDebts(): List<Debt>
    suspend fun update(debt: Debt)
    suspend fun delete(debt: Debt)
}
