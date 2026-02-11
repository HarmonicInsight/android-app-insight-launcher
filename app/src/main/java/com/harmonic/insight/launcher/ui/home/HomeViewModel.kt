package com.harmonic.insight.launcher.ui.home

import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harmonic.insight.launcher.R
import com.harmonic.insight.launcher.data.local.entity.DockEntity
import com.harmonic.insight.launcher.data.model.AppCategory
import com.harmonic.insight.launcher.data.model.AppInfo
import com.harmonic.insight.launcher.data.repository.AppRepository
import com.harmonic.insight.launcher.data.repository.CategoryRepository
import com.harmonic.insight.launcher.domain.usecase.LaunchAppUseCase
import com.harmonic.insight.launcher.util.PackageUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * サブカテゴリごとのアプリグループ
 */
data class SubCategoryGroup(
    val subCategory: AppCategory?,
    val apps: List<AppInfo>,
)

data class HomeUiState(
    val topLevelCategories: List<AppCategory> = emptyList(),
    /** トップレベルカテゴリ → サブカテゴリグループのリスト */
    val groupedApps: Map<AppCategory, List<SubCategoryGroup>> = emptyMap(),
    val dockApps: List<AppInfo> = emptyList(),
    val isLoading: Boolean = true,
    val showOnboarding: Boolean = false,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appRepository: AppRepository,
    private val categoryRepository: CategoryRepository,
    private val launchAppUseCase: LaunchAppUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val packageManager: PackageManager = context.packageManager

    init {
        // One-time initialization
        viewModelScope.launch {
            appRepository.refreshInstalledApps()
            setupInitialDock()
            checkOnboarding()
        }

        // Long-running: observe categorized apps reactively
        viewModelScope.launch {
            loadCategorizedApps()
        }

        // Long-running: observe dock apps reactively
        viewModelScope.launch {
            categoryRepository.getDockApps().collect { dock ->
                val dockApps = withContext(Dispatchers.IO) {
                    dock.mapNotNull { d -> loadAppInfo(d.packageName) }
                }
                _uiState.value = _uiState.value.copy(dockApps = dockApps)
            }
        }
    }

    fun launchApp(packageName: String) {
        viewModelScope.launch {
            val launched = launchAppUseCase(packageName)
            if (!launched) {
                Toast.makeText(context, R.string.launch_failed, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun dismissOnboarding() {
        viewModelScope.launch {
            categoryRepository.setOnboardingCompleted()
            _uiState.value = _uiState.value.copy(showOnboarding = false)
        }
    }

    private suspend fun loadCategorizedApps() {
        appRepository.getAllAppsWithIcons().collect { allApps ->
            // アプリをトップレベルカテゴリでグループ化
            val byTopLevel = allApps.groupBy { AppCategory.topLevelOf(it.category) }

            val categoryOrder = categoryRepository.getCategoryOrder()
                .filter { AppCategory.isTopLevel(it) }
            val availableCategories = categoryOrder.filter { it in byTopLevel.keys }

            // 各トップレベルカテゴリ内をサブカテゴリでさらにグループ化
            val groupedApps = availableCategories.associateWith { topLevel ->
                val appsInCategory = byTopLevel[topLevel] ?: emptyList()
                val subCategories = AppCategory.HIERARCHY[topLevel]

                if (subCategories != null && subCategories.isNotEmpty()) {
                    // サブカテゴリがある場合：サブカテゴリごとにグループ化
                    val subGroups = mutableListOf<SubCategoryGroup>()

                    // 親カテゴリ直属のアプリ（サブカテゴリ未割り当て）
                    val directApps = appsInCategory.filter { it.category == topLevel }
                    if (directApps.isNotEmpty()) {
                        subGroups.add(SubCategoryGroup(subCategory = null, apps = directApps))
                    }

                    // 各サブカテゴリのアプリ
                    for (sub in subCategories) {
                        val subApps = appsInCategory.filter { it.category == sub }
                        if (subApps.isNotEmpty()) {
                            subGroups.add(SubCategoryGroup(subCategory = sub, apps = subApps))
                        }
                    }

                    subGroups
                } else {
                    // サブカテゴリなし：フラットにリスト
                    listOf(SubCategoryGroup(subCategory = null, apps = appsInCategory))
                }
            }

            _uiState.value = _uiState.value.copy(
                topLevelCategories = availableCategories,
                groupedApps = groupedApps,
                isLoading = false,
            )
        }
    }

    private suspend fun setupInitialDock() {
        val currentDock = categoryRepository.getDockApps().first()
        if (currentDock.isNotEmpty()) return

        val dockApps = mutableListOf<DockEntity>()
        PackageUtils.getDefaultPhonePackage(context)?.let {
            dockApps.add(DockEntity(0, it))
        }
        PackageUtils.getDefaultCameraPackage(context)?.let {
            dockApps.add(DockEntity(1, it))
        }
        PackageUtils.getDefaultBrowserPackage(context)?.let {
            dockApps.add(DockEntity(2, it))
        }
        dockApps.add(DockEntity(3, PackageUtils.getSettingsPackage()))

        categoryRepository.setDockApps(dockApps)
    }

    private suspend fun checkOnboarding() {
        if (!categoryRepository.isOnboardingCompleted()) {
            _uiState.value = _uiState.value.copy(showOnboarding = true)
        }
    }

    private fun loadAppInfo(packageName: String): AppInfo? {
        return try {
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            val appName = appInfo.loadLabel(packageManager).toString()
            val icon = packageManager.getApplicationIcon(packageName)
            AppInfo(
                packageName = packageName,
                appName = appName,
                icon = icon,
                category = AppCategory.OTHER,
            )
        } catch (_: Exception) {
            null
        }
    }
}
