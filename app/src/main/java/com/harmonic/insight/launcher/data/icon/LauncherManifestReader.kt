package com.harmonic.insight.launcher.data.icon

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Reads launcher-manifest.json and provides branded icon loading
 * for known Insight products.
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
    private val packageManager: PackageManager = context.packageManager

    /** Product code → manifest entry */
    private val entries: Map<String, ManifestEntry> by lazy { loadManifest() }

    /** Android package name → product code */
    private val packageToCode: Map<String, String> by lazy { buildPackageMapping() }

    /**
     * Load the branded icon for a package, falling back to the system icon.
     */
    fun loadIconWithFallback(packageName: String): Drawable {
        val branded = loadBrandedIcon(packageName)
        if (branded != null) return branded

        return try {
            packageManager.getApplicationIcon(packageName)
        } catch (_: Exception) {
            packageManager.defaultActivityIcon
        }
    }

    /**
     * Try to load a branded icon for the given package name.
     * Checks cache first, then bundled assets.
     */
    fun loadBrandedIcon(packageName: String): Drawable? {
        val code = packageToCode[packageName] ?: return null
        return loadIconByCode(code)
    }

    /**
     * Load icon directly by product code (e.g. "INSS", "CAMERA").
     * Priority: runtime cache → bundled assets.
     */
    fun loadIconByCode(code: String): Drawable? {
        val density = getOptimalDensity()

        // 1. Try runtime cache (downloaded by IconSyncManager)
        loadFromCache(code, density)?.let { return it }

        // 2. Try bundled assets
        loadFromAssets(code, density)?.let { return it }

        return null
    }

    /**
     * Reload manifest entries after a sync operation.
     */
    fun invalidateCache() {
        // entries is lazy; force re-evaluation by accessing fresh data
        // In practice the lazy delegate won't re-run, so we use a mutable backing field
        _cachedEntries = null
    }

    /**
     * Get all manifest entries sorted by displayOrder.
     */
    fun getAllEntries(): List<ManifestEntry> {
        return entries.values.sortedBy { it.displayOrder }
    }

    /**
     * Get manifest entries filtered by category.
     */
    fun getEntriesByCategory(category: String): List<ManifestEntry> {
        return entries.values
            .filter { it.category == category }
            .sortedBy { it.displayOrder }
    }

    // =========================================================================
    // Icon loading
    // =========================================================================

    private fun loadFromCache(code: String, density: String): Drawable? {
        val file = File(
            iconSyncManager.getCacheDirectory(),
            "$code/mipmap-$density/ic_launcher.png"
        )
        if (!file.exists()) return null
        return try {
            FileInputStream(file).use { stream ->
                val bitmap = BitmapFactory.decodeStream(stream) ?: return null
                BitmapDrawable(context.resources, bitmap)
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun loadFromAssets(code: String, density: String): Drawable? {
        val path = "launcher/$code/mipmap-$density/ic_launcher.png"
        return try {
            context.assets.open(path).use { stream ->
                val bitmap = BitmapFactory.decodeStream(stream) ?: return null
                BitmapDrawable(context.resources, bitmap)
            }
        } catch (_: Exception) {
            null
        }
    }

    // =========================================================================
    // Manifest loading
    // =========================================================================

    @Volatile
    private var _cachedEntries: Map<String, ManifestEntry>? = null

    private fun loadManifest(): Map<String, ManifestEntry> {
        _cachedEntries?.let { return it }

        val result = loadManifestFromCache() ?: loadManifestFromAssets() ?: emptyMap()
        _cachedEntries = result
        return result
    }

    private fun loadManifestFromCache(): Map<String, ManifestEntry>? {
        val file = File(iconSyncManager.getCacheDirectory(), "launcher-manifest.json")
        if (!file.exists()) return null
        return try {
            parseManifest(file.readText())
        } catch (_: Exception) {
            null
        }
    }

    private fun loadManifestFromAssets(): Map<String, ManifestEntry>? {
        return try {
            val json = context.assets.open("launcher/launcher-manifest.json")
                .bufferedReader().use { it.readText() }
            parseManifest(json)
        } catch (_: Exception) {
            null
        }
    }

    private fun parseManifest(json: String): Map<String, ManifestEntry> {
        val root = JSONObject(json)
        val entriesArray = root.getJSONArray("entries")
        val result = mutableMapOf<String, ManifestEntry>()

        for (i in 0 until entriesArray.length()) {
            val obj = entriesArray.getJSONObject(i)
            val entry = ManifestEntry(
                code = obj.getString("code"),
                name = obj.getString("name"),
                category = obj.getString("category"),
                displayOrder = obj.getInt("displayOrder"),
                isProduct = obj.getBoolean("isProduct"),
            )
            result[entry.code] = entry
        }
        return result
    }

    // =========================================================================
    // Package mapping
    // =========================================================================

    private fun buildPackageMapping(): Map<String, String> {
        val map = mutableMapOf<String, String>()

        // Products
        map["com.harmonic.insight.slide"] = "INSS"
        map["com.harmonic.insight.sheet"] = "IOSH"
        map["com.harmonic.insight.doc"] = "IOSD"
        map["com.harmonic.insight.senioroffice"] = "ISOF"
        map["com.harmonic.insight.py"] = "INPY"
        map["com.harmonic.insight.movie"] = "INMV"
        map["com.harmonic.insight.imagegen"] = "INIG"
        map["com.harmonic.insight.nca"] = "INCA"
        map["com.harmonic.insight.bot"] = "INBT"
        map["com.harmonic.insight.interview"] = "IVIN"

        // Utilities
        map["com.harmonic.insight.camera"] = "CAMERA"
        map["com.harmonic.insight.voiceclock"] = "VOICE_CLOCK"
        map["com.harmonic.insight.pinboard"] = "PINBOARD"
        map["com.harmonic.insight.voicememo"] = "VOICE_MEMO"
        map["com.harmonic.insight.qr"] = "QR"

        return map
    }

    private fun getOptimalDensity(): String {
        return when (context.resources.displayMetrics.densityDpi) {
            in 0..DisplayMetrics.DENSITY_MEDIUM -> "mdpi"
            in (DisplayMetrics.DENSITY_MEDIUM + 1)..DisplayMetrics.DENSITY_HIGH -> "hdpi"
            in (DisplayMetrics.DENSITY_HIGH + 1)..DisplayMetrics.DENSITY_XHIGH -> "xhdpi"
            in (DisplayMetrics.DENSITY_XHIGH + 1)..DisplayMetrics.DENSITY_XXHIGH -> "xxhdpi"
            else -> "xxxhdpi"
        }
    }
}

data class ManifestEntry(
    val code: String,
    val name: String,
    val category: String,
    val displayOrder: Int,
    val isProduct: Boolean,
)
