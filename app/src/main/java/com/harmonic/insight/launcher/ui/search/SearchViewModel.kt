package com.harmonic.insight.launcher.ui.search

import android.content.Context
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harmonic.insight.launcher.data.model.AppInfo
import com.harmonic.insight.launcher.data.repository.AppRepository
import com.harmonic.insight.launcher.domain.usecase.LaunchAppUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val results: List<AppInfo> = emptyList(),
    val recentApps: List<AppInfo> = emptyList(),
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appRepository: AppRepository,
    private val launchAppUseCase: LaunchAppUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val packageManager: PackageManager = context.packageManager
    private var searchJob: Job? = null

    init {
        viewModelScope.launch {
            appRepository.getRecentApps(5).collect { entities ->
                val recentApps = entities.mapNotNull { entity ->
                    try {
                        val icon = packageManager.getApplicationIcon(entity.packageName)
                        AppInfo(
                            packageName = entity.packageName,
                            appName = entity.appName,
                            icon = icon,
                            category = entity.category,
                            lastUsedTimestamp = entity.lastUsedTimestamp,
                        )
                    } catch (_: PackageManager.NameNotFoundException) {
                        null
                    }
                }
                _uiState.value = _uiState.value.copy(recentApps = recentApps)
            }
        }
    }

    fun updateQuery(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
        searchJob?.cancel()
        if (query.isBlank()) {
            _uiState.value = _uiState.value.copy(results = emptyList())
            return
        }
        searchJob = viewModelScope.launch {
            appRepository.searchApps(query).collect { entities ->
                val results = entities.mapNotNull { entity ->
                    try {
                        val icon = packageManager.getApplicationIcon(entity.packageName)
                        AppInfo(
                            packageName = entity.packageName,
                            appName = entity.appName,
                            icon = icon,
                            category = entity.category,
                            lastUsedTimestamp = entity.lastUsedTimestamp,
                        )
                    } catch (_: PackageManager.NameNotFoundException) {
                        null
                    }
                }
                _uiState.value = _uiState.value.copy(results = results)
            }
        }
    }

    fun launchApp(packageName: String) {
        viewModelScope.launch {
            launchAppUseCase(packageName)
        }
    }
}
