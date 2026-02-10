package com.harmonic.insight.launcher.domain.classifier

import android.content.pm.ApplicationInfo
import com.harmonic.insight.launcher.data.model.AppCategory
import com.harmonic.insight.launcher.data.model.CategoryMapping
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppClassifier @Inject constructor(
    private val playStoreCategoryMapper: PlayStoreCategoryMapper,
) {
    fun classify(packageName: String, applicationInfo: ApplicationInfo): AppCategory {
        // Priority 1: Japanese app-specific rules
        JapaneseAppRules.KNOWN_APPS[packageName]?.let { return it }

        // Priority 2: Android system category mapping
        playStoreCategoryMapper.mapFromAndroidCategory(applicationInfo)?.let { return it }

        // Priority 3: Package name heuristic
        classifyByPackageName(packageName)?.let { return it }

        return AppCategory.OTHER
    }

    private fun classifyByPackageName(packageName: String): AppCategory? {
        val lowerPackageName = packageName.lowercase()
        for ((keyword, category) in CategoryMapping.PACKAGE_KEYWORDS) {
            if (lowerPackageName.contains(keyword)) {
                return category
            }
        }
        return null
    }
}
