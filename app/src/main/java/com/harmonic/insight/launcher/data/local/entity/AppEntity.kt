package com.harmonic.insight.launcher.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.harmonic.insight.launcher.data.model.AppCategory

@Entity(tableName = "apps")
data class AppEntity(
    @PrimaryKey
    val packageName: String,
    val appName: String,
    val category: AppCategory,
    val isUserCategorized: Boolean = false,
    val lastUsedTimestamp: Long = 0L,
)
