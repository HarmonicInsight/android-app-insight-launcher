package com.harmonic.insight.launcher.ui.drawer

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harmonic.insight.launcher.R
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

    init {
        viewModelScope.launch {
            val viewMode = categoryRepository.getDrawerViewMode()
            _uiState.value = _uiState.value.copy(viewMode = viewMode)

            appRepository.refreshInstalledApps()
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
            val launched = launchAppUseCase(packageName)
            if (!launched) {
                Toast.makeText(context, R.string.launch_failed, Toast.LENGTH_SHORT).show()
            }
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

    private suspend fun loadAvailableCategories() {
        val allApps = appRepository.getAllApps().first()
        // トップレベルカテゴリでグループ化
        val usedTopLevel = allApps.map { AppCategory.topLevelOf(it.category) }.toSet()
        val categoryOrder = categoryRepository.getCategoryOrder()
            .filter { AppCategory.isTopLevel(it) }
        val availableCategories = categoryOrder.filter { it in usedTopLevel }

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

    /**
     * トップレベルカテゴリを選択した場合、そのサブカテゴリのアプリも含めて表示
     */
    private fun loadAppsForCategory(category: AppCategory) {
        viewModelScope.launch {
            appRepository.getAllAppsWithIcons().collect { allApps ->
                val filtered = allApps.filter {
                    AppCategory.topLevelOf(it.category) == category
                }
                _uiState.value = _uiState.value.copy(apps = filtered)
            }
        }
    }

    private fun searchApps(query: String) {
        viewModelScope.launch {
            appRepository.searchAppsWithIcons(query).collect { apps ->
                _uiState.value = _uiState.value.copy(apps = apps)
            }
        }
    }
}
