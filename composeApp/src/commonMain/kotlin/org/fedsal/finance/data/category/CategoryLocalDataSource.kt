package org.fedsal.finance.data.category

import org.fedsal.finance.domain.models.Category

interface CategoryLocalDataSource {

    // CRUD
    suspend fun create(category: Category): Long
    suspend fun read(): List<Category>
    suspend fun update(category: Category)
    suspend fun delete(category: Category)

    suspend fun getById(id: Int): Category?
    suspend fun deleteById(id: Int): Boolean
}
