package org.fedsal.finance.ui.common.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation

/**
 * Slide in/out animated navigation
 */
inline fun <reified T : Any> NavGraphBuilder.animatedNavigation(
    startDestination: Any,
    noinline builder: NavGraphBuilder.() -> Unit
) {
    navigation<T>(
        startDestination = startDestination,
        builder = builder,
        enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
        exitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
    )
}

/**
 * Slide in/out animated composable
 */
inline fun <reified T : Any> NavGraphBuilder.animatedComposable(
    noinline content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
    composable<T>(
        enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
        exitTransition = { slideOutHorizontally(targetOffsetX = { it }) },
        content = content
    )
}
