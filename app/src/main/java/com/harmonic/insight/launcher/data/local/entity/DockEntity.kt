package com.harmonic.insight.launcher.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dock")
data class DockEntity(
    @PrimaryKey
    val position: Int,
    val packageName: String,
)
