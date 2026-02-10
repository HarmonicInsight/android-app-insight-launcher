package com.harmonic.insight.launcher.domain.usecase

import com.harmonic.insight.launcher.data.local.entity.AppEntity
import com.harmonic.insight.launcher.data.repository.AppRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchAppsUseCase @Inject constructor(
    private val appRepository: AppRepository,
) {
    fun search(query: String): Flow<List<AppEntity>> = appRepository.searchApps(query)

    fun getRecentApps(limit: Int = 5): Flow<List<AppEntity>> = appRepository.getRecentApps(limit)
}
