package com.harmonic.insight.launcher.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harmonic.insight.launcher.data.repository.AppRepository
import com.harmonic.insight.launcher.data.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val viewMode: String = "list",
    val iconSize: String = "medium",
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val appRepository: AppRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val viewMode = categoryRepository.getDrawerViewMode()
            val iconSize = categoryRepository.getIconSize()
            _uiState.value = SettingsUiState(
                viewMode = viewMode,
                iconSize = iconSize,
            )
        }
    }

    fun setViewMode(mode: String) {
        _uiState.value = _uiState.value.copy(viewMode = mode)
        viewModelScope.launch {
            categoryRepository.setDrawerViewMode(mode)
        }
    }

    fun setIconSize(size: String) {
        _uiState.value = _uiState.value.copy(iconSize = size)
        viewModelScope.launch {
            categoryRepository.setIconSize(size)
        }
    }

    fun reclassifyApps() {
        viewModelScope.launch {
            appRepository.refreshInstalledApps()
        }
    }
}
