package com.harmonic.insight.launcher.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.harmonic.insight.launcher.data.local.entity.AppEntity
import com.harmonic.insight.launcher.data.model.AppCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Query("SELECT * FROM apps ORDER BY lastUsedTimestamp DESC, appName ASC")
    fun getAllApps(): Flow<List<AppEntity>>

    @Query("SELECT * FROM apps WHERE category = :category ORDER BY lastUsedTimestamp DESC, appName ASC")
    fun getAppsByCategory(category: AppCategory): Flow<List<AppEntity>>

    @Query("SELECT * FROM apps WHERE packageName = :packageName")
    suspend fun getApp(packageName: String): AppEntity?

    @Query("SELECT * FROM apps WHERE isUserCategorized = 1")
    suspend fun getUserCategorizedApps(): List<AppEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApp(app: AppEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApps(apps: List<AppEntity>)

    @Update
    suspend fun updateApp(app: AppEntity)

    @Query("UPDATE apps SET category = :category, isUserCategorized = 1 WHERE packageName = :packageName")
    suspend fun updateCategory(packageName: String, category: AppCategory)

    @Query("UPDATE apps SET lastUsedTimestamp = :timestamp WHERE packageName = :packageName")
    suspend fun updateLastUsed(packageName: String, timestamp: Long)

    @Query("DELETE FROM apps WHERE packageName = :packageName")
    suspend fun deleteApp(packageName: String)

    @Query("DELETE FROM apps WHERE packageName NOT IN (:installedPackages)")
    suspend fun removeUninstalledApps(installedPackages: List<String>)

    @Query("SELECT * FROM apps WHERE appName LIKE '%' || :query || '%' ORDER BY lastUsedTimestamp DESC")
    fun searchApps(query: String): Flow<List<AppEntity>>

    @Query("SELECT * FROM apps ORDER BY lastUsedTimestamp DESC LIMIT :limit")
    fun getRecentApps(limit: Int): Flow<List<AppEntity>>
}
