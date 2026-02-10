package com.harmonic.insight.launcher.ui.components

import android.graphics.drawable.Drawable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.harmonic.insight.launcher.util.IconUtils

enum class IconSize(val sizeDp: Dp, val labelWidth: Dp) {
    SMALL(40.dp, 56.dp),
    MEDIUM(48.dp, 64.dp),
    LARGE(56.dp, 72.dp),
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppIcon(
    appName: String,
    icon: Drawable?,
    iconSize: IconSize = IconSize.MEDIUM,
    showLabel: Boolean = true,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val imageBitmap = remember(icon) {
        IconUtils.drawableToImageBitmap(icon, iconSize.sizeDp.value.toInt() * 2)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
            .width(iconSize.labelWidth + 8.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
            ),
    ) {
        if (imageBitmap != null) {
            Image(
                bitmap = imageBitmap,
                contentDescription = appName,
                modifier = Modifier.size(iconSize.sizeDp),
            )
        }

        if (showLabel) {
            Text(
                text = appName,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.width(iconSize.labelWidth),
            )
        }
    }
}
