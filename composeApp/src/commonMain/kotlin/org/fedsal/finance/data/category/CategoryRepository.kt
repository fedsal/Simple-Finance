package org.fedsal.finance.data.category

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import org.fedsal.finance.data.usercategory.UserCategoryLocalDataSource
import org.fedsal.finance.domain.models.Category
import org.fedsal.finance.domain.models.toCategory
import org.fedsal.finance.domain.models.toUserCategory

class CategoryRepository(
    private val localDataSource: CategoryLocalDataSource,
    private val userCategoryLocalDataSource: UserCategoryLocalDataSource
) {
    // CRUD
    suspend fun create(category: Category): Long {
        return userCategoryLocalDataSource.create(category.toUserCategory())
    }

    fun read(selectedDate: String, currentDate: String): Flow<List<Category>> = flow {
        val currentCategories = localDataSource.read(selectedDate).first()
        val userCategories = userCategoryLocalDataSource.read().first()
        val userCategoriesAsCategories = userCategories.map { it.toCategory() }

        val categoriesToAdd = userCategoriesAsCategories.filterNot { userCat ->
            currentCategories.any { it.userCategoryId == userCat.userCategoryId }
        }

        if (categoriesToAdd.isNotEmpty() && isDateGreaterOrEqual(selectedDate, currentDate)) {
            categoriesToAdd.forEach { category ->
                localDataSource.create(category.copy(date = selectedDate))
            }
        }

        emitAll(localDataSource.read(selectedDate))
    }

    suspend fun update(category: Category, currentDate: String){
        userCategoryLocalDataSource.update(category.toUserCategory())

        val allMonthlyCategories = localDataSource.readAll().first()

        val futureInstancesToUpdate = allMonthlyCategories.filter {
            it.userCategoryId == category.userCategoryId && isDateGreaterOrEqual(it.date, currentDate)
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


    suspend fun delete(category: Category, currentDate: String){
        userCategoryLocalDataSource.delete(category.toUserCategory())

        val allMonthlyCategories = localDataSource.readAll().first()

        val futureInstancesToDelete = allMonthlyCategories.filter {
            it.userCategoryId == category.userCategoryId && isDateGreaterOrEqual(it.date, currentDate)
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
