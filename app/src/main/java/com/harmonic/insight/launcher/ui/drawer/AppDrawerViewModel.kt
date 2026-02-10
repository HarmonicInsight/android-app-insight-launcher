package com.harmonic.insight.launcher.ui.drawer

import android.content.Context
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harmonic.insight.launcher.data.model.AppCategory
import com.harmonic.insight.launcher.data.model.AppInfo
import com.harmonic.insight.launcher.data.repository.AppRepository
import com.harmonic.insight.launcher.data.repository.CategoryRepository
import com.harmonic.insight.launcher.domain.usecase.LaunchAppUseCase
import com.harmonic.insight.launcher.util.PackageUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AppDrawerUiState(
    val categories: List<AppCategory> = emptyList(),
    val selectedCategory: AppCategory = AppCategory.COMMUNICATION,
    val apps: List<AppInfo> = emptyList(),
    val searchQuery: String = "",
    val viewMode: String = "list", // "list" or "grid"
    val isLoading: Boolean = true,
)

@HiltViewModel
class AppDrawerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appRepository: AppRepository,
    private val categoryRepository: CategoryRepository,
    private val launchAppUseCase: LaunchAppUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppDrawerUiState())
    val uiState: StateFlow<AppDrawerUiState> = _uiState.asStateFlow()

    private val packageManager: PackageManager = context.packageManager

    init {
        viewModelScope.launch {
            val viewMode = categoryRepository.getDrawerViewMode()
            val categoryOrder = categoryRepository.getCategoryOrder()
            _uiState.value = _uiState.value.copy(
                viewMode = viewMode,
                categories = categoryOrder,
            )
            loadAvailableCategories()
        }
    }

    fun selectCategory(category: AppCategory) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
        loadAppsForCategory(category)
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        if (query.isNotBlank()) {
            searchApps(query)
        } else {
            loadAppsForCategory(_uiState.value.selectedCategory)
        }
    }

    fun toggleViewMode() {
        val newMode = if (_uiState.value.viewMode == "list") "grid" else "list"
        _uiState.value = _uiState.value.copy(viewMode = newMode)
        viewModelScope.launch {
            categoryRepository.setDrawerViewMode(newMode)
        }
    }

    fun launchApp(packageName: String) {
        viewModelScope.launch {
            launchAppUseCase(packageName)
        }
    }

    fun updateAppCategory(packageName: String, newCategory: AppCategory) {
        viewModelScope.launch {
            appRepository.updateAppCategory(packageName, newCategory)
            loadAvailableCategories()
        }
    }

    fun openAppInfo(packageName: String) {
        PackageUtils.openAppInfo(context, packageName)
    }

    fun uninstallApp(packageName: String) {
        PackageUtils.uninstallApp(context, packageName)
    }

    private fun loadAvailableCategories() {
        viewModelScope.launch {
            val allApps = appRepository.getAllApps().first()
            val usedCategories = allApps.map { it.category }.toSet()
            val categoryOrder = categoryRepository.getCategoryOrder()
            val availableCategories = categoryOrder.filter { it in usedCategories }

            val selectedCategory = if (_uiState.value.selectedCategory in availableCategories) {
                _uiState.value.selectedCategory
            } else {
                availableCategories.firstOrNull() ?: AppCategory.OTHER
            }

            _uiState.value = _uiState.value.copy(
                categories = availableCategories,
                selectedCategory = selectedCategory,
                isLoading = false,
            )
            loadAppsForCategory(selectedCategory)
        }
    }

    private fun loadAppsForCategory(category: AppCategory) {
        viewModelScope.launch {
            appRepository.getAppsByCategory(category).collect { entities ->
                val apps = entities.mapNotNull { entity ->
                    try {
                        val icon = packageManager.getApplicationIcon(entity.packageName)
                        AppInfo(
                            packageName = entity.packageName,
                            appName = entity.appName,
                            icon = icon,
                            category = entity.category,
                            isUserCategorized = entity.isUserCategorized,
                            lastUsedTimestamp = entity.lastUsedTimestamp,
                        )
                    } catch (_: PackageManager.NameNotFoundException) {
                        null
                    }
                }
                _uiState.value = _uiState.value.copy(apps = apps)
            }
        }
    }

    private fun searchApps(query: String) {
        viewModelScope.launch {
            appRepository.searchApps(query).collect { entities ->
                val apps = entities.mapNotNull { entity ->
                    try {
                        val icon = packageManager.getApplicationIcon(entity.packageName)
                        AppInfo(
                            packageName = entity.packageName,
                            appName = entity.appName,
                            icon = icon,
                            category = entity.category,
                            isUserCategorized = entity.isUserCategorized,
                            lastUsedTimestamp = entity.lastUsedTimestamp,
                        )
                    } catch (_: PackageManager.NameNotFoundException) {
                        null
                    }
                }
                _uiState.value = _uiState.value.copy(apps = apps)
            }
        }
    }
}
