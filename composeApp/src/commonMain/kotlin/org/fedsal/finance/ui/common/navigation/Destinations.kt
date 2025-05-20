package org.fedsal.finance.ui.common.navigation

import kotlinx.serialization.Serializable

// Route for home
sealed class HomeDestination {
    @Serializable
    data object HomeGraph : HomeDestination()
    @Serializable
    data object Home : HomeDestination()
    @Serializable
    data object Shop : HomeDestination()
}
