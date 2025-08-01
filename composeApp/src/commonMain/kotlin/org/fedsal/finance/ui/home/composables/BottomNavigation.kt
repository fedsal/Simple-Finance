package org.fedsal.finance.ui.home.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import org.fedsal.finance.ui.home.navigation.ItemsBottomNav
import org.fedsal.finance.ui.home.navigation.hasRoute

@Composable
fun BottomNavigation(
    navHostController: NavHostController,
    onButtonClicked: () -> Unit,
) {
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        CustomAppBar(navHostController)
        FloatingActionButton(
            modifier = Modifier.size(65.dp).offset(y = (-30).dp),
            onClick = { onButtonClicked.invoke() },
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            elevation = FloatingActionButtonDefaults.elevation(0.dp)
        ) {
            Icon(
                modifier = Modifier.size(42.dp),
                imageVector = Icons.Rounded.Add,
                tint = MaterialTheme.colorScheme.primaryContainer,
                contentDescription = ""
            )
        }
    }

}

@Composable
private fun CustomAppBar(navHostController: NavHostController) {
    val menuItems = listOf(ItemsBottomNav.Expenses, ItemsBottomNav.Balance)
    val navBackStackEntry by navHostController.currentBackStackEntryAsState()
    BottomAppBar(
        modifier = Modifier
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .height(80.dp),
    ) {
        NavigationBar {
            menuItems.forEach { item ->
                NavigationBarItem(
                    selected = navBackStackEntry.hasRoute(item.route),
                    label = { Text(text = item.title) },
                    onClick = {
                        navHostController.navigate(item.route) {
                            navHostController.graph.startDestinationRoute?.let { startDestinationRoute ->
                                popUpTo(startDestinationRoute) {
                                    saveState = true
                                }
                            }

                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent,
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                    )
                )
            }
        }
    }
}

