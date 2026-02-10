package com.harmonic.insight.launcher.domain.classifier

import android.content.pm.ApplicationInfo
import com.harmonic.insight.launcher.data.model.AppCategory
import com.harmonic.insight.launcher.data.model.CategoryMapping
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayStoreCategoryMapper @Inject constructor() {

    fun mapFromAndroidCategory(applicationInfo: ApplicationInfo): AppCategory? {
        val androidCategory = applicationInfo.category
        if (androidCategory == ApplicationInfo.CATEGORY_UNDEFINED) return null
        return CategoryMapping.ANDROID_CATEGORY_MAP[androidCategory]
    }
}
