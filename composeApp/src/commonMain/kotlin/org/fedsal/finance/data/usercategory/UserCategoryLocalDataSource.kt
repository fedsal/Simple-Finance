package org.fedsal.finance.data.usercategory

import kotlinx.coroutines.flow.Flow
import org.fedsal.finance.domain.models.UserCategory

interface UserCategoryLocalDataSource {
    // CRUD
    suspend fun create(category: UserCategory): Long
    fun read(): Flow<List<UserCategory>>

    suspend fun getById(id: Int): UserCategory?
    suspend fun update(category: UserCategory)
    suspend fun delete(category: UserCategory)
}
