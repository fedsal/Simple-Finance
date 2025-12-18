package org.fedsal.finance.data.category

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import org.fedsal.finance.data.usercategory.UserCategoryLocalDataSource
import org.fedsal.finance.domain.models.Category
import org.fedsal.finance.domain.models.UserCategory
import org.fedsal.finance.domain.models.toCategory
import org.fedsal.finance.domain.models.toUserCategory

class CategoryRepository(
    private val localDataSource: CategoryLocalDataSource,
    private val userCategoryLocalDataSource: UserCategoryLocalDataSource
) {
    private val _categoryUpdateFlow = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    // CRUD
    suspend fun create(category: Category, selectedDate: String): Long {

        val formattedDate: String = if (selectedDate.length == 6) {
            StringBuilder(selectedDate)
                .insert(2, "/")
                .toString()
        } else {
            selectedDate
        }

        val userCategoryId = userCategoryLocalDataSource.create(
            UserCategory(
                title = category.title,
                budget = category.budget,
                iconId = category.iconId,
                color = category.color
            )
        )

        val newCategory = category.copy(
            userCategoryId = userCategoryId.toInt(),
            date = formattedDate
        )

        val newCategoryId = localDataSource.create(newCategory)

        _categoryUpdateFlow.tryEmit(Unit)

        return newCategoryId
    }

    fun read(selectedDate: String, currentDate: String): Flow<List<Category>> = channelFlow {
        val currentCategories = localDataSource.read(selectedDate).first()
        val userCategories = userCategoryLocalDataSource.read().first()

        val categoriesToAdd = userCategories.filterNot { userCat ->
            currentCategories.any { it.userCategoryId == userCat.id }
        }

        if (categoriesToAdd.isNotEmpty()) {
            if (isDateGreaterOrEqual(selectedDate, currentDate) || currentCategories.isEmpty()) {
                categoriesToAdd.forEach { category ->
                    localDataSource.create(category.toCategory().copy(date = selectedDate))
                }
            }
        }
        launch {
            localDataSource.read(selectedDate).collect { categories ->
                send(categories)
            }
        }

        launch {
            _categoryUpdateFlow.collect {
                send(localDataSource.read(selectedDate).first())
            }
        }
    }

    suspend fun update(category: Category, currentDate: String) {
        userCategoryLocalDataSource.update(category.toUserCategory())

        val allMonthlyCategories = localDataSource.readAll()

        val futureInstancesToUpdate = allMonthlyCategories.filter {
            it.userCategoryId == category.userCategoryId && isDateGreaterOrEqual(
                it.date,
                category.date
            )
        }

        futureInstancesToUpdate.forEach { futureCategory ->
            localDataSource.update(
                futureCategory.copy(
                    title = category.title,
                    iconId = category.iconId,
                    color = category.color,
                    budget = category.budget,
                )
            )
        }

        localDataSource.update(category)
    }


    suspend fun delete(category: Category, currentDate: String) {
        userCategoryLocalDataSource.delete(category.toUserCategory())

        val allMonthlyCategories = localDataSource.readAll()

        val futureInstancesToDelete = allMonthlyCategories.filter {
            it.userCategoryId == category.userCategoryId && isDateGreaterOrEqual(
                it.date,
                category.date
            )
        }

        futureInstancesToDelete.forEach { futureCategory ->
            localDataSource.delete(futureCategory)
        }

        localDataSource.delete(category)
    }

    suspend fun getById(id: Int): Category? {
        return localDataSource.getById(id)
    }

    suspend fun deleteById(id: Int): Boolean {
        return localDataSource.deleteById(id)
    }

    /**
     * UTILS
     */

    private fun isDateGreaterOrEqual(selectedDate: String, currentDate: String): Boolean {
        val selectedMonth = selectedDate.take(2).toInt()
        val selectedYear = selectedDate.takeLast(2).toInt()
        val currentMonth = currentDate.take(2).toInt()
        val currentYear = currentDate.takeLast(2).toInt()

        if (selectedYear > currentYear) return true
        if (selectedYear < currentYear) return false

        return selectedMonth >= currentMonth
    }
}
