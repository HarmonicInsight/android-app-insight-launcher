package com.harmonic.insight.launcher.ui.drawer

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.harmonic.insight.launcher.R
import com.harmonic.insight.launcher.data.model.AppCategory
import com.harmonic.insight.launcher.data.model.AppInfo
import com.harmonic.insight.launcher.ui.components.AppIcon
import com.harmonic.insight.launcher.ui.components.DrawerSearchBar
import com.harmonic.insight.launcher.ui.components.IconSize
import kotlinx.coroutines.launch

@Composable
fun AppDrawerScreen(
    onNavigateHome: () -> Unit,
    viewModel: AppDrawerViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val gridState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()

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
        Row(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
        ) {
            // Main content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Search bar
                DrawerSearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = { viewModel.updateSearchQuery(it) },
                )

                Spacer(modifier = Modifier.height(8.dp))

                // A-Z grid with section headers
                if (uiState.isLoading) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                    ) {
                        androidx.compose.material3.CircularProgressIndicator()
                    }
                } else {
                    // Build flat list of items for grid
                    val gridItems = buildGridItems(uiState.sections)

                    LazyVerticalGrid(
                        state = gridState,
                        columns = GridCells.Fixed(4),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                    ) {
                        for (item in gridItems) {
                            when (item) {
                                is GridItem.Header -> {
                                    item(
                                        span = { GridItemSpan(4) },
                                        key = "header_${item.title}",
                                    ) {
                                        SectionHeader(title = item.title)
                                    }
                                }
                                is GridItem.App -> {
                                    item(key = item.appInfo.packageName) {
                                        DrawerAppIcon(
                                            app = item.appInfo,
                                            onAppClick = { viewModel.launchApp(item.appInfo.packageName) },
                                            onCategoryChange = { cat ->
                                                viewModel.updateAppCategory(item.appInfo.packageName, cat)
                                            },
                                            onAppInfo = { viewModel.openAppInfo(item.appInfo.packageName) },
                                            onUninstall = { viewModel.uninstallApp(item.appInfo.packageName) },
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Side alphabet bar
            if (uiState.sections.isNotEmpty()) {
                AlphabetSideBar(
                    headers = uiState.sectionHeaders,
                    allItems = buildGridItems(uiState.sections),
                    onHeaderSelected = { headerIndex ->
                        coroutineScope.launch {
                            gridState.animateScrollToItem(headerIndex)
                        }
                    },
                )
            }
        }
    }
}

// ===== Grid item model =====

private sealed class GridItem {
    data class Header(val title: String) : GridItem()
    data class App(val appInfo: AppInfo) : GridItem()
}

private fun buildGridItems(sections: List<AppSection>): List<GridItem> {
    val items = mutableListOf<GridItem>()
    for (section in sections) {
        items.add(GridItem.Header(section.header))
        for (app in section.apps) {
            items.add(GridItem.App(app))
        }
    }
    return items
}

// ===== Section header =====

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 4.dp, start = 4.dp),
    )
}

// ===== Side alphabet bar =====

@Composable
private fun AlphabetSideBar(
    headers: List<String>,
    allItems: List<GridItem>,
    onHeaderSelected: (Int) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxHeight()
            .width(24.dp)
            .padding(vertical = 48.dp),
    ) {
        headers.forEach { header ->
            val gridIndex = allItems.indexOfFirst {
                it is GridItem.Header && it.title == header
            }
            Text(
                text = header,
                fontSize = if (headers.size > 20) 9.sp else 11.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .clickable {
                        if (gridIndex >= 0) onHeaderSelected(gridIndex)
                    }
                    .padding(vertical = 1.dp),
            )
        }
    }
}

// ===== Drawer app icon with long-press menu =====

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DrawerAppIcon(
    app: AppInfo,
    onAppClick: () -> Unit,
    onCategoryChange: (AppCategory) -> Unit,
    onAppInfo: () -> Unit,
    onUninstall: () -> Unit,
) {
    var showContextMenu by remember { mutableStateOf(false) }
    var showCategoryPicker by remember { mutableStateOf(false) }
    var showUninstallConfirm by remember { mutableStateOf(false) }

    Box {
        AppIcon(
            appName = app.appName,
            icon = app.icon,
            iconSize = IconSize.MEDIUM,
            onClick = onAppClick,
            onLongClick = { showContextMenu = true },
        )

        // Context menu
        DropdownMenu(
            expanded = showContextMenu,
            onDismissRequest = { showContextMenu = false },
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.move_to_category)) },
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
                    showUninstallConfirm = true
                },
            )
        }

        // Category picker
        DropdownMenu(
            expanded = showCategoryPicker,
            onDismissRequest = { showCategoryPicker = false },
        ) {
            AppCategory.entries.forEach { category ->
                val isSelected = category == app.category
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "${category.icon} ${category.displayName}" +
                                if (isSelected) " ✓" else "",
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        )
                    },
                    onClick = {
                        showCategoryPicker = false
                        onCategoryChange(category)
                    },
                )
            }
        }
    }

    // Uninstall confirmation dialog
    if (showUninstallConfirm) {
        AlertDialog(
            onDismissRequest = { showUninstallConfirm = false },
            title = { Text(stringResource(R.string.confirm_uninstall_title)) },
            text = { Text(stringResource(R.string.confirm_uninstall_message, app.appName)) },
            confirmButton = {
                TextButton(onClick = {
                    showUninstallConfirm = false
                    onUninstall()
                }) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showUninstallConfirm = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }
}
