package com.harmonic.insight.launcher.data.model

enum class AppCategory(val displayName: String, val icon: String) {
    // -- 親カテゴリ (Top-level) --
    COMMUNICATION("連絡", "💬"),
    MONEY("お金", "💰"),
    WORK("仕事", "💼"),
    TRANSPORT("移動", "🚃"),
    SHOPPING("買い物", "🛒"),
    NEWS("ニュース", "📰"),
    MEDIA("写真・動画", "📷"),
    GAME("ゲーム", "🎮"),
    HEALTH("健康", "❤️"),
    TOOL("便利ツール", "🔧"),
    LEARN("学び", "📚"),
    OTHER("その他", "📱"),

    // -- サブカテゴリ (Sub-categories) --
    // COMMUNICATION
    MESSAGING("メッセージ", "💬"),
    VIDEO_CALL("ビデオ通話", "📹"),
    EMAIL("メール", "📧"),

    // MONEY
    PAYMENT("決済", "💳"),
    BANKING("銀行", "🏦"),
    INVEST("投資・資産", "📈"),

    // SHOPPING
    FOOD("フード", "🍔"),
    FASHION("ファッション", "👗"),
    EC("ネット通販", "📦"),

    // MEDIA
    STREAMING("動画配信", "🎬"),
    MUSIC("音楽", "🎵"),
    PHOTO("写真", "📸"),
    SNS("SNS", "📱"),

    // WORK
    OFFICE("オフィス", "📝"),
    COLLABORATION("コラボ", "👥"),

    // TOOL
    BROWSER("ブラウザ", "🌐"),
    SYSTEM("システム", "⚙️"),
    ;

    companion object {
        /** サブカテゴリ → 親カテゴリの定義 */
        val HIERARCHY: Map<AppCategory, List<AppCategory>> = mapOf(
            COMMUNICATION to listOf(MESSAGING, VIDEO_CALL, EMAIL),
            MONEY to listOf(PAYMENT, BANKING, INVEST),
            SHOPPING to listOf(FOOD, FASHION, EC),
            MEDIA to listOf(STREAMING, MUSIC, PHOTO, SNS),
            WORK to listOf(OFFICE, COLLABORATION),
            TOOL to listOf(BROWSER, SYSTEM),
        )

        private val childToParent: Map<AppCategory, AppCategory> by lazy {
            HIERARCHY.flatMap { (parent, children) ->
                children.map { child -> child to parent }
            }.toMap()
        }

        /** サブカテゴリの親を取得（トップレベルならnull） */
        fun parentOf(category: AppCategory): AppCategory? = childToParent[category]

        /** トップレベルカテゴリ一覧 */
        fun topLevel(): List<AppCategory> {
            val allChildren = HIERARCHY.values.flatten().toSet()
            return entries.filter { it !in allChildren }
        }

        /** 指定カテゴリがトップレベルかどうか */
        fun isTopLevel(category: AppCategory): Boolean = parentOf(category) == null

        /** カテゴリのトップレベル親を取得（自分がトップレベルならそのまま返す） */
        fun topLevelOf(category: AppCategory): AppCategory = parentOf(category) ?: category
    }
}
