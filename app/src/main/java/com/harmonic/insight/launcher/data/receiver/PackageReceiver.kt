package com.harmonic.insight.launcher.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.harmonic.insight.launcher.data.repository.AppRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PackageReceiver : BroadcastReceiver() {

    @Inject
    lateinit var appRepository: AppRepository

    override fun onReceive(context: Context, intent: Intent) {
        val packageName = intent.data?.schemeSpecificPart ?: return
        val pendingResult = goAsync()

        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                when (intent.action) {
                    Intent.ACTION_PACKAGE_ADDED -> {
                        appRepository.onPackageAdded(packageName)
                    }
                    Intent.ACTION_PACKAGE_REMOVED -> {
                        if (!intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)) {
                            appRepository.onPackageRemoved(packageName)
                        }
                    }
                    Intent.ACTION_PACKAGE_CHANGED -> {
                        appRepository.onPackageAdded(packageName)
                    }
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
