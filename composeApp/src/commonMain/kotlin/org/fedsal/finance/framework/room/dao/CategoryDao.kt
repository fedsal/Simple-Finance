package org.fedsal.finance.framework.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import org.fedsal.finance.framework.room.model.CategoryEntity

@Dao
interface CategoryDao {
    @Insert
    suspend fun create(category: CategoryEntity)

    @Query("SELECT * FROM categories ORDER BY title ASC")
    suspend fun readAll(): List<CategoryEntity>

    @Update
    suspend fun update(category: CategoryEntity)

    @Delete
    suspend fun delete(entity: CategoryEntity)

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategoryById(id: Int): CategoryEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg categories: CategoryEntity)
}
