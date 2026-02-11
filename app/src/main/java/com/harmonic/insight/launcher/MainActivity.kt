package com.harmonic.insight.launcher

import android.app.WallpaperManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.harmonic.insight.launcher.ui.drawer.AppDrawerScreen
import com.harmonic.insight.launcher.ui.home.HomeScreen
import com.harmonic.insight.launcher.ui.search.SearchScreen
import com.harmonic.insight.launcher.ui.settings.SettingsScreen
import com.harmonic.insight.launcher.ui.theme.InsightLauncherTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        disableBackButton()

        setContent {
            InsightLauncherTheme {
                InsightLauncherNavigation()
            }
        }
    }

    private fun disableBackButton() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Launcher should not go back
            }
        })
    }
}

object Routes {
    const val HOME = "home"
    const val DRAWER = "drawer"
    const val SEARCH = "search"
    const val SETTINGS = "settings"
}

@Composable
fun InsightLauncherNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    // Load wallpaper as background
    val wallpaperBitmap = remember {
        try {
            val wallpaperManager = WallpaperManager.getInstance(context)
            wallpaperManager.drawable?.toBitmap()?.asImageBitmap()
        } catch (_: Exception) {
            null
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Wallpaper background
        wallpaperBitmap?.let { bitmap ->
            Image(
                bitmap = bitmap,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }

        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
        ) {
            composable(
                route = Routes.HOME,
                enterTransition = {
                    fadeIn(animationSpec = tween(300)) +
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Down,
                            animationSpec = tween(300),
                        )
                },
                exitTransition = {
                    fadeOut(animationSpec = tween(300))
                },
            ) {
                HomeScreen(
                    onOpenSearch = { navController.navigate(Routes.SEARCH) },
                    onOpenDrawer = { navController.navigate(Routes.DRAWER) },
                    onOpenSettings = { navController.navigate(Routes.SETTINGS) },
                )
            }

            composable(
                route = Routes.DRAWER,
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Up,
                        animationSpec = tween(300),
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Down,
                        animationSpec = tween(300),
                    )
                },
            ) {
                AppDrawerScreen(
                    onNavigateHome = { navController.popBackStack() },
                )
            }

            composable(
                route = Routes.SEARCH,
                enterTransition = {
                    fadeIn(animationSpec = tween(200))
                },
                exitTransition = {
                    fadeOut(animationSpec = tween(200))
                },
            ) {
                SearchScreen(
                    onNavigateBack = { navController.popBackStack() },
                )
            }

            composable(
                route = Routes.SETTINGS,
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Start,
                        animationSpec = tween(300),
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.End,
                        animationSpec = tween(300),
                    )
                },
            ) {
                SettingsScreen(
                    onNavigateBack = { navController.popBackStack() },
                )
            }
        }
    }
}
