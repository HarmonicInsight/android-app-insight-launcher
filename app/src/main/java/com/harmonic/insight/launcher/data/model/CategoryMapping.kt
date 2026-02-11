package com.harmonic.insight.launcher.data.model

import android.content.pm.ApplicationInfo

object CategoryMapping {

    /** Android システムカテゴリ → サブカテゴリ対応 */
    val ANDROID_CATEGORY_MAP = mapOf(
        ApplicationInfo.CATEGORY_SOCIAL to AppCategory.SNS,
        ApplicationInfo.CATEGORY_PRODUCTIVITY to AppCategory.OFFICE,
        ApplicationInfo.CATEGORY_GAME to AppCategory.GAME,
        ApplicationInfo.CATEGORY_AUDIO to AppCategory.MUSIC,
        ApplicationInfo.CATEGORY_VIDEO to AppCategory.STREAMING,
        ApplicationInfo.CATEGORY_IMAGE to AppCategory.PHOTO,
        ApplicationInfo.CATEGORY_NEWS to AppCategory.NEWS,
        ApplicationInfo.CATEGORY_MAPS to AppCategory.TRANSPORT,
    )

    /**
     * パッケージ名キーワード → カテゴリ（サブカテゴリ対応）
     * 順序が重要：より具体的なキーワードを先に定義
     */
    val PACKAGE_KEYWORDS = linkedMapOf(
        // -- MONEY系 --
        "bank" to AppCategory.BANKING,
        "ginko" to AppCategory.BANKING,  // 銀行
        "invest" to AppCategory.INVEST,
        "stock" to AppCategory.INVEST,
        "trade" to AppCategory.INVEST,
        "sec" to AppCategory.INVEST,     // 証券 (securities) - 注意: "sec"は短いので後半で精度確認
        "crypto" to AppCategory.INVEST,
        "coin" to AppCategory.INVEST,
        "paypay" to AppCategory.PAYMENT,
        "pay" to AppCategory.PAYMENT,
        "wallet" to AppCategory.PAYMENT,
        "money" to AppCategory.PAYMENT,
        "finance" to AppCategory.MONEY,

        // -- COMMUNICATION系 --
        "email" to AppCategory.EMAIL,
        "mail" to AppCategory.EMAIL,
        "meet" to AppCategory.VIDEO_CALL,
        "zoom" to AppCategory.VIDEO_CALL,
        "call" to AppCategory.VIDEO_CALL,
        "messenger" to AppCategory.MESSAGING,
        "message" to AppCategory.MESSAGING,
        "messag" to AppCategory.MESSAGING,
        "chat" to AppCategory.MESSAGING,

        // -- MEDIA系 --
        "camera" to AppCategory.PHOTO,
        "photo" to AppCategory.PHOTO,
        "gallery" to AppCategory.PHOTO,
        "image" to AppCategory.PHOTO,
        "stream" to AppCategory.STREAMING,
        "video" to AppCategory.STREAMING,
        "movie" to AppCategory.STREAMING,
        "tv" to AppCategory.STREAMING,
        "music" to AppCategory.MUSIC,
        "audio" to AppCategory.MUSIC,
        "radio" to AppCategory.MUSIC,
        "podcast" to AppCategory.MUSIC,
        "player" to AppCategory.MUSIC,
        "instagram" to AppCategory.SNS,
        "twitter" to AppCategory.SNS,
        "social" to AppCategory.SNS,
        "thread" to AppCategory.SNS,

        // -- SHOPPING系 --
        "food" to AppCategory.FOOD,
        "eat" to AppCategory.FOOD,
        "gourmet" to AppCategory.FOOD,
        "restaurant" to AppCategory.FOOD,
        "delivery" to AppCategory.FOOD,
        "demae" to AppCategory.FOOD,    // 出前
        "cook" to AppCategory.FOOD,
        "recipe" to AppCategory.FOOD,
        "fashion" to AppCategory.FASHION,
        "cloth" to AppCategory.FASHION,
        "wear" to AppCategory.FASHION,
        "uniqlo" to AppCategory.FASHION,
        "zozo" to AppCategory.FASHION,
        "shop" to AppCategory.EC,
        "store" to AppCategory.EC,
        "market" to AppCategory.EC,
        "amazon" to AppCategory.EC,
        "rakuten" to AppCategory.EC,
        "auction" to AppCategory.EC,
        "mercari" to AppCategory.EC,

        // -- WORK系 --
        "office" to AppCategory.OFFICE,
        "docs" to AppCategory.OFFICE,
        "sheet" to AppCategory.OFFICE,
        "slide" to AppCategory.OFFICE,
        "word" to AppCategory.OFFICE,
        "excel" to AppCategory.OFFICE,
        "pdf" to AppCategory.OFFICE,
        "note" to AppCategory.OFFICE,
        "slack" to AppCategory.COLLABORATION,
        "teams" to AppCategory.COLLABORATION,
        "jira" to AppCategory.COLLABORATION,
        "task" to AppCategory.COLLABORATION,

        // -- TOOL系 --
        "browser" to AppCategory.BROWSER,
        "chrome" to AppCategory.BROWSER,
        "firefox" to AppCategory.BROWSER,
        "brave" to AppCategory.BROWSER,
        "webview" to AppCategory.BROWSER,
        "clock" to AppCategory.SYSTEM,
        "calc" to AppCategory.SYSTEM,
        "file" to AppCategory.SYSTEM,
        "setting" to AppCategory.SYSTEM,
        "keyboard" to AppCategory.SYSTEM,
        "input" to AppCategory.SYSTEM,
        "launcher" to AppCategory.SYSTEM,
        "weather" to AppCategory.TOOL,
        "translate" to AppCategory.TOOL,
        "vpn" to AppCategory.TOOL,
        "auth" to AppCategory.TOOL,

        // -- 他 --
        "game" to AppCategory.GAME,
        "puzzle" to AppCategory.GAME,
        "health" to AppCategory.HEALTH,
        "fit" to AppCategory.HEALTH,
        "meditat" to AppCategory.HEALTH,
        "workout" to AppCategory.HEALTH,
        "map" to AppCategory.TRANSPORT,
        "navi" to AppCategory.TRANSPORT,
        "taxi" to AppCategory.TRANSPORT,
        "transit" to AppCategory.TRANSPORT,
        "train" to AppCategory.TRANSPORT,
        "suica" to AppCategory.TRANSPORT,
        "news" to AppCategory.NEWS,
        "learn" to AppCategory.LEARN,
        "edu" to AppCategory.LEARN,
        "study" to AppCategory.LEARN,
        "school" to AppCategory.LEARN,
        "language" to AppCategory.LEARN,
    )
}
