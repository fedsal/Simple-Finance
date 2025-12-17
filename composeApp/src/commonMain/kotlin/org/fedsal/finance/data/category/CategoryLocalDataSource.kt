package org.fedsal.finance.data.category

import kotlinx.coroutines.flow.Flow
import org.fedsal.finance.domain.models.Category

interface CategoryLocalDataSource {

    // CRUD
    suspend fun create(category: Category): Long
    fun read(selectedDate: String): Flow<List<Category>>

    suspend fun readAll(): List<Category>
    suspend fun update(category: Category)
    suspend fun delete(category: Category)

    suspend fun getById(id: Int): Category?
    suspend fun deleteById(id: Int): Boolean
}
