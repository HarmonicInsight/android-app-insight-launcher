package com.harmonic.insight.launcher.data.icon

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Reads launcher-manifest.json and provides branded icon loading
 * for known Insight products.
 *
 * Aligned with insight-common/android/launcher/LauncherManifestReader.kt,
 * extended with Hilt DI and runtime cache support (IconSyncManager).
 *
 * Icon resolution priority:
 *   1. Runtime cache  (filesDir/launcher_icon_cache/) — downloaded by IconSyncManager
 *   2. Bundled assets (assets/launcher/)              — baked in at build time
 *   3. PackageManager                                 — system icon fallback
 */
@Singleton
class LauncherManifestReader @Inject constructor(
    @ApplicationContext private val context: Context,
    private val iconSyncManager: IconSyncManager,
) {
    companion object {
        private const val TAG = "LauncherManifest"
        private const val ASSETS_BASE_PATH = "launcher"
        private const val MANIFEST_FILENAME = "launcher-manifest.json"
        private const val ICON_FILENAME = "ic_launcher.png"

        private val DENSITY_ORDER = listOf("mdpi", "hdpi", "xhdpi", "xxhdpi", "xxxhdpi")
    }

    private val packageManager: PackageManager = context.packageManager

    /** Android package name → product code */
    private val packageToCode: Map<String, String> by lazy { buildPackageMapping() }

    @Volatile
    private var cachedEntries: List<LauncherIconEntry>? = null

    // =========================================================================
    // Public API — icon loading
    // =========================================================================

    /**
     * Load the branded icon for a package, falling back to the system icon.
     * This is the primary entry point for AppRepository.
     */
    fun loadIconWithFallback(packageName: String): Drawable {
        val code = packageToCode[packageName]
        if (code != null) {
            val bitmap = loadIcon(code)
            if (bitmap != null) {
                return BitmapDrawable(context.resources, bitmap)
            }
        }

        return try {
            packageManager.getApplicationIcon(packageName)
        } catch (_: Exception) {
            packageManager.defaultActivityIcon
        }
    }

    /**
     * Load icon by product code with PackageManager fallback.
     * Compatible with insight-common's official API signature.
     */
    fun loadIconWithFallback(
        code: String,
        packageName: String?,
        pm: PackageManager = packageManager,
    ): Drawable? {
        val bitmap = loadIcon(code)
        if (bitmap != null) {
            return BitmapDrawable(context.resources, bitmap)
        }

        if (packageName != null) {
            return try {
                pm.getApplicationIcon(packageName)
            } catch (_: PackageManager.NameNotFoundException) {
                Log.w(TAG, "Package not found: $packageName")
                null
            }
        }
        return null
    }

    /**
     * Load icon as Bitmap by product code.
     * Checks runtime cache first, then bundled assets, with density fallback.
     */
    fun loadIcon(code: String, density: String? = null): Bitmap? {
        val targetDensity = density ?: getDeviceDensityName()

        // 1. Try runtime cache
        loadBitmapFromCache(code, targetDensity)?.let { return it }

        // 2. Try bundled assets
        loadBitmapFromAssets(code, targetDensity)?.let { return it }

        // 3. Fallback: try other densities (high → low for better quality)
        return tryFallbackDensities(code, targetDensity)
    }

    /**
     * Load all icons as Map<code, Bitmap>.
     */
    fun loadAllIcons(productsOnly: Boolean = false): Map<String, Bitmap> {
        val entries = if (productsOnly) getProductEntries() else getEntries()
        val result = mutableMapOf<String, Bitmap>()
        for (entry in entries) {
            loadIcon(entry.code)?.let { result[entry.code] = it }
        }
        return result
    }

    // =========================================================================
    // Public API — manifest entries
    // =========================================================================

    /**
     * Get all entries sorted by displayOrder.
     */
    fun getEntries(): List<LauncherIconEntry> {
        cachedEntries?.let { return it }

        val entries = loadManifestFromCache() ?: loadManifestFromAssets() ?: emptyList()
        cachedEntries = entries
        return entries
    }

    /**
     * Get entries grouped by category.
     */
    fun getEntriesByCategory(): Map<LauncherIconCategory, List<LauncherIconEntry>> {
        val entries = getEntries()
        val result = mutableMapOf<LauncherIconCategory, MutableList<LauncherIconEntry>>()
        LauncherIconCategory.entries.forEach { result[it] = mutableListOf() }

        for (entry in entries) {
            val category = LauncherIconCategory.fromKey(entry.category) ?: LauncherIconCategory.UTILITY
            result[category]?.add(entry)
        }
        return result
    }

    fun getProductEntries(): List<LauncherIconEntry> = getEntries().filter { it.isProduct }

    fun getEntry(code: String): LauncherIconEntry? = getEntries().find { it.code == code }

    fun isAvailable(): Boolean {
        return try {
            context.assets.open("$ASSETS_BASE_PATH/$MANIFEST_FILENAME").use { true }
        } catch (_: IOException) {
            false
        }
    }

    /**
     * Invalidate cached manifest (call after IconSyncManager downloads new icons).
     */
    fun invalidateCache() {
        cachedEntries = null
    }

    // =========================================================================
    // Bitmap loading
    // =========================================================================

    private fun loadBitmapFromCache(code: String, density: String): Bitmap? {
        val file = File(iconSyncManager.getCacheDirectory(), "$code/mipmap-$density/$ICON_FILENAME")
        if (!file.exists()) return null
        return try {
            FileInputStream(file).use { BitmapFactory.decodeStream(it) }
        } catch (_: Exception) {
            null
        }
    }

    private fun loadBitmapFromAssets(code: String, density: String): Bitmap? {
        val path = "$ASSETS_BASE_PATH/$code/mipmap-$density/$ICON_FILENAME"
        return try {
            context.assets.open(path).use { BitmapFactory.decodeStream(it) }
        } catch (_: IOException) {
            null
        }
    }

    private fun tryFallbackDensities(code: String, failedDensity: String): Bitmap? {
        // High → low for better scale-down quality
        val fallbacks = DENSITY_ORDER.reversed().filter { it != failedDensity }
        for (density in fallbacks) {
            loadBitmapFromCache(code, density)?.let { return it }
            loadBitmapFromAssets(code, density)?.let { return it }
        }
        Log.w(TAG, "No icon found for $code at any density")
        return null
    }

    // =========================================================================
    // Manifest loading
    // =========================================================================

    private fun loadManifestFromCache(): List<LauncherIconEntry>? {
        val file = File(iconSyncManager.getCacheDirectory(), MANIFEST_FILENAME)
        if (!file.exists()) return null
        return try {
            parseManifest(file.readText())
        } catch (_: Exception) {
            null
        }
    }

    private fun loadManifestFromAssets(): List<LauncherIconEntry>? {
        return try {
            val json = context.assets.open("$ASSETS_BASE_PATH/$MANIFEST_FILENAME")
                .bufferedReader().use { it.readText() }
            parseManifest(json)
        } catch (_: Exception) {
            null
        }
    }

    private fun parseManifest(json: String): List<LauncherIconEntry> {
        val root = JSONObject(json)
        val entriesArray = root.getJSONArray("entries")
        val result = mutableListOf<LauncherIconEntry>()

        for (i in 0 until entriesArray.length()) {
            val obj = entriesArray.getJSONObject(i)
            result.add(
                LauncherIconEntry(
                    code = obj.getString("code"),
                    name = obj.getString("name"),
                    masterIcon = obj.optString("masterIcon", ""),
                    category = obj.optString("category", "utility"),
                    displayOrder = obj.optInt("displayOrder", 999),
                    isProduct = obj.optBoolean("isProduct", false),
                )
            )
        }

        result.sortBy { it.displayOrder }
        return result
    }

    // =========================================================================
    // Package mapping
    // =========================================================================

    private fun buildPackageMapping(): Map<String, String> = mapOf(
        // Products
        "com.harmonic.insight.slide" to "INSS",
        "com.harmonic.insight.sheet" to "IOSH",
        "com.harmonic.insight.doc" to "IOSD",
        "com.harmonic.insight.senioroffice" to "ISOF",
        "com.harmonic.insight.py" to "INPY",
        "com.harmonic.insight.movie" to "INMV",
        "com.harmonic.insight.imagegen" to "INIG",
        "com.harmonic.insight.nca" to "INCA",
        "com.harmonic.insight.bot" to "INBT",
        "com.harmonic.insight.interview" to "IVIN",
        // Utilities
        "com.harmonic.insight.camera" to "CAMERA",
        "com.harmonic.insight.voiceclock" to "VOICE_CLOCK",
        "com.harmonic.insight.pinboard" to "PINBOARD",
        "com.harmonic.insight.voicememo" to "VOICE_MEMO",
        "com.harmonic.insight.qr" to "QR",
    )

    private fun getDeviceDensityName(): String {
        val dpi = context.resources.displayMetrics.densityDpi
        return when {
            dpi <= DisplayMetrics.DENSITY_MEDIUM -> "mdpi"
            dpi <= DisplayMetrics.DENSITY_HIGH -> "hdpi"
            dpi <= DisplayMetrics.DENSITY_XHIGH -> "xhdpi"
            dpi <= DisplayMetrics.DENSITY_XXHIGH -> "xxhdpi"
            else -> "xxxhdpi"
        }
    }
}

// =============================================================================
// Data models — aligned with insight-common/android/launcher/LauncherManifestReader.kt
// =============================================================================

data class LauncherIconEntry(
    val code: String,
    val name: String,
    val masterIcon: String = "",
    val category: String,
    val displayOrder: Int,
    val isProduct: Boolean,
)

enum class LauncherIconCategory(val key: String, val labelJa: String, val labelEn: String) {
    OFFICE("office", "InsightOffice", "InsightOffice"),
    AI_TOOLS("ai_tools", "AI ツール", "AI Tools"),
    ENTERPRISE("enterprise", "業務変革ツール", "Enterprise Tools"),
    SENIOR("senior", "シニアオフィス", "Senior Office"),
    UTILITY("utility", "ユーティリティ", "Utilities");

    companion object {
        fun fromKey(key: String): LauncherIconCategory? =
            entries.find { it.key == key }
    }
}
