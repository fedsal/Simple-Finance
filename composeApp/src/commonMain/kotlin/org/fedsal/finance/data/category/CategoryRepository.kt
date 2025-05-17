package org.fedsal.finance.data.category

import org.fedsal.finance.domain.models.Category

class CategoryRepository(
    private val localDataSource: CategoryLocalDataSource,
) {
    // CRUD
    suspend fun create(category: Category){
        return localDataSource.create(category)
    }

    suspend fun read(): List<Category> {
        return localDataSource.read()
    }

    suspend fun update(category: Category){
        return localDataSource.update(category)
    }

    suspend fun delete(category: Category){
        return localDataSource.delete(category)
    }

    suspend fun getById(id: Int): Category? {
        return localDataSource.getById(id)
    }

    suspend fun deleteById(id: Int): Boolean {
        return localDataSource.deleteById(id)
    }
}
