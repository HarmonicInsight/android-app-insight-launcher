package com.harmonic.insight.launcher.data.model

import android.content.pm.ApplicationInfo

object CategoryMapping {

    val ANDROID_CATEGORY_MAP = mapOf(
        ApplicationInfo.CATEGORY_SOCIAL to AppCategory.COMMUNICATION,
        ApplicationInfo.CATEGORY_PRODUCTIVITY to AppCategory.WORK,
        ApplicationInfo.CATEGORY_GAME to AppCategory.GAME,
        ApplicationInfo.CATEGORY_AUDIO to AppCategory.MEDIA,
        ApplicationInfo.CATEGORY_VIDEO to AppCategory.MEDIA,
        ApplicationInfo.CATEGORY_IMAGE to AppCategory.MEDIA,
        ApplicationInfo.CATEGORY_NEWS to AppCategory.NEWS,
        ApplicationInfo.CATEGORY_MAPS to AppCategory.TRANSPORT,
    )

    val PACKAGE_KEYWORDS = mapOf(
        "bank" to AppCategory.MONEY,
        "pay" to AppCategory.MONEY,
        "finance" to AppCategory.MONEY,
        "mail" to AppCategory.COMMUNICATION,
        "message" to AppCategory.COMMUNICATION,
        "chat" to AppCategory.COMMUNICATION,
        "camera" to AppCategory.MEDIA,
        "photo" to AppCategory.MEDIA,
        "video" to AppCategory.MEDIA,
        "music" to AppCategory.MEDIA,
        "game" to AppCategory.GAME,
        "map" to AppCategory.TRANSPORT,
        "navi" to AppCategory.TRANSPORT,
        "taxi" to AppCategory.TRANSPORT,
        "shop" to AppCategory.SHOPPING,
        "store" to AppCategory.SHOPPING,
        "news" to AppCategory.NEWS,
        "weather" to AppCategory.TOOL,
        "calc" to AppCategory.TOOL,
        "health" to AppCategory.HEALTH,
        "fit" to AppCategory.HEALTH,
        "learn" to AppCategory.LEARN,
        "edu" to AppCategory.LEARN,
        "study" to AppCategory.LEARN,
    )
}
