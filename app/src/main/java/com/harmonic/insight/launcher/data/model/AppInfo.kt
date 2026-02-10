package com.harmonic.insight.launcher.data.model

import android.graphics.drawable.Drawable

data class AppInfo(
    val packageName: String,
    val appName: String,
    val icon: Drawable?,
    val category: AppCategory,
    val isUserCategorized: Boolean = false,
    val lastUsedTimestamp: Long = 0L,
)
