package org.fedsal.finance.framework.room.datasource

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.fedsal.finance.data.usercategory.UserCategoryLocalDataSource
import org.fedsal.finance.domain.models.UserCategory
import org.fedsal.finance.framework.room.dao.UserCategoryDao
import org.fedsal.finance.framework.room.model.toDomain
import org.fedsal.finance.framework.room.model.toEntity

class UserCategoryRoomDataSource(
    private val userCategoryDao: UserCategoryDao
): UserCategoryLocalDataSource {
    override suspend fun create(category: UserCategory): Long {
        return userCategoryDao.create(category.toEntity())
    }

    override fun read(): Flow<List<UserCategory>> {
        return userCategoryDao.read().map { categoryEntities -> categoryEntities.map { it.toDomain() }  }
    }

    override suspend fun update(category: UserCategory) {
        userCategoryDao.update(category.toEntity())
    }

    override suspend fun delete(category: UserCategory) {
        userCategoryDao.delete(category.toEntity())
    }
}
