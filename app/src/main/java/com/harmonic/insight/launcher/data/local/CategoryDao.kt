package com.harmonic.insight.launcher.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.harmonic.insight.launcher.data.local.entity.DockEntity
import com.harmonic.insight.launcher.data.local.entity.FavoriteEntity
import com.harmonic.insight.launcher.data.local.entity.SettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    // Favorites
    @Query("SELECT * FROM favorites ORDER BY position ASC")
    fun getFavorites(): Flow<List<FavoriteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE packageName = :packageName")
    suspend fun removeFavorite(packageName: String)

    @Query("DELETE FROM favorites")
    suspend fun clearFavorites()

    // Dock
    @Query("SELECT * FROM dock ORDER BY position ASC")
    fun getDockApps(): Flow<List<DockEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDockApp(dock: DockEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDockApps(docks: List<DockEntity>)

    @Query("DELETE FROM dock")
    suspend fun clearDock()

    // Settings
    @Query("SELECT * FROM settings WHERE `key` = :key")
    suspend fun getSetting(key: String): SettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSetting(setting: SettingsEntity)
}
