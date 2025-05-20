package org.fedsal.finance.ui.common.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.QrCode
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import org.fedsal.finance.ui.common.navigation.HomeDestination
import org.fedsal.finance.ui.common.navigation.ItemsBottomNav
import org.fedsal.finance.ui.common.navigation.hasRoute

@Composable
fun BottomNavigation(
    navHostController: NavHostController,
    onButtonClicked: () -> Unit,
) {
    val navBackStackEntry by navHostController.currentBackStackEntryAsState()
    val showBottomBar = navBackStackEntry.hasRoute(HomeDestination.HomeGraph)
    if (showBottomBar) {
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            CustomAppBar(navHostController)
            FloatingActionButton(
                modifier = Modifier.size(70.dp).offset(y = (-30).dp),
                onClick = { onButtonClicked.invoke() },
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(0.dp)
            ) {
                Icon(
                    modifier = Modifier.size(30.dp),
                    imageVector = Icons.Rounded.QrCode,
                    tint = Color.Black,
                    contentDescription = ""
                )
            }
        }
    }
}

@Composable
private fun CustomAppBar(navHostController: NavHostController) {
    val menuItems = listOf(ItemsBottomNav.Profile, ItemsBottomNav.Shop)
    val navBackStackEntry by navHostController.currentBackStackEntryAsState()
    BottomAppBar(
        modifier = Modifier.graphicsLayer {
            clip = true
            shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
            shadowElevation = 20f
        }
            .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
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

