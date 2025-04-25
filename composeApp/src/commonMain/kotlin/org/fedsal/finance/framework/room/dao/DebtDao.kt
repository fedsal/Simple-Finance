package org.fedsal.finance.framework.room.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import org.fedsal.finance.framework.room.model.DebtEntity

interface DebtDao {
    @Insert
    suspend fun create(debt: DebtEntity)

    @Query("SELECT * FROM debts ORDER BY date DESC")
    suspend fun readAll(): List<DebtEntity>

    @Update
    suspend fun update(debt: DebtEntity)

    @Delete
    suspend fun delete(entity: DebtEntity)
}
