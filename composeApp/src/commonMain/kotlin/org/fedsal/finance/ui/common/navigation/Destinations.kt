package org.fedsal.finance.ui.common.navigation

import kotlinx.serialization.Serializable

// Route for home
sealed class HomeDestination {
    @Serializable
    data object HomeGraph : HomeDestination()
    @Serializable
    data object Home : HomeDestination()
    @Serializable
    data object Balance : HomeDestination()
}

@Serializable
data class CategoryExpenses(val id: Int)
