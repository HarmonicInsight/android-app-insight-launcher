package com.harmonic.insight.launcher.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.harmonic.insight.launcher.R
import com.harmonic.insight.launcher.ui.components.AppIcon
import com.harmonic.insight.launcher.ui.components.CategoryTabRow
import com.harmonic.insight.launcher.ui.components.ClockWidget
import com.harmonic.insight.launcher.ui.components.HomeSearchBar
import com.harmonic.insight.launcher.ui.components.IconSize

@Composable
fun HomeScreen(
    onOpenSearch: () -> Unit,
    onOpenSettings: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Settings button (top-right)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 4.dp),
                horizontalArrangement = Arrangement.End,
            ) {
                IconButton(onClick = onOpenSettings) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = stringResource(R.string.settings),
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(24.dp),
                    )
                }
            }

            // Clock widget
            ClockWidget(
                textColor = Color.White,
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isLoading) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else if (uiState.categories.isNotEmpty()) {
                // Category pages
                CategoryPager(
                    uiState = uiState,
                    onLaunchApp = { viewModel.launchApp(it) },
                    modifier = Modifier.weight(1f),
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Search bar
            HomeSearchBar(
                onClick = onOpenSearch,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Dock (4 fixed apps)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                uiState.dockApps.forEach { app ->
                    AppIcon(
                        appName = app.appName,
                        icon = app.icon,
                        iconSize = IconSize.MEDIUM,
                        showLabel = false,
                        onClick = { viewModel.launchApp(app.packageName) },
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        // Onboarding dialog
        if (uiState.showOnboarding) {
            AlertDialog(
                onDismissRequest = { viewModel.dismissOnboarding() },
                title = {
                    Text(
                        text = stringResource(R.string.onboarding_title),
                        style = MaterialTheme.typography.headlineSmall,
                    )
                },
                text = {
                    Text(
                        text = stringResource(R.string.onboarding_message),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                },
                confirmButton = {
                    TextButton(onClick = { viewModel.dismissOnboarding() }) {
                        Text(stringResource(R.string.onboarding_ok))
                    }
                },
            )
        }
    }
}

@Composable
private fun CategoryPager(
    uiState: HomeUiState,
    onLaunchApp: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val categories = uiState.categories
    val pagerState = rememberPagerState(pageCount = { categories.size })
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = modifier) {
        // Category tabs - synced with pager
        CategoryTabRow(
            categories = categories,
            selectedCategory = categories.getOrElse(pagerState.currentPage) { categories.first() },
            onCategorySelected = { category ->
                val index = categories.indexOf(category)
                if (index >= 0) {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            },
        )

        // Horizontal pager - one page per category
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) { page ->
            val category = categories[page]
            val apps = uiState.appsByCategory[category] ?: emptyList()

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                items(
                    items = apps,
                    key = { it.packageName },
                ) { app ->
                    AppIcon(
                        appName = app.appName,
                        icon = app.icon,
                        iconSize = IconSize.MEDIUM,
                        onClick = { onLaunchApp(app.packageName) },
                    )
                }
            }
        }
    }
}
