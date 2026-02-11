package com.harmonic.insight.launcher.ui.home

import androidx.compose.foundation.gestures.detectVerticalDragGestures
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.harmonic.insight.launcher.R
import com.harmonic.insight.launcher.ui.components.AppIcon
import com.harmonic.insight.launcher.ui.components.ClockWidget
import com.harmonic.insight.launcher.ui.components.HomeSearchBar
import com.harmonic.insight.launcher.ui.components.IconSize

@Composable
fun HomeScreen(
    onOpenDrawer: () -> Unit,
    onOpenSearch: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    if (dragAmount < -50) {
                        onOpenDrawer()
                    }
                }
            },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Clock widget
            ClockWidget(
                textColor = Color.White,
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Favorites grid (up to 8 apps, 4 columns)
            if (uiState.favorites.isNotEmpty()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                ) {
                    items(
                        items = uiState.favorites.take(8),
                        key = { it.packageName },
                    ) { app ->
                        AppIcon(
                            appName = app.appName,
                            icon = app.icon,
                            iconSize = IconSize.MEDIUM,
                            onClick = { viewModel.launchApp(app.packageName) },
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            Spacer(modifier = Modifier.weight(1f))

            // Swipe up hint
            Text(
                text = stringResource(R.string.swipe_up_hint),
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f),
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Search bar
            HomeSearchBar(
                onClick = onOpenSearch,
            )

            Spacer(modifier = Modifier.height(24.dp))

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

            Spacer(modifier = Modifier.height(16.dp))
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
