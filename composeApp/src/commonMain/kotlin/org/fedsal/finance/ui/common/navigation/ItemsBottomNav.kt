package org.fedsal.finance.ui.common.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.ui.graphics.vector.ImageVector

sealed class ItemsBottomNav(
    val icon: ImageVector,
    val title: String,
    val route: HomeDestination
) {
    data object Expenses: ItemsBottomNav(
        icon = Icons.Outlined.AccountBalanceWallet,
        title = "Gastos",
        route = HomeDestination.Home
    )

    data object Balance: ItemsBottomNav(
        icon = Icons.Outlined.AccountBalance,
        title = "Balance",
        route = HomeDestination.Shop
    )
}
