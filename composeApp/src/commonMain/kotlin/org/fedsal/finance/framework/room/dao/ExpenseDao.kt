package org.fedsal.finance.framework.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import org.fedsal.finance.framework.room.model.ExpenseEntity

@Dao
interface ExpenseDao {
    @Insert
    suspend fun create(expense: ExpenseEntity)

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun readAll(): Flow<List<ExpenseEntity>>

    @Update
    suspend fun update(expense: ExpenseEntity)

    @Delete
    suspend fun delete(entity: ExpenseEntity)

    @Query("SELECT * FROM expenses WHERE id = :id")
    suspend fun getById(id: Long): ExpenseEntity?

    @Query(
        "SELECT * FROM expenses WHERE strftime('%m', date) = :month AND " +
                "strftime('%Y', date) = :year AND categoryId = :categoryId ORDER BY date DESC"
    )
    fun getExpensesByCategory(
        categoryId: Int,
        month: String,
        year: String
    ): Flow<List<ExpenseEntity>>
}
