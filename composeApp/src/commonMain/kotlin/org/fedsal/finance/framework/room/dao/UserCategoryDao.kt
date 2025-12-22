package org.fedsal.finance.framework.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import org.fedsal.finance.framework.room.model.UserCategoryEntity

@Dao
interface UserCategoryDao {
    @Insert
    suspend fun create(category: UserCategoryEntity): Long

    @Query("SELECT * FROM user_categories ORDER BY title ASC")
    fun read(): Flow<List<UserCategoryEntity>>

    @Update
    suspend fun update(category: UserCategoryEntity)

    @Delete
    suspend fun delete(category: UserCategoryEntity)

    @Query("SELECT * FROM user_categories WHERE id = :id")
    suspend fun getById(id: Int): UserCategoryEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg categories: UserCategoryEntity)
}
