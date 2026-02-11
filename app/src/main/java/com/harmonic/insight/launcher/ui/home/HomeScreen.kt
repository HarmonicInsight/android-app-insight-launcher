package com.harmonic.insight.launcher.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.harmonic.insight.launcher.R
import com.harmonic.insight.launcher.data.model.AppCategory
import com.harmonic.insight.launcher.data.model.AppInfo
import com.harmonic.insight.launcher.ui.components.AppIcon
import com.harmonic.insight.launcher.ui.components.CategoryTabRow
import com.harmonic.insight.launcher.ui.components.ClockWidget
import com.harmonic.insight.launcher.ui.components.HomeSearchBar
import com.harmonic.insight.launcher.ui.components.IconSize

@Composable
fun HomeScreen(
    onOpenSearch: () -> Unit,
    onOpenDrawer: () -> Unit,
    onOpenSettings: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    // App context menu state
    var contextMenuApp by remember { mutableStateOf<AppInfo?>(null) }
    var showCategoryPicker by remember { mutableStateOf(false) }
    var showUninstallConfirm by remember { mutableStateOf(false) }

    // Dock edit state
    var dockEditPosition by remember { mutableIntStateOf(-1) }
    var showDockAppPicker by remember { mutableStateOf(false) }

    var totalDrag by remember { mutableStateOf(0f) }
    var drawerOpened by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragStart = {
                        totalDrag = 0f
                        drawerOpened = false
                    },
                    onDragEnd = { totalDrag = 0f },
                    onDragCancel = { totalDrag = 0f },
                ) { _, dragAmount ->
                    totalDrag += dragAmount
                    // Swipe up (accumulated negative drag > 100px) to open drawer
                    if (totalDrag < -100f && !drawerOpened) {
                        drawerOpened = true
                        onOpenDrawer()
                    }
                }
            },
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
            ClockWidget(textColor = Color.White)

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
            } else if (uiState.topLevelCategories.isNotEmpty()) {
                CategoryPager(
                    uiState = uiState,
                    onLaunchApp = { viewModel.launchApp(it) },
                    onLongClickApp = { app -> contextMenuApp = app },
                    modifier = Modifier.weight(1f),
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            // Drawer handle indicator (tap or swipe up)
            Box(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .clickable { onOpenDrawer() },
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .width(32.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.White.copy(alpha = 0.5f)),
                )
            }

            HomeSearchBar(onClick = onOpenSearch)

            Spacer(modifier = Modifier.height(16.dp))

            // Dock (long-press to edit)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                uiState.dockApps.forEachIndexed { index, app ->
                    AppIcon(
                        appName = app.appName,
                        icon = app.icon,
                        iconSize = IconSize.MEDIUM,
                        showLabel = false,
                        onClick = { viewModel.launchApp(app.packageName) },
                        onLongClick = {
                            dockEditPosition = index
                            showDockAppPicker = true
                        },
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        // ===== App context menu =====
        if (contextMenuApp != null) {
            AppContextMenu(
                app = contextMenuApp!!,
                onDismiss = { contextMenuApp = null },
                onChangeCategory = {
                    showCategoryPicker = true
                },
                onAppInfo = {
                    viewModel.openAppInfo(contextMenuApp!!.packageName)
                    contextMenuApp = null
                },
                onUninstall = {
                    showUninstallConfirm = true
                },
            )
        }

        // ===== Category picker dialog =====
        if (showCategoryPicker && contextMenuApp != null) {
            CategoryPickerDialog(
                currentCategory = contextMenuApp!!.category,
                onDismiss = {
                    showCategoryPicker = false
                    contextMenuApp = null
                },
                onCategorySelected = { newCategory ->
                    viewModel.updateAppCategory(contextMenuApp!!.packageName, newCategory)
                    showCategoryPicker = false
                    contextMenuApp = null
                },
            )
        }

        // ===== Uninstall confirm =====
        if (showUninstallConfirm && contextMenuApp != null) {
            AlertDialog(
                onDismissRequest = {
                    showUninstallConfirm = false
                    contextMenuApp = null
                },
                title = { Text(stringResource(R.string.confirm_uninstall_title)) },
                text = {
                    Text(stringResource(R.string.confirm_uninstall_message, contextMenuApp!!.appName))
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.uninstallApp(contextMenuApp!!.packageName)
                        showUninstallConfirm = false
                        contextMenuApp = null
                    }) {
                        Text(stringResource(R.string.confirm))
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showUninstallConfirm = false
                        contextMenuApp = null
                    }) {
                        Text(stringResource(R.string.cancel))
                    }
                },
            )
        }

        // ===== Dock app picker dialog =====
        if (showDockAppPicker && dockEditPosition >= 0) {
            DockAppPickerDialog(
                allApps = uiState.allApps,
                onDismiss = {
                    showDockAppPicker = false
                    dockEditPosition = -1
                },
                onAppSelected = { packageName ->
                    viewModel.replaceDockApp(dockEditPosition, packageName)
                    showDockAppPicker = false
                    dockEditPosition = -1
                },
                onRemove = {
                    viewModel.removeDockApp(dockEditPosition)
                    showDockAppPicker = false
                    dockEditPosition = -1
                },
            )
        }

        // ===== Onboarding dialog =====
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

// ===== App long-press context menu =====

@Composable
private fun AppContextMenu(
    app: AppInfo,
    onDismiss: () -> Unit,
    onChangeCategory: () -> Unit,
    onAppInfo: () -> Unit,
    onUninstall: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(app.appName, style = MaterialTheme.typography.titleMedium) },
        text = {
            Column {
                TextButton(
                    onClick = onChangeCategory,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = stringResource(R.string.move_to_category),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                TextButton(
                    onClick = onAppInfo,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = stringResource(R.string.app_info),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                TextButton(
                    onClick = onUninstall,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = stringResource(R.string.uninstall),
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
    )
}

// ===== Category picker dialog =====

@Composable
private fun CategoryPickerDialog(
    currentCategory: AppCategory,
    onDismiss: () -> Unit,
    onCategorySelected: (AppCategory) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.move_to_category)) },
        text = {
            LazyColumn(
                modifier = Modifier.heightIn(max = 400.dp),
            ) {
                items(AppCategory.entries.toList()) { category ->
                    val isSelected = category == currentCategory
                    TextButton(
                        onClick = { onCategorySelected(category) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = "${category.icon} ${category.displayName}" +
                                if (isSelected) " ✓" else "",
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
    )
}

// ===== Dock app picker dialog =====

@Composable
private fun DockAppPickerDialog(
    allApps: List<AppInfo>,
    onDismiss: () -> Unit,
    onAppSelected: (String) -> Unit,
    onRemove: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.dock_replace_title)) },
        text = {
            Column {
                // Remove button
                TextButton(
                    onClick = onRemove,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = stringResource(R.string.dock_remove),
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.dock_select_app),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(4.dp))

                // App list (scrollable)
                LazyColumn(
                    modifier = Modifier.heightIn(max = 350.dp),
                ) {
                    items(
                        items = allApps,
                        key = { it.packageName },
                    ) { app ->
                        TextButton(
                            onClick = { onAppSelected(app.packageName) },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                AppIcon(
                                    appName = app.appName,
                                    icon = app.icon,
                                    iconSize = IconSize.SMALL,
                                    showLabel = false,
                                )
                                Spacer(modifier = Modifier.size(12.dp))
                                Text(text = app.appName)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
    )
}

// ===== Category pager =====

@Composable
private fun CategoryPager(
    uiState: HomeUiState,
    onLaunchApp: (String) -> Unit,
    onLongClickApp: (AppInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    val categories = uiState.topLevelCategories
    val pagerState = rememberPagerState(pageCount = { categories.size })
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = modifier) {
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
            containerColor = Color.Black.copy(alpha = 0.3f),
            contentColor = Color.White,
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) { page ->
            val category = categories[page]
            val groups = uiState.groupedApps[category] ?: emptyList()

            CategoryPageContent(
                groups = groups,
                onLaunchApp = onLaunchApp,
                onLongClickApp = onLongClickApp,
            )
        }
    }
}

@Composable
private fun CategoryPageContent(
    groups: List<SubCategoryGroup>,
    onLaunchApp: (String) -> Unit,
    onLongClickApp: (AppInfo) -> Unit,
) {
    val showHeaders = groups.size > 1 || (groups.size == 1 && groups[0].subCategory != null)

    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        for (group in groups) {
            if (showHeaders && group.subCategory != null) {
                item(
                    span = { GridItemSpan(4) },
                    key = "header_${group.subCategory.name}",
                ) {
                    SubCategoryHeader(
                        icon = group.subCategory.icon,
                        name = group.subCategory.displayName,
                    )
                }
            }

            items(
                items = group.apps,
                key = { it.packageName },
            ) { app ->
                AppIcon(
                    appName = app.appName,
                    icon = app.icon,
                    iconSize = IconSize.MEDIUM,
                    labelColor = Color.White,
                    onClick = { onLaunchApp(app.packageName) },
                    onLongClick = { onLongClickApp(app) },
                )
            }
        }
    }
}

@Composable
private fun SubCategoryHeader(
    icon: String,
    name: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.15f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        Text(
            text = "$icon $name",
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}
