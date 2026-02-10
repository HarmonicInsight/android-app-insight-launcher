package com.harmonic.insight.launcher.data.local

import androidx.room.TypeConverter
import com.harmonic.insight.launcher.data.model.AppCategory

class Converters {
    @TypeConverter
    fun fromAppCategory(category: AppCategory): String = category.name

    @TypeConverter
    fun toAppCategory(name: String): AppCategory = AppCategory.valueOf(name)
}
