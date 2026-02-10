package com.harmonic.insight.launcher.domain.classifier

import android.app.usage.UsageStatsManager
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsagePatternAnalyzer @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun getRecentlyUsedPackages(daysBack: Int = 7): Map<String, Long> {
        return try {
            val usageStatsManager =
                context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
                    ?: return emptyMap()

            val endTime = System.currentTimeMillis()
            val startTime = endTime - (daysBack * 24 * 60 * 60 * 1000L)

            val stats = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                startTime,
                endTime,
            )

            stats?.associate { it.packageName to it.lastTimeUsed } ?: emptyMap()
        } catch (_: SecurityException) {
            emptyMap()
        }
    }
}
