package com.harmonic.insight.launcher.data.model

data class FolderInfo(
    val id: Long,
    val name: String,
    val apps: List<AppInfo>,
)
