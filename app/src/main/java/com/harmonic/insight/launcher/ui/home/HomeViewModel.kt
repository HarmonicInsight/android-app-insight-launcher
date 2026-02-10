package com.harmonic.insight.launcher.ui.home

import android.content.Context
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harmonic.insight.launcher.data.local.entity.DockEntity
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val favorites: List<AppInfo> = emptyList(),
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
        viewModelScope.launch {
            appRepository.refreshInstalledApps()
            setupInitialDock()
            checkOnboarding()
        }

        viewModelScope.launch {
            combine(
                categoryRepository.getFavorites(),
                categoryRepository.getDockApps(),
            ) { favorites, dock ->
                Pair(favorites, dock)
            }.collect { (favorites, dock) ->
                val favoriteApps = favorites.mapNotNull { fav ->
                    loadAppInfo(fav.packageName)
                }
                val dockApps = dock.mapNotNull { d ->
                    loadAppInfo(d.packageName)
                }
                _uiState.value = HomeUiState(
                    favorites = favoriteApps,
                    dockApps = dockApps,
                    isLoading = false,
                    showOnboarding = _uiState.value.showOnboarding,
                )
            }
        }
    }

    fun launchApp(packageName: String) {
        viewModelScope.launch {
            launchAppUseCase(packageName)
        }
    }

    fun dismissOnboarding() {
        viewModelScope.launch {
            categoryRepository.setOnboardingCompleted()
            _uiState.value = _uiState.value.copy(showOnboarding = false)
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

    private suspend fun loadAppInfo(packageName: String): AppInfo? {
        return try {
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            val appName = appInfo.loadLabel(packageManager).toString()
            val icon = packageManager.getApplicationIcon(packageName)
            val category = appRepository.getAppCategory(packageName)
            AppInfo(
                packageName = packageName,
                appName = appName,
                icon = icon,
                category = category,
            )
        } catch (_: PackageManager.NameNotFoundException) {
            null
        }
    }
}
