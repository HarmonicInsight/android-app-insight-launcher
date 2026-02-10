package com.harmonic.insight.launcher.domain.usecase

import com.harmonic.insight.launcher.data.model.AppInfo
import com.harmonic.insight.launcher.data.repository.AppRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchAppsUseCase @Inject constructor(
    private val appRepository: AppRepository,
) {
    fun search(query: String): Flow<List<AppInfo>> = appRepository.searchAppsWithIcons(query)

    fun getRecentApps(limit: Int = 5): Flow<List<AppInfo>> =
        appRepository.getRecentAppsWithIcons(limit)
}
