package com.harmonic.insight.launcher.domain.classifier

import com.harmonic.insight.launcher.data.model.AppCategory

/**
 * キーワード自動分類では正しく分類できないアプリのみ定義。
 * キーワードで分類可能なアプリは CategoryMapping.PACKAGE_KEYWORDS に委任。
 */
object JapaneseAppRules {
    val KNOWN_APPS = mapOf(
        // -- キーワードでは拾えない日本固有アプリ --

        // 連絡 - メッセージ
        "jp.naver.line.android" to AppCategory.MESSAGING,

        // 決済（パッケージ名に pay/wallet が含まれないもの）
        "jp.ne.paypay.android.app" to AppCategory.PAYMENT,
        "jp.d_payment.app" to AppCategory.PAYMENT,
        "jp.aupay.wallet" to AppCategory.PAYMENT,
        "com.mercari" to AppCategory.EC, // mercariはキーワードで拾える

        // SNS（パッケージ名から判別不能）
        "com.zhiliaoapp.musically" to AppCategory.SNS, // TikTok
        "com.facebook.katana" to AppCategory.SNS,
        "com.threads.android" to AppCategory.SNS,

        // ニュース（パッケージ名から判別不能）
        "com.yahoo.android.yjtop" to AppCategory.NEWS,
        "jp.smartnews.android" to AppCategory.NEWS,
        "jp.gocro.smartnews" to AppCategory.NEWS,
        "com.gunosy.android" to AppCategory.NEWS,
        "com.newspicks" to AppCategory.NEWS,

        // 動画配信（パッケージ名から判別不能）
        "com.netflix.mediaclient" to AppCategory.STREAMING,
        "com.abema" to AppCategory.STREAMING,
        "jp.co.tver" to AppCategory.STREAMING,
        "jp.happyon.android" to AppCategory.STREAMING, // Hulu
        "com.disney.disneyplus" to AppCategory.STREAMING,
        "jp.co.u_next.android" to AppCategory.STREAMING,
        "com.dazn" to AppCategory.STREAMING,
        "jp.co.dwango.niconico" to AppCategory.STREAMING,
        "com.amazon.avod.thirdpartyclient" to AppCategory.STREAMING,

        // 音楽（パッケージ名から判別不能）
        "com.spotify.music" to AppCategory.MUSIC,

        // 写真
        "com.instagram.android" to AppCategory.PHOTO,
        "com.pinterest" to AppCategory.PHOTO,

        // 交通（パッケージ名から判別不能）
        "jp.co.jorudan.nrkj" to AppCategory.TRANSPORT,
        "com.sonyericsson.suica" to AppCategory.TRANSPORT,
        "jp.co.jreast.mobilesuica" to AppCategory.TRANSPORT,
        "jp.co.pasmo.android" to AppCategory.TRANSPORT,
        "com.ubercab" to AppCategory.TRANSPORT,
        "jp.co.ekitan.android" to AppCategory.TRANSPORT,

        // フード（パッケージ名から判別不能）
        "com.ubereats.waiter" to AppCategory.FOOD,
        "com.demaecan.android" to AppCategory.FOOD,
        "com.tabelog.android" to AppCategory.FOOD,
        "jp.gurunavi.android" to AppCategory.FOOD,
        "com.wolt.android" to AppCategory.FOOD,
        "jp.co.recruit.hotpepper.gourmet" to AppCategory.FOOD,
        "jp.co.cookpad" to AppCategory.FOOD,
        "com.mcdonalds.mobileapp.jp" to AppCategory.FOOD,
        "com.starbucks.jp" to AppCategory.FOOD,

        // ゲーム（パッケージ名から判別不能）
        "com.miHoYo.GenshinImpact" to AppCategory.GAME,
        "jp.co.mixi.monsterstrike" to AppCategory.GAME,
        "jp.gungho.padEN" to AppCategory.GAME,
        "com.aniplex.fategrandorder" to AppCategory.GAME,
        "jp.co.cygames.umamusume" to AppCategory.GAME,
        "jp.co.craftegg.band" to AppCategory.GAME,
        "com.supercell.clashofclans" to AppCategory.GAME,
        "com.supercell.clashroyale" to AppCategory.GAME,

        // 銀行（パッケージ名に bank が含まれないもの）
        "jp.mufg.bk.applisp.app" to AppCategory.BANKING,
        "jp.co.smbc.direct" to AppCategory.BANKING,
        "jp.co.netbk" to AppCategory.BANKING,

        // オフィス（パッケージ名から判別不能）
        "com.notion.id" to AppCategory.OFFICE,
        "com.evernote" to AppCategory.OFFICE,
        "com.todoist" to AppCategory.COLLABORATION,
        "com.dropbox.android" to AppCategory.OFFICE,
        "com.adobe.reader" to AppCategory.OFFICE,

        // ツール
        "com.android.vending" to AppCategory.SYSTEM, // Play Store
        "com.google.android.apps.walletnfcrel" to AppCategory.PAYMENT, // Google Wallet
        "jp.co.yahoo.android.yweather" to AppCategory.TOOL,

        // 健康
        "jp.co.fincorporation.finc" to AppCategory.HEALTH,
        "jp.co.linkandcommunication.calendar" to AppCategory.HEALTH,

        // 学び
        "com.duolingo" to AppCategory.LEARN,
        "jp.studyplus" to AppCategory.LEARN,
        "jp.mikan.and.mikan" to AppCategory.LEARN,
    )
}
