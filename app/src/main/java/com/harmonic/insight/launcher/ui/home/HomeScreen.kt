package com.harmonic.insight.launcher.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.harmonic.insight.launcher.R
import com.harmonic.insight.launcher.data.model.AppCategory
import com.harmonic.insight.launcher.data.model.AppInfo
import com.harmonic.insight.launcher.data.model.FolderInfo
import com.harmonic.insight.launcher.ui.components.AppIcon
import com.harmonic.insight.launcher.ui.components.CategoryTabRow
import com.harmonic.insight.launcher.ui.components.ClockWidget
import com.harmonic.insight.launcher.ui.components.HomeSearchBar
import com.harmonic.insight.launcher.ui.components.IconSize
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private data class DragInfo(
    val app: AppInfo,
    val currentPosition: Offset,
    val totalDistance: Float = 0f,
)

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
    var showFolderPicker by remember { mutableStateOf(false) }

    // Dock edit state
    var dockEditPosition by remember { mutableIntStateOf(-1) }
    var showDockAppPicker by remember { mutableStateOf(false) }

    // Swipe-to-drawer state
    var totalDrag by remember { mutableStateOf(0f) }
    var drawerOpened by remember { mutableStateOf(false) }

    // Drag-and-drop state
    var dragInfo by remember { mutableStateOf<DragInfo?>(null) }
    val folderBounds = remember { mutableStateMapOf<Long, Rect>() }
    var addButtonBounds by remember { mutableStateOf(Rect.Zero) }
    val isDragging = dragInfo != null

    // Folder dialog state
    var showCreateFolder by remember { mutableStateOf(false) }
    var pendingAppForNewFolder by remember { mutableStateOf<AppInfo?>(null) }
    var openFolder by remember { mutableStateOf<FolderInfo?>(null) }
    var editFolder by remember { mutableStateOf<FolderInfo?>(null) }
    var showDeleteFolderConfirm by remember { mutableStateOf(false) }

    // Determine hovered folder during drag
    val hoveredFolderId = if (isDragging) {
        folderBounds.entries.find { (_, bounds) ->
            bounds.contains(dragInfo!!.currentPosition)
        }?.key
    } else null
    val isHoveringAddButton = isDragging && addButtonBounds.contains(dragInfo!!.currentPosition)

    val iconSizePx = with(LocalDensity.current) { IconSize.MEDIUM.sizeDp.toPx() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(isDragging) {
                if (!isDragging) {
                    detectVerticalDragGestures(
                        onDragStart = {
                            totalDrag = 0f
                            drawerOpened = false
                        },
                        onDragEnd = { totalDrag = 0f },
                        onDragCancel = { totalDrag = 0f },
                    ) { _, dragAmount ->
                        totalDrag += dragAmount
                        if (totalDrag < -100f && !drawerOpened) {
                            drawerOpened = true
                            onOpenDrawer()
                        }
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

            Spacer(modifier = Modifier.height(12.dp))

            // Folder row
            FolderRow(
                folders = uiState.folders,
                isDragging = isDragging,
                hoveredFolderId = hoveredFolderId,
                isHoveringAddButton = isHoveringAddButton,
                onFolderClick = { folder -> openFolder = folder },
                onFolderLongClick = { folder -> editFolder = folder },
                onAddClick = { showCreateFolder = true },
                onFolderPositioned = { folderId, bounds ->
                    folderBounds[folderId] = bounds
                },
                onAddButtonPositioned = { bounds -> addButtonBounds = bounds },
            )

            Spacer(modifier = Modifier.height(8.dp))

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
                    onDragStart = { app, position ->
                        dragInfo = DragInfo(app = app, currentPosition = position)
                    },
                    onDragMove = { amount ->
                        dragInfo = dragInfo?.let {
                            it.copy(
                                currentPosition = it.currentPosition + amount,
                                totalDistance = it.totalDistance + amount.getDistance(),
                            )
                        }
                    },
                    onDragEnd = {
                        val info = dragInfo
                        if (info != null) {
                            if (info.totalDistance < 30f) {
                                contextMenuApp = info.app
                            } else if (hoveredFolderId != null) {
                                viewModel.addAppToFolder(hoveredFolderId, info.app.packageName)
                            } else if (isHoveringAddButton) {
                                pendingAppForNewFolder = info.app
                                showCreateFolder = true
                            }
                        }
                        dragInfo = null
                    },
                    onDragCancel = { dragInfo = null },
                    modifier = Modifier.weight(1f),
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            // Drawer handle indicator
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

            // Dock
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

        // ===== Drag overlay =====
        if (dragInfo != null) {
            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            (dragInfo!!.currentPosition.x - iconSizePx / 2).roundToInt(),
                            (dragInfo!!.currentPosition.y - iconSizePx).roundToInt(),
                        )
                    }
                    .alpha(0.85f),
            ) {
                AppIcon(
                    appName = dragInfo!!.app.appName,
                    icon = dragInfo!!.app.icon,
                    iconSize = IconSize.LARGE,
                    showLabel = false,
                    interactive = false,
                )
            }
        }

        // ===== App context menu =====
        if (contextMenuApp != null) {
            AppContextMenu(
                app = contextMenuApp!!,
                onDismiss = { contextMenuApp = null },
                onChangeCategory = { showCategoryPicker = true },
                onAddToFolder = { showFolderPicker = true },
                onAppInfo = {
                    viewModel.openAppInfo(contextMenuApp!!.packageName)
                    contextMenuApp = null
                },
                onUninstall = { showUninstallConfirm = true },
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

        // ===== Folder picker (from context menu) =====
        if (showFolderPicker && contextMenuApp != null) {
            FolderPickerDialog(
                folders = uiState.folders,
                onDismiss = {
                    showFolderPicker = false
                    contextMenuApp = null
                },
                onFolderSelected = { folderId ->
                    viewModel.addAppToFolder(folderId, contextMenuApp!!.packageName)
                    showFolderPicker = false
                    contextMenuApp = null
                },
                onCreateNewFolder = {
                    pendingAppForNewFolder = contextMenuApp
                    showFolderPicker = false
                    contextMenuApp = null
                    showCreateFolder = true
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

        // ===== Create folder dialog =====
        if (showCreateFolder) {
            CreateFolderDialog(
                onDismiss = {
                    showCreateFolder = false
                    pendingAppForNewFolder = null
                },
                onConfirm = { name ->
                    val pendingApp = pendingAppForNewFolder
                    if (pendingApp != null) {
                        viewModel.createFolderWithApp(name, pendingApp.packageName)
                    } else {
                        viewModel.createFolder(name)
                    }
                    showCreateFolder = false
                    pendingAppForNewFolder = null
                },
            )
        }

        // ===== Folder content dialog =====
        if (openFolder != null) {
            // Keep reference in sync with uiState
            val currentFolder = uiState.folders.find { it.id == openFolder!!.id } ?: openFolder!!
            FolderContentDialog(
                folder = currentFolder,
                onDismiss = { openFolder = null },
                onLaunchApp = { packageName ->
                    viewModel.launchApp(packageName)
                    openFolder = null
                },
                onRemoveApp = { packageName ->
                    viewModel.removeAppFromFolder(currentFolder.id, packageName)
                },
            )
        }

        // ===== Folder edit dialog =====
        if (editFolder != null && !showDeleteFolderConfirm) {
            FolderEditDialog(
                folder = editFolder!!,
                onDismiss = { editFolder = null },
                onRename = { newName ->
                    viewModel.renameFolder(editFolder!!.id, newName)
                    editFolder = null
                },
                onDelete = { showDeleteFolderConfirm = true },
            )
        }

        // ===== Delete folder confirmation =====
        if (showDeleteFolderConfirm && editFolder != null) {
            AlertDialog(
                onDismissRequest = {
                    showDeleteFolderConfirm = false
                    editFolder = null
                },
                title = { Text(stringResource(R.string.folder_delete_confirm_title)) },
                text = {
                    Text(stringResource(R.string.folder_delete_confirm_message, editFolder!!.name))
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.deleteFolder(editFolder!!.id)
                        showDeleteFolderConfirm = false
                        editFolder = null
                    }) {
                        Text(
                            text = stringResource(R.string.folder_delete),
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showDeleteFolderConfirm = false
                        editFolder = null
                    }) {
                        Text(stringResource(R.string.cancel))
                    }
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

// ===== Folder Row =====

@Composable
private fun FolderRow(
    folders: List<FolderInfo>,
    isDragging: Boolean,
    hoveredFolderId: Long?,
    isHoveringAddButton: Boolean,
    onFolderClick: (FolderInfo) -> Unit,
    onFolderLongClick: (FolderInfo) -> Unit,
    onAddClick: () -> Unit,
    onFolderPositioned: (Long, Rect) -> Unit,
    onAddButtonPositioned: (Rect) -> Unit,
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        items(
            items = folders,
            key = { it.id },
        ) { folder ->
            FolderChip(
                folder = folder,
                isHovered = hoveredFolderId == folder.id,
                isDragging = isDragging,
                onClick = { onFolderClick(folder) },
                onLongClick = { onFolderLongClick(folder) },
                modifier = Modifier.onGloballyPositioned { coords ->
                    onFolderPositioned(folder.id, coords.boundsInRoot())
                },
            )
        }
        item(key = "add_folder") {
            val addBgColor = when {
                isHoveringAddButton -> Color.White.copy(alpha = 0.4f)
                isDragging -> Color.White.copy(alpha = 0.25f)
                else -> Color.White.copy(alpha = 0.15f)
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .height(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(addBgColor)
                    .clickable { onAddClick() }
                    .padding(horizontal = 14.dp)
                    .onGloballyPositioned { coords ->
                        onAddButtonPositioned(coords.boundsInRoot())
                    },
            ) {
                Icon(
                    imageVector = if (isDragging) Icons.Default.CreateNewFolder else Icons.Default.Add,
                    contentDescription = stringResource(R.string.folder_create),
                    tint = Color.White,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FolderChip(
    folder: FolderInfo,
    isHovered: Boolean,
    isDragging: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bgColor = when {
        isHovered -> Color.White.copy(alpha = 0.4f)
        isDragging -> Color.White.copy(alpha = 0.25f)
        else -> Color.White.copy(alpha = 0.15f)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .height(40.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
            )
            .padding(horizontal = 12.dp),
    ) {
        Icon(
            imageVector = Icons.Default.Folder,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(18.dp),
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = folder.name,
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (folder.apps.isNotEmpty()) {
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "${folder.apps.size}",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 11.sp,
            )
        }
        if (isHovered) {
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = stringResource(R.string.folder_drop_hint),
                color = Color.White,
                fontSize = 11.sp,
            )
        }
    }
}

// ===== Category pager =====

@Composable
private fun CategoryPager(
    uiState: HomeUiState,
    onLaunchApp: (String) -> Unit,
    onLongClickApp: (AppInfo) -> Unit,
    onDragStart: (AppInfo, Offset) -> Unit,
    onDragMove: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit,
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
                onDragStart = onDragStart,
                onDragMove = onDragMove,
                onDragEnd = onDragEnd,
                onDragCancel = onDragCancel,
            )
        }
    }
}

@Composable
private fun CategoryPageContent(
    groups: List<SubCategoryGroup>,
    onLaunchApp: (String) -> Unit,
    onLongClickApp: (AppInfo) -> Unit,
    onDragStart: (AppInfo, Offset) -> Unit,
    onDragMove: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit,
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
                DraggableAppItem(
                    app = app,
                    onLaunchApp = onLaunchApp,
                    onDragStart = onDragStart,
                    onDragMove = onDragMove,
                    onDragEnd = onDragEnd,
                    onDragCancel = onDragCancel,
                )
            }
        }
    }
}

@Composable
private fun DraggableAppItem(
    app: AppInfo,
    onLaunchApp: (String) -> Unit,
    onDragStart: (AppInfo, Offset) -> Unit,
    onDragMove: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit,
) {
    var itemCoords by remember { mutableStateOf<LayoutCoordinates?>(null) }

    Box(
        modifier = Modifier
            .onGloballyPositioned { itemCoords = it }
            .clickable { onLaunchApp(app.packageName) }
            .pointerInput(app.packageName) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { offset ->
                        val rootPos = itemCoords?.positionInRoot() ?: Offset.Zero
                        onDragStart(app, rootPos + offset)
                    },
                    onDrag = { change, amount ->
                        change.consume()
                        onDragMove(amount)
                    },
                    onDragEnd = onDragEnd,
                    onDragCancel = onDragCancel,
                )
            },
    ) {
        AppIcon(
            appName = app.appName,
            icon = app.icon,
            iconSize = IconSize.MEDIUM,
            labelColor = Color.White,
            interactive = false,
        )
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

// ===== App long-press context menu =====

@Composable
private fun AppContextMenu(
    app: AppInfo,
    onDismiss: () -> Unit,
    onChangeCategory: () -> Unit,
    onAddToFolder: () -> Unit,
    onAppInfo: () -> Unit,
    onUninstall: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(app.appName, style = MaterialTheme.typography.titleMedium) },
        text = {
            Column {
                TextButton(
                    onClick = onAddToFolder,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = stringResource(R.string.add_to_folder),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
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

// ===== Folder picker dialog =====

@Composable
private fun FolderPickerDialog(
    folders: List<FolderInfo>,
    onDismiss: () -> Unit,
    onFolderSelected: (Long) -> Unit,
    onCreateNewFolder: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.add_to_folder)) },
        text = {
            Column {
                TextButton(
                    onClick = onCreateNewFolder,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(
                            imageVector = Icons.Default.CreateNewFolder,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = stringResource(R.string.folder_create))
                    }
                }
                if (folders.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 300.dp),
                    ) {
                        items(
                            items = folders,
                            key = { it.id },
                        ) { folder ->
                            TextButton(
                                onClick = { onFolderSelected(folder.id) },
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Folder,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp),
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = folder.name)
                                    if (folder.apps.isNotEmpty()) {
                                        Text(
                                            text = " (${folder.apps.size})",
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                }
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

// ===== Create folder dialog =====

@Composable
private fun CreateFolderDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    val defaultName = stringResource(R.string.folder_new_name)
    var folderName by remember { mutableStateOf(defaultName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.folder_create)) },
        text = {
            OutlinedTextField(
                value = folderName,
                onValueChange = { folderName = it },
                label = { Text(stringResource(R.string.folder_name_hint)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val name = folderName.trim()
                    if (name.isNotEmpty()) {
                        onConfirm(name)
                    }
                },
                enabled = folderName.trim().isNotEmpty(),
            ) {
                Text(stringResource(R.string.create))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
    )
}

// ===== Folder content dialog =====

@Composable
private fun FolderContentDialog(
    folder: FolderInfo,
    onDismiss: () -> Unit,
    onLaunchApp: (String) -> Unit,
    onRemoveApp: (String) -> Unit,
) {
    var removeConfirmApp by remember { mutableStateOf<AppInfo?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(folder.name, style = MaterialTheme.typography.titleMedium) },
        text = {
            if (folder.apps.isEmpty()) {
                Text(
                    text = stringResource(R.string.folder_empty),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 16.dp),
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.heightIn(max = 300.dp),
                ) {
                    items(
                        items = folder.apps,
                        key = { it.packageName },
                    ) { app ->
                        AppIcon(
                            appName = app.appName,
                            icon = app.icon,
                            iconSize = IconSize.MEDIUM,
                            onClick = { onLaunchApp(app.packageName) },
                            onLongClick = { removeConfirmApp = app },
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

    if (removeConfirmApp != null) {
        AlertDialog(
            onDismissRequest = { removeConfirmApp = null },
            title = { Text(removeConfirmApp!!.appName) },
            text = { Text(stringResource(R.string.folder_remove_app)) },
            confirmButton = {
                TextButton(onClick = {
                    onRemoveApp(removeConfirmApp!!.packageName)
                    removeConfirmApp = null
                }) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { removeConfirmApp = null }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }
}

// ===== Folder edit dialog (rename/delete) =====

@Composable
private fun FolderEditDialog(
    folder: FolderInfo,
    onDismiss: () -> Unit,
    onRename: (String) -> Unit,
    onDelete: () -> Unit,
) {
    var folderName by remember(folder.id) { mutableStateOf(folder.name) }
    var isRenaming by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(folder.name, style = MaterialTheme.typography.titleMedium) },
        text = {
            Column {
                if (isRenaming) {
                    OutlinedTextField(
                        value = folderName,
                        onValueChange = { folderName = it },
                        label = { Text(stringResource(R.string.folder_name_hint)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = {
                            val name = folderName.trim()
                            if (name.isNotEmpty()) {
                                onRename(name)
                            }
                        },
                        enabled = folderName.trim().isNotEmpty(),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = stringResource(R.string.save),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                } else {
                    TextButton(
                        onClick = { isRenaming = true },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = stringResource(R.string.folder_rename),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    TextButton(
                        onClick = onDelete,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = stringResource(R.string.folder_delete),
                            color = MaterialTheme.colorScheme.error,
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
