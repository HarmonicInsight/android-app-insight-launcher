package com.harmonic.insight.launcher.data.repository

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.harmonic.insight.launcher.data.local.AppDao
import com.harmonic.insight.launcher.data.local.entity.AppEntity
import com.harmonic.insight.launcher.data.model.AppCategory
import com.harmonic.insight.launcher.data.model.AppInfo
import com.harmonic.insight.launcher.domain.classifier.AppClassifier
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appDao: AppDao,
    private val appClassifier: AppClassifier,
) {
    private val packageManager: PackageManager = context.packageManager

    fun getAllApps(): Flow<List<AppEntity>> = appDao.getAllApps()

    fun getAppsByCategory(category: AppCategory): Flow<List<AppEntity>> =
        appDao.getAppsByCategory(category)

    fun searchApps(query: String): Flow<List<AppEntity>> = appDao.searchApps(query)

    fun getRecentApps(limit: Int = 5): Flow<List<AppEntity>> = appDao.getRecentApps(limit)

    fun getAllAppsWithIcons(): Flow<List<AppInfo>> {
        return appDao.getAllApps().map { entities ->
            entities.map { entity -> entityToAppInfo(entity) }
        }.flowOn(Dispatchers.IO)
    }

    fun getAppsByCategoryWithIcons(category: AppCategory): Flow<List<AppInfo>> {
        return appDao.getAppsByCategory(category).map { entities ->
            entities.map { entity -> entityToAppInfo(entity) }
        }.flowOn(Dispatchers.IO)
    }

    fun searchAppsWithIcons(query: String): Flow<List<AppInfo>> {
        return appDao.searchApps(query).map { entities ->
            entities.map { entity -> entityToAppInfo(entity) }
        }.flowOn(Dispatchers.IO)
    }

    fun getRecentAppsWithIcons(limit: Int = 5): Flow<List<AppInfo>> {
        return appDao.getRecentApps(limit).map { entities ->
            entities.map { entity -> entityToAppInfo(entity) }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getAppCategory(packageName: String): AppCategory {
        return appDao.getApp(packageName)?.category ?: AppCategory.OTHER
    }

    suspend fun refreshInstalledApps() = withContext(Dispatchers.IO) {
        try {
            val launchableApps = getLaunchableApps()
            val userCategorized = appDao.getUserCategorizedApps()
                .associate { it.packageName to it.category }

            // Batch fetch existing apps to avoid N+1 query
            val existingApps = appDao.getAllApps().first()
            val timestampMap = existingApps.associate { it.packageName to it.lastUsedTimestamp }

            val entities = launchableApps.mapNotNull { resolveInfo ->
                try {
                    val packageName = resolveInfo.activityInfo.packageName
                    val appName = resolveInfo.loadLabel(packageManager).toString()

                    val category = if (userCategorized.containsKey(packageName)) {
                        userCategorized[packageName]!!
                    } else {
                        val appInfo = try {
                            packageManager.getApplicationInfo(packageName, 0)
                        } catch (_: Exception) {
                            null
                        }
                        if (appInfo != null) {
                            appClassifier.classify(packageName, appInfo)
                        } else {
                            AppCategory.OTHER
                        }
                    }

                    AppEntity(
                        packageName = packageName,
                        appName = appName,
                        category = category,
                        isUserCategorized = userCategorized.containsKey(packageName),
                        lastUsedTimestamp = timestampMap[packageName] ?: 0L,
                    )
                } catch (_: Exception) {
                    null
                }
            }

            if (entities.isNotEmpty()) {
                appDao.insertApps(entities)
            }
            // Always clean up stale entries, even if entities is empty
            val installedPackages = entities.map { it.packageName }
            appDao.removeUninstalledApps(installedPackages)
        } catch (_: Exception) {
            // Prevent crash - drawer will show empty state
        }
    }

    /**
     * Re-classify apps while preserving user-categorized apps.
     */
    suspend fun reclassifyApps() = withContext(Dispatchers.IO) {
        try {
            val launchableApps = getLaunchableApps()
            val userCategorized = appDao.getUserCategorizedApps()
                .associate { it.packageName to it }

            val existingApps = appDao.getAllApps().first()
            val timestampMap = existingApps.associate { it.packageName to it.lastUsedTimestamp }

            val entities = launchableApps.mapNotNull { resolveInfo ->
                try {
                    val packageName = resolveInfo.activityInfo.packageName
                    val appName = resolveInfo.loadLabel(packageManager).toString()

                    // Preserve user-categorized apps
                    if (userCategorized.containsKey(packageName)) {
                        val existing = userCategorized[packageName]!!
                        existing.copy(appName = appName)
                    } else {
                        val appInfo = try {
                            packageManager.getApplicationInfo(packageName, 0)
                        } catch (_: Exception) {
                            null
                        }
                        val category = if (appInfo != null) {
                            appClassifier.classify(packageName, appInfo)
                        } else {
                            AppCategory.OTHER
                        }

                        AppEntity(
                            packageName = packageName,
                            appName = appName,
                            category = category,
                            isUserCategorized = false,
                            lastUsedTimestamp = timestampMap[packageName] ?: 0L,
                        )
                    }
                } catch (_: Exception) {
                    null
                }
            }

            if (entities.isNotEmpty()) {
                appDao.insertApps(entities)
            }
            val installedPackages = entities.map { it.packageName }
            appDao.removeUninstalledApps(installedPackages)
        } catch (_: Exception) {
            // Prevent crash
        }
    }

    suspend fun updateAppCategory(packageName: String, category: AppCategory) {
        appDao.updateCategory(packageName, category)
    }

    suspend fun recordAppLaunch(packageName: String) {
        appDao.updateLastUsed(packageName, System.currentTimeMillis())
    }

    suspend fun onPackageAdded(packageName: String) = withContext(Dispatchers.IO) {
        val intent = packageManager.getLaunchIntentForPackage(packageName) ?: return@withContext
        val appInfo = try {
            packageManager.getApplicationInfo(packageName, 0)
        } catch (_: PackageManager.NameNotFoundException) {
            return@withContext
        }

        val appName = appInfo.loadLabel(packageManager).toString()
        val category = appClassifier.classify(packageName, appInfo)

        appDao.insertApp(
            AppEntity(
                packageName = packageName,
                appName = appName,
                category = category,
            )
        )
    }

    suspend fun onPackageRemoved(packageName: String) {
        appDao.deleteApp(packageName)
    }

    private fun entityToAppInfo(entity: AppEntity): AppInfo {
        val icon = try {
            packageManager.getApplicationIcon(entity.packageName)
        } catch (_: Exception) {
            packageManager.defaultActivityIcon
        }

        return AppInfo(
            packageName = entity.packageName,
            appName = entity.appName,
            icon = icon,
            category = entity.category,
            isUserCategorized = entity.isUserCategorized,
            lastUsedTimestamp = entity.lastUsedTimestamp,
        )
    }

    private fun getLaunchableApps(): List<android.content.pm.ResolveInfo> {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        return packageManager.queryIntentActivities(intent, 0)
            .filter { it.activityInfo.packageName != context.packageName }
    }
}
