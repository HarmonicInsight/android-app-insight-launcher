package com.harmonic.insight.launcher.domain.classifier

import com.harmonic.insight.launcher.data.model.AppCategory

object JapaneseAppRules {
    val KNOWN_APPS = mapOf(
        // 連絡
        "jp.naver.line.android" to AppCategory.COMMUNICATION,
        "com.discord" to AppCategory.COMMUNICATION,
        "org.telegram.messenger" to AppCategory.COMMUNICATION,
        "com.whatsapp" to AppCategory.COMMUNICATION,
        "com.facebook.orca" to AppCategory.COMMUNICATION,
        "com.google.android.apps.messaging" to AppCategory.COMMUNICATION,

        // お金
        "jp.ne.paypay.android.app" to AppCategory.MONEY,
        "jp.mufg.bk.applisp.app" to AppCategory.MONEY,
        "com.japan.bank.mizuho" to AppCategory.MONEY,
        "jp.co.smbc.direct" to AppCategory.MONEY,
        "jp.co.rakuten.pay" to AppCategory.MONEY,
        "com.mercari" to AppCategory.MONEY,
        "jp.co.aeonbank.android.passbook" to AppCategory.MONEY,
        "com.linecorp.linepay" to AppCategory.MONEY,
        "jp.d_payment.app" to AppCategory.MONEY,
        "jp.aupay.wallet" to AppCategory.MONEY,

        // 移動
        "jp.co.jorudan.nrkj" to AppCategory.TRANSPORT,
        "jp.co.yahoo.android.apps.transit" to AppCategory.TRANSPORT,
        "com.google.android.apps.maps" to AppCategory.TRANSPORT,
        "jp.co.jr_central.timetable" to AppCategory.TRANSPORT,
        "com.sonyericsson.suica" to AppCategory.TRANSPORT,
        "jp.co.yahoo.android.apps.navi" to AppCategory.TRANSPORT,
        "com.navitime.local.navitime" to AppCategory.TRANSPORT,
        "jp.uber.driver" to AppCategory.TRANSPORT,
        "com.didi.passenger" to AppCategory.TRANSPORT,

        // 買い物
        "jp.co.rakuten.android" to AppCategory.SHOPPING,
        "jp.amazon.mShop.android.shopping" to AppCategory.SHOPPING,
        "com.yahoo.android.yauction" to AppCategory.SHOPPING,
        "com.zozo.android" to AppCategory.SHOPPING,
        "jp.co.uniqlo.gu.android" to AppCategory.SHOPPING,
        "com.uniqlo.catalogue" to AppCategory.SHOPPING,

        // ニュース
        "jp.smartnews.android" to AppCategory.NEWS,
        "com.yahoo.android.yjtop" to AppCategory.NEWS,
        "jp.gocro.smartnews" to AppCategory.NEWS,
        "com.twitter.android" to AppCategory.NEWS,
        "com.google.android.apps.magazines" to AppCategory.NEWS,

        // 仕事
        "com.microsoft.teams" to AppCategory.WORK,
        "com.slack" to AppCategory.WORK,
        "com.Slack" to AppCategory.WORK,
        "com.microsoft.office.outlook" to AppCategory.WORK,
        "com.google.android.apps.docs" to AppCategory.WORK,
        "com.google.android.apps.docs.editors.sheets" to AppCategory.WORK,
        "com.google.android.apps.docs.editors.slides" to AppCategory.WORK,
        "com.google.android.gm" to AppCategory.WORK,
        "com.google.android.calendar" to AppCategory.WORK,
        "com.microsoft.office.word" to AppCategory.WORK,
        "com.microsoft.office.excel" to AppCategory.WORK,
        "com.notion.id" to AppCategory.WORK,

        // 写真・動画
        "com.google.android.apps.photos" to AppCategory.MEDIA,
        "com.instagram.android" to AppCategory.MEDIA,
        "com.zhiliaoapp.musically" to AppCategory.MEDIA, // TikTok
        "com.google.android.youtube" to AppCategory.MEDIA,
        "com.netflix.mediaclient" to AppCategory.MEDIA,
        "com.amazon.avod.thirdpartyclient" to AppCategory.MEDIA,
        "jp.nicovideo.nicobox" to AppCategory.MEDIA,
        "com.abema" to AppCategory.MEDIA,
        "jp.co.tver" to AppCategory.MEDIA,
        "com.spotify.music" to AppCategory.MEDIA,
        "com.apple.android.music" to AppCategory.MEDIA,

        // 健康
        "com.google.android.apps.fitness" to AppCategory.HEALTH,
        "jp.co.fincorporation.finc" to AppCategory.HEALTH,

        // 学び
        "com.duolingo" to AppCategory.LEARN,

        // 便利ツール
        "com.google.android.deskclock" to AppCategory.TOOL,
        "com.google.android.calculator" to AppCategory.TOOL,
        "com.google.android.apps.walletnfcrel" to AppCategory.TOOL,
        "com.android.vending" to AppCategory.TOOL, // Play Store
    )
}
