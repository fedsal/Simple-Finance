package org.fedsal.finance.data.usercategory

import kotlinx.coroutines.flow.Flow
import org.fedsal.finance.domain.models.UserCategory

class UserCategoryRepository(
    private val localDataSource: UserCategoryLocalDataSource,
) {
    // CRUD
    suspend fun create(category: UserCategory): Long {
        return localDataSource.create(category)
    }

    fun read(): Flow<List<UserCategory>> {
        return localDataSource.read()
    }

    suspend fun update(category: UserCategory) {
        return localDataSource.update(category)
    }

    suspend fun delete(category: UserCategory) {
        return localDataSource.delete(category)
    }
}
