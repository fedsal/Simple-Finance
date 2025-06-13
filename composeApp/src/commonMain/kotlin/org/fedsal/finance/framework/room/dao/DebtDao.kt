package org.fedsal.finance.framework.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import org.fedsal.finance.framework.room.model.DebtEntity

@Dao
interface DebtDao {
    @Insert
    suspend fun create(debt: DebtEntity)

    @Query("SELECT * FROM debts ORDER BY date DESC")
    suspend fun readAll(): List<DebtEntity>

    @Update
    suspend fun update(debt: DebtEntity)

    @Delete
    suspend fun delete(entity: DebtEntity)

    @Query(
        "SELECT * FROM debts WHERE strftime('%m', date) = :month AND " +
                "strftime('%Y', date) = :year AND paymentMethodId = :paymentMethodId ORDER BY date DESC"
    )
    fun getDebtsByPaymentMethod(
        paymentMethodId: Int,
        month: String,
        year: String
    ): Flow<List<DebtEntity>>
}
