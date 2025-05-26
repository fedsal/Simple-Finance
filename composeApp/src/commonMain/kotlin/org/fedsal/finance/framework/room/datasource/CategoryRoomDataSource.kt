package org.fedsal.finance.framework.room.datasource

import org.fedsal.finance.data.category.CategoryLocalDataSource
import org.fedsal.finance.domain.models.Category
import org.fedsal.finance.framework.room.dao.CategoryDao
import org.fedsal.finance.framework.room.model.toDomain
import org.fedsal.finance.framework.room.model.toEntity

class CategoryRoomDataSource(
    private val categoryDao: CategoryDao
): CategoryLocalDataSource {
    override suspend fun create(category: Category) {
        return categoryDao.create(category.toEntity())
    }

    override suspend fun read(): List<Category> {
        return categoryDao.readAll().map { it.toDomain() }
    }

    override suspend fun update(category: Category) {
        return categoryDao.update(category.toEntity())
    }

    override suspend fun delete(category: Category){
        return categoryDao.delete(category.toEntity())
    }

    override suspend fun getById(id: Int): Category? {
        return categoryDao.getCategoryById(id)?.toDomain()
    }

    override suspend fun deleteById(id: Int): Boolean {
        TODO("Not yet implemented")
    }

}
