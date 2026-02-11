package com.harmonic.insight.launcher.data.icon

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Reads launcher-manifest.json from assets and provides branded icon loading
 * for known Insight products.
 *
 * Icon resolution:
 *   1. Check if the package matches a known Insight product
 *   2. If yes, load the branded icon from assets/launcher/{code}/mipmap-{density}/ic_launcher.png
 *   3. If no match or asset loading fails, fall back to PackageManager.getApplicationIcon()
 */
@Singleton
class LauncherManifestReader @Inject constructor(
    @ApplicationContext private val context: Context,
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
     * Try to load a branded icon from assets for the given package name.
     * Returns null if no branded icon is available.
     */
    fun loadBrandedIcon(packageName: String): Drawable? {
        val code = packageToCode[packageName] ?: return null
        return loadIconByCode(code)
    }

    /**
     * Load icon directly by product code (e.g. "INSS", "CAMERA").
     */
    fun loadIconByCode(code: String): Drawable? {
        val density = getOptimalDensity()
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

    private fun loadManifest(): Map<String, ManifestEntry> {
        return try {
            val json = context.assets.open("launcher/launcher-manifest.json")
                .bufferedReader().use { it.readText() }
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
            result
        } catch (_: Exception) {
            emptyMap()
        }
    }

    /**
     * Build package name → product code mapping for all known Insight apps.
     */
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
