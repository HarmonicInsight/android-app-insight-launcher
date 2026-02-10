package com.harmonic.insight.launcher.domain.usecase

import com.harmonic.insight.launcher.data.model.AppCategory
import com.harmonic.insight.launcher.data.model.AppInfo
import com.harmonic.insight.launcher.data.repository.AppRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCategorizedAppsUseCase @Inject constructor(
    private val appRepository: AppRepository,
) {
    fun getAllApps(): Flow<List<AppInfo>> = appRepository.getAllAppsWithIcons()

    fun getAppsByCategory(category: AppCategory): Flow<List<AppInfo>> =
        appRepository.getAppsByCategoryWithIcons(category)

    suspend fun refreshApps() {
        appRepository.refreshInstalledApps()
    }
}
