package com.harmonic.insight.launcher.data.icon

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Syncs launcher icons from the remote insight-common repository at runtime.
 *
 * Flow:
 *   1. Fetch launcher-manifest.json from GitHub
 *   2. Compare version with locally cached version
 *   3. If newer, download icons for the device's optimal density
 *   4. Store in app internal cache for LauncherManifestReader to pick up
 */
@Singleton
class IconSyncManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val cacheDir: File
        get() = File(context.filesDir, CACHE_DIR_NAME).also { it.mkdirs() }

    /**
     * Check for icon updates and download if a newer version is available.
     * Safe to call from any coroutine — runs on IO dispatcher.
     *
     * @return true if icons were updated, false if already up-to-date or on error
     */
    suspend fun sync(): Boolean = withContext(Dispatchers.IO) {
        try {
            val remoteManifest = fetchRemoteManifest() ?: return@withContext false
            val remoteVersion = remoteManifest.optInt("version", 0)
            val localVersion = readLocalVersion()

            if (remoteVersion <= localVersion) {
                Log.d(TAG, "Icons up-to-date (v$localVersion)")
                return@withContext false
            }

            Log.d(TAG, "Updating icons: v$localVersion → v$remoteVersion")

            val entries = remoteManifest.getJSONArray("entries")
            val density = getOptimalDensity()
            var downloaded = 0

            for (i in 0 until entries.length()) {
                val entry = entries.getJSONObject(i)
                val code = entry.getString("code")
                val success = downloadIcon(code, density)
                if (success) downloaded++
            }

            // Save manifest and version
            File(cacheDir, MANIFEST_FILE).writeText(remoteManifest.toString())
            File(cacheDir, VERSION_FILE).writeText(remoteVersion.toString())

            Log.d(TAG, "Synced $downloaded icons (v$remoteVersion)")
            true
        } catch (e: Exception) {
            Log.w(TAG, "Icon sync failed", e)
            false
        }
    }

    /**
     * Get the cache directory for synced icons.
     */
    fun getCacheDirectory(): File = cacheDir

    private fun fetchRemoteManifest(): JSONObject? {
        val url = URL("$BASE_URL/launcher-manifest.json")
        val conn = url.openConnection() as HttpURLConnection
        return try {
            conn.connectTimeout = TIMEOUT_MS
            conn.readTimeout = TIMEOUT_MS
            conn.requestMethod = "GET"

            if (conn.responseCode != HttpURLConnection.HTTP_OK) {
                Log.w(TAG, "Manifest fetch failed: HTTP ${conn.responseCode}")
                return null
            }

            val text = conn.inputStream.bufferedReader().use { it.readText() }
            JSONObject(text)
        } finally {
            conn.disconnect()
        }
    }

    private fun downloadIcon(code: String, density: String): Boolean {
        val remotePath = "$BASE_URL/$code/mipmap-$density/ic_launcher.png"
        val localDir = File(cacheDir, "$code/mipmap-$density").also { it.mkdirs() }
        val localFile = File(localDir, "ic_launcher.png")

        val url = URL(remotePath)
        val conn = url.openConnection() as HttpURLConnection
        return try {
            conn.connectTimeout = TIMEOUT_MS
            conn.readTimeout = TIMEOUT_MS
            conn.requestMethod = "GET"

            if (conn.responseCode != HttpURLConnection.HTTP_OK) {
                Log.w(TAG, "Icon download failed for $code: HTTP ${conn.responseCode}")
                return false
            }

            conn.inputStream.use { input ->
                localFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            true
        } catch (e: Exception) {
            Log.w(TAG, "Icon download failed for $code", e)
            false
        } finally {
            conn.disconnect()
        }
    }

    private fun readLocalVersion(): Int {
        val versionFile = File(cacheDir, VERSION_FILE)
        return try {
            if (versionFile.exists()) versionFile.readText().trim().toInt() else 0
        } catch (_: Exception) {
            0
        }
    }

    private fun getOptimalDensity(): String {
        val dpi = context.resources.displayMetrics.densityDpi
        return when {
            dpi <= 160 -> "mdpi"
            dpi <= 240 -> "hdpi"
            dpi <= 320 -> "xhdpi"
            dpi <= 480 -> "xxhdpi"
            else -> "xxxhdpi"
        }
    }

    companion object {
        private const val TAG = "IconSyncManager"
        private const val CACHE_DIR_NAME = "launcher_icon_cache"
        private const val MANIFEST_FILE = "launcher-manifest.json"
        private const val VERSION_FILE = "manifest_version"
        private const val TIMEOUT_MS = 10_000

        /**
         * Base URL for icon assets on GitHub (raw content).
         * Points to the main branch of insight-common.
         */
        private const val BASE_URL =
            "https://raw.githubusercontent.com/HarmonicInsight/cross-lib-insight-common/main/brand/icons/generated/launcher"
    }
}
