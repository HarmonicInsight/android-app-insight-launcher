package com.harmonic.insight.launcher.ui.drawer

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.harmonic.insight.launcher.R
import com.harmonic.insight.launcher.data.model.AppCategory
import com.harmonic.insight.launcher.data.model.AppInfo
import com.harmonic.insight.launcher.ui.components.AppIcon
import com.harmonic.insight.launcher.ui.components.CategoryTabRow
import com.harmonic.insight.launcher.ui.components.DrawerSearchBar
import com.harmonic.insight.launcher.ui.components.IconSize

@Composable
fun AppDrawerScreen(
    onNavigateHome: () -> Unit,
    viewModel: AppDrawerViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Surface(
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                var totalDrag = 0f
                var triggered = false
                detectVerticalDragGestures(
                    onDragStart = {
                        totalDrag = 0f
                        triggered = false
                    },
                    onVerticalDrag = { _, dragAmount ->
                        totalDrag += dragAmount
                        if (totalDrag > 100 && !triggered) {
                            triggered = true
                            onNavigateHome()
                        }
                    },
                )
            },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Search bar
            DrawerSearchBar(
                query = uiState.searchQuery,
                onQueryChange = { viewModel.updateSearchQuery(it) },
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Category tabs + view mode toggle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                CategoryTabRow(
                    categories = uiState.categories,
                    selectedCategory = uiState.selectedCategory,
                    onCategorySelected = { viewModel.selectCategory(it) },
                    modifier = Modifier.weight(1f),
                )

                IconButton(onClick = { viewModel.toggleViewMode() }) {
                    Icon(
                        imageVector = if (uiState.viewMode == "list") {
                            Icons.Default.GridView
                        } else {
                            Icons.Default.ViewList
                        },
                        contentDescription = stringResource(R.string.toggle_view_mode),
                    )
                }
            }

            // App list or grid
            if (uiState.isLoading) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.viewMode == "list") {
                AppListView(
                    apps = uiState.apps,
                    onAppClick = { viewModel.launchApp(it.packageName) },
                    onAppLongClick = { /* handled by context menu */ },
                    onCategoryChange = { pkg, cat -> viewModel.updateAppCategory(pkg, cat) },
                    onAppInfo = { viewModel.openAppInfo(it) },
                    onUninstall = { viewModel.uninstallApp(it) },
                    modifier = Modifier.weight(1f),
                )
            } else {
                AppGridView(
                    apps = uiState.apps,
                    onAppClick = { viewModel.launchApp(it.packageName) },
                    onAppLongClick = { /* handled by context menu */ },
                    onCategoryChange = { pkg, cat -> viewModel.updateAppCategory(pkg, cat) },
                    onAppInfo = { viewModel.openAppInfo(it) },
                    onUninstall = { viewModel.uninstallApp(it) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AppListView(
    apps: List<AppInfo>,
    onAppClick: (AppInfo) -> Unit,
    onAppLongClick: (AppInfo) -> Unit,
    onCategoryChange: (String, AppCategory) -> Unit,
    onAppInfo: (String) -> Unit,
    onUninstall: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.padding(horizontal = 8.dp),
    ) {
        items(
            items = apps,
            key = { it.packageName },
        ) { app ->
            AppListItem(
                app = app,
                onClick = { onAppClick(app) },
                onCategoryChange = { onCategoryChange(app.packageName, it) },
                onAppInfo = { onAppInfo(app.packageName) },
                onUninstall = { onUninstall(app.packageName) },
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AppListItem(
    app: AppInfo,
    onClick: () -> Unit,
    onCategoryChange: (AppCategory) -> Unit,
    onAppInfo: () -> Unit,
    onUninstall: () -> Unit,
) {
    var showContextMenu by remember { mutableStateOf(false) }
    var showCategoryPicker by remember { mutableStateOf(false) }

    Box {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = { showContextMenu = true },
                )
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            AppIcon(
                appName = app.appName,
                icon = app.icon,
                iconSize = IconSize.SMALL,
                showLabel = false,
                onClick = onClick,
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = app.appName,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
            )
        }

        // Context menu
        DropdownMenu(
            expanded = showContextMenu,
            onDismissRequest = { showContextMenu = false },
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.change_category)) },
                onClick = {
                    showContextMenu = false
                    showCategoryPicker = true
                },
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.app_info)) },
                onClick = {
                    showContextMenu = false
                    onAppInfo()
                },
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.uninstall)) },
                onClick = {
                    showContextMenu = false
                    onUninstall()
                },
            )
        }

        // Category picker
        DropdownMenu(
            expanded = showCategoryPicker,
            onDismissRequest = { showCategoryPicker = false },
        ) {
            AppCategory.entries.forEach { category ->
                DropdownMenuItem(
                    text = { Text("${category.icon} ${category.displayName}") },
                    onClick = {
                        showCategoryPicker = false
                        onCategoryChange(category)
                    },
                )
            }
        }
    }
}

@Composable
private fun AppGridView(
    apps: List<AppInfo>,
    onAppClick: (AppInfo) -> Unit,
    onAppLongClick: (AppInfo) -> Unit,
    onCategoryChange: (String, AppCategory) -> Unit,
    onAppInfo: (String) -> Unit,
    onUninstall: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        items(
            items = apps,
            key = { it.packageName },
        ) { app ->
            AppIcon(
                appName = app.appName,
                icon = app.icon,
                iconSize = IconSize.MEDIUM,
                onClick = { onAppClick(app) },
                onLongClick = { onAppLongClick(app) },
            )
        }
    }
}
