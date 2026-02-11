package com.harmonic.insight.launcher.ui.settings

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.harmonic.insight.launcher.BuildConfig
import com.harmonic.insight.launcher.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showReclassifyConfirm by remember { mutableStateOf(false) }

    // Reclassify confirmation dialog
    if (showReclassifyConfirm) {
        AlertDialog(
            onDismissRequest = { showReclassifyConfirm = false },
            title = { Text(stringResource(R.string.confirm_reclassify_title)) },
            text = { Text(stringResource(R.string.confirm_reclassify_message)) },
            confirmButton = {
                TextButton(onClick = {
                    showReclassifyConfirm = false
                    viewModel.reclassifyApps()
                }) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showReclassifyConfirm = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
            )
        },
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
        ) {
            // Display section
            SettingsSectionHeader(stringResource(R.string.settings_display))

            // Icon size
            SettingsItem(
                title = stringResource(R.string.settings_icon_size),
                subtitle = when (uiState.iconSize) {
                    "small" -> stringResource(R.string.settings_icon_small)
                    "large" -> stringResource(R.string.settings_icon_large)
                    else -> stringResource(R.string.settings_icon_medium)
                },
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    listOf("small" to R.string.settings_icon_small, "medium" to R.string.settings_icon_medium, "large" to R.string.settings_icon_large).forEach { (size, labelRes) ->
                        RadioButton(
                            selected = uiState.iconSize == size,
                            onClick = { viewModel.setIconSize(size) },
                        )
                        Text(stringResource(labelRes))
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }

            HorizontalDivider()

            // Category management section
            SettingsSectionHeader(stringResource(R.string.settings_category))

            SettingsClickableItem(
                title = stringResource(R.string.settings_reclassify),
                subtitle = stringResource(R.string.settings_reclassify_description),
                onClick = { showReclassifyConfirm = true },
            )

            HorizontalDivider()

            // App info section
            SettingsSectionHeader(stringResource(R.string.settings_app_info))

            SettingsInfoItem(
                title = stringResource(R.string.settings_version),
                value = BuildConfig.VERSION_NAME,
            )

            SettingsInfoItem(
                title = stringResource(R.string.settings_developer),
                value = "Harmonic Insight",
            )

            HorizontalDivider()

            // Default launcher
            SettingsClickableItem(
                title = stringResource(R.string.settings_set_default_launcher),
                subtitle = stringResource(R.string.settings_set_default_launcher_description),
                onClick = {
                    val intent = Intent(Settings.ACTION_HOME_SETTINGS)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                },
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
    )
}

@Composable
private fun SettingsItem(
    title: String,
    subtitle: String,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(8.dp))
        content()
    }
}

@Composable
private fun SettingsClickableItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun SettingsInfoItem(
    title: String,
    value: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
