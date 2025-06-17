package org.fedsal.finance.ui.main.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class AppDestinations{
    @Serializable
    data object Home: AppDestinations()
    @Serializable
    data class Category(val id: Int): AppDestinations()
    @Serializable
    data class DebtDetail(val id: Int): AppDestinations()
}
