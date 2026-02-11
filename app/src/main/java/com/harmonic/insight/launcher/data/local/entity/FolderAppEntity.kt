package com.harmonic.insight.launcher.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "folder_apps",
    primaryKeys = ["folderId", "packageName"],
    indices = [Index("folderId")],
    foreignKeys = [
        ForeignKey(
            entity = FolderEntity::class,
            parentColumns = ["id"],
            childColumns = ["folderId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class FolderAppEntity(
    val folderId: Long,
    val packageName: String,
    val position: Int,
)
