package com.harmonic.insight.launcher.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.harmonic.insight.launcher.data.model.AppCategory

@Composable
fun CategoryTabRow(
    categories: List<AppCategory>,
    selectedCategory: AppCategory,
    onCategorySelected: (AppCategory) -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    if (categories.isEmpty()) return

    val selectedIndex = categories.indexOf(selectedCategory).coerceAtLeast(0)

    ScrollableTabRow(
        selectedTabIndex = selectedIndex,
        edgePadding = 16.dp,
        containerColor = containerColor,
        contentColor = contentColor,
        modifier = modifier,
    ) {
        categories.forEachIndexed { index, category ->
            Tab(
                selected = index == selectedIndex,
                onClick = { onCategorySelected(category) },
                text = {
                    Text(
                        text = "${category.icon} ${category.displayName}",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(horizontal = 4.dp),
                    )
                },
            )
        }
    }
}
