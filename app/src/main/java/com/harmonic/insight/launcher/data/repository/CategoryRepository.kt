package com.harmonic.insight.launcher.data.repository

import com.harmonic.insight.launcher.data.local.CategoryDao
import com.harmonic.insight.launcher.data.local.entity.DockEntity
import com.harmonic.insight.launcher.data.local.entity.FavoriteEntity
import com.harmonic.insight.launcher.data.local.entity.SettingsEntity
import com.harmonic.insight.launcher.data.model.AppCategory
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(
    private val categoryDao: CategoryDao,
) {
    // Category order
    suspend fun getCategoryOrder(): List<AppCategory> {
        val saved = categoryDao.getSetting("category_order")
        return if (saved != null) {
            saved.value.split(",").mapNotNull { name ->
                try {
                    AppCategory.valueOf(name)
                } catch (_: IllegalArgumentException) {
                    null
                }
            }
        } else {
            AppCategory.entries.toList()
        }
    }

    suspend fun saveCategoryOrder(order: List<AppCategory>) {
        categoryDao.saveSetting(
            SettingsEntity("category_order", order.joinToString(",") { it.name })
        )
    }

    // Favorites
    fun getFavorites(): Flow<List<FavoriteEntity>> = categoryDao.getFavorites()

    suspend fun addFavorite(packageName: String, position: Int) {
        categoryDao.insertFavorite(FavoriteEntity(packageName, position))
    }

    suspend fun removeFavorite(packageName: String) {
        categoryDao.removeFavorite(packageName)
    }

    // Dock
    fun getDockApps(): Flow<List<DockEntity>> = categoryDao.getDockApps()

    suspend fun setDockApps(apps: List<DockEntity>) {
        categoryDao.clearDock()
        apps.forEach { categoryDao.insertDockApp(it) }
    }

    // Settings
    suspend fun getDrawerViewMode(): String {
        return categoryDao.getSetting("drawer_view_mode")?.value ?: "list"
    }

    suspend fun setDrawerViewMode(mode: String) {
        categoryDao.saveSetting(SettingsEntity("drawer_view_mode", mode))
    }

    suspend fun getIconSize(): String {
        return categoryDao.getSetting("icon_size")?.value ?: "medium"
    }

    suspend fun setIconSize(size: String) {
        categoryDao.saveSetting(SettingsEntity("icon_size", size))
    }

    suspend fun isOnboardingCompleted(): Boolean {
        return categoryDao.getSetting("onboarding_completed")?.value == "true"
    }

    suspend fun setOnboardingCompleted() {
        categoryDao.saveSetting(SettingsEntity("onboarding_completed", "true"))
    }
}
