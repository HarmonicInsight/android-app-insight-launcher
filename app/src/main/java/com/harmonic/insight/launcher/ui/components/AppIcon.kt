package com.harmonic.insight.launcher.ui.components

import android.graphics.drawable.Drawable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
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
    labelColor: Color = Color.Unspecified,
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
        } else {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(iconSize.sizeDp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
            ) {
                Icon(
                    imageVector = Icons.Default.Apps,
                    contentDescription = appName,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(iconSize.sizeDp / 2),
                )
            }
        }

        if (showLabel) {
            val resolvedColor = if (labelColor != Color.Unspecified) labelColor else MaterialTheme.colorScheme.onSurface
            val textStyle = if (labelColor == Color.White) {
                MaterialTheme.typography.labelSmall.copy(
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.6f),
                        offset = Offset(1f, 1f),
                        blurRadius = 3f,
                    ),
                )
            } else {
                MaterialTheme.typography.labelSmall
            }
            Text(
                text = appName,
                style = textStyle,
                color = resolvedColor,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.width(iconSize.labelWidth),
            )
        }
    }
}
