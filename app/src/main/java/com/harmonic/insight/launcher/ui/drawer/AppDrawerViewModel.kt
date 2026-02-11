package com.harmonic.insight.launcher.ui.drawer

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harmonic.insight.launcher.R
import com.harmonic.insight.launcher.data.model.AppCategory
import com.harmonic.insight.launcher.data.model.AppInfo
import com.harmonic.insight.launcher.data.repository.AppRepository
import com.harmonic.insight.launcher.domain.usecase.LaunchAppUseCase
import com.harmonic.insight.launcher.util.PackageUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AppSection(
    val header: String,
    val apps: List<AppInfo>,
)

data class AppDrawerUiState(
    val sections: List<AppSection> = emptyList(),
    val sectionHeaders: List<String> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = true,
)

@HiltViewModel
class AppDrawerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appRepository: AppRepository,
    private val launchAppUseCase: LaunchAppUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppDrawerUiState())
    val uiState: StateFlow<AppDrawerUiState> = _uiState.asStateFlow()

    private var collectJob: Job? = null

    init {
        viewModelScope.launch {
            appRepository.refreshInstalledApps()
            loadAllApps()
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        if (query.isNotBlank()) {
            searchApps(query)
        } else {
            loadAllApps()
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
        }
    }

    fun openAppInfo(packageName: String) {
        PackageUtils.openAppInfo(context, packageName)
    }

    fun uninstallApp(packageName: String) {
        PackageUtils.uninstallApp(context, packageName)
    }

    private fun loadAllApps() {
        collectJob?.cancel()
        collectJob = viewModelScope.launch {
            appRepository.getAllAppsWithIcons().collect { allApps ->
                val sorted = allApps.sortedBy { it.appName.lowercase() }
                val sections = groupIntoSections(sorted)
                _uiState.value = _uiState.value.copy(
                    sections = sections,
                    sectionHeaders = sections.map { it.header },
                    isLoading = false,
                )
            }
        }
    }

    private fun searchApps(query: String) {
        collectJob?.cancel()
        collectJob = viewModelScope.launch {
            appRepository.searchAppsWithIcons(query).collect { apps ->
                val sorted = apps.sortedBy { it.appName.lowercase() }
                val sections = groupIntoSections(sorted)
                _uiState.value = _uiState.value.copy(
                    sections = sections,
                    sectionHeaders = sections.map { it.header },
                )
            }
        }
    }

    companion object {
        /**
         * Group apps into alphabetical sections.
         * - Katakana/Hiragana → 五十音 row header (ア, カ, サ, タ, ナ, ハ, マ, ヤ, ラ, ワ)
         * - Latin → uppercase letter (A-Z)
         * - Digit → "#"
         * - Other (kanji etc.) → "他"
         */
        fun groupIntoSections(apps: List<AppInfo>): List<AppSection> {
            val grouped = linkedMapOf<String, MutableList<AppInfo>>()
            for (app in apps) {
                val header = sectionHeaderFor(app.appName)
                grouped.getOrPut(header) { mutableListOf() }.add(app)
            }
            return grouped.map { (header, appList) -> AppSection(header, appList) }
        }

        fun sectionHeaderFor(name: String): String {
            if (name.isBlank()) return "#"
            val first = name.first()
            return when {
                first.isDigit() -> "#"
                first in 'A'..'Z' || first in 'a'..'z' -> first.uppercaseChar().toString()
                first.isKana() -> kanaRow(first)
                else -> "他"
            }
        }

        private fun Char.isKana(): Boolean {
            return this in '\u3040'..'\u309F' || // Hiragana
                this in '\u30A0'..'\u30FF'       // Katakana
        }

        /**
         * Map a kana character to its 五十音 row header (Katakana).
         */
        private fun kanaRow(ch: Char): String {
            // Normalize to Katakana
            val katakana = if (ch in '\u3040'..'\u309F') {
                ch + 0x60 // Hiragana → Katakana
            } else {
                ch
            }
            return when (katakana) {
                in 'ア'..'オ' -> "ア"
                in 'カ'..'ゴ' -> "カ"
                in 'サ'..'ゾ' -> "サ"
                in 'タ'..'ド' -> "タ"
                in 'ナ'..'ノ' -> "ナ"
                in 'ハ'..'ポ' -> "ハ"
                in 'マ'..'モ' -> "マ"
                in 'ヤ'..'ヨ' -> "ヤ"
                in 'ラ'..'ロ' -> "ラ"
                in 'ワ'..'ン' -> "ワ"
                else -> "他"
            }
        }
    }
}
