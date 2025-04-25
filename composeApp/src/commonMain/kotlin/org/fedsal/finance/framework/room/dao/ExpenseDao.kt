package org.fedsal.finance.framework.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import org.fedsal.finance.framework.room.model.ExpenseEntity

@Dao
interface ExpenseDao {
    @Insert
    suspend fun create(expense: ExpenseEntity)

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    suspend fun readAll(): List<ExpenseEntity>

    @Update
    suspend fun update(expense: ExpenseEntity)

    @Delete
    suspend fun delete(entity: ExpenseEntity)
}
