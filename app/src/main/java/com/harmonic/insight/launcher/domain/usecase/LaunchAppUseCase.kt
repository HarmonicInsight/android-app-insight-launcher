package com.harmonic.insight.launcher.domain.usecase

import android.content.Context
import com.harmonic.insight.launcher.data.repository.AppRepository
import com.harmonic.insight.launcher.util.PackageUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class LaunchAppUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appRepository: AppRepository,
) {
    suspend operator fun invoke(packageName: String): Boolean {
        val launched = PackageUtils.launchApp(context, packageName)
        if (launched) {
            appRepository.recordAppLaunch(packageName)
        }
        return launched
    }
}
