package com.harmonic.insight.launcher.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.harmonic.insight.launcher.data.local.entity.AppEntity
import com.harmonic.insight.launcher.data.local.entity.DockEntity
import com.harmonic.insight.launcher.data.local.entity.FavoriteEntity
import com.harmonic.insight.launcher.data.local.entity.FolderAppEntity
import com.harmonic.insight.launcher.data.local.entity.FolderEntity
import com.harmonic.insight.launcher.data.local.entity.SettingsEntity

@Database(
    entities = [
        AppEntity::class,
        FavoriteEntity::class,
        DockEntity::class,
        SettingsEntity::class,
        FolderEntity::class,
        FolderAppEntity::class,
    ],
    version = 2,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao
    abstract fun categoryDao(): CategoryDao
    abstract fun folderDao(): FolderDao
}
