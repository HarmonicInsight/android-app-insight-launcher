package com.harmonic.insight.launcher.domain.classifier

import com.harmonic.insight.launcher.data.model.AppCategory

object JapaneseAppRules {
    val KNOWN_APPS = mapOf(
        // 連絡 (Communication)
        "jp.naver.line.android" to AppCategory.COMMUNICATION,
        "com.discord" to AppCategory.COMMUNICATION,
        "org.telegram.messenger" to AppCategory.COMMUNICATION,
        "com.whatsapp" to AppCategory.COMMUNICATION,
        "com.facebook.orca" to AppCategory.COMMUNICATION,
        "com.google.android.apps.messaging" to AppCategory.COMMUNICATION,
        "com.skype.raider" to AppCategory.COMMUNICATION,
        "com.viber.voip" to AppCategory.COMMUNICATION,
        "com.kakao.talk" to AppCategory.COMMUNICATION,
        "com.google.android.apps.tachyon" to AppCategory.COMMUNICATION, // Google Duo/Meet
        "com.google.android.apps.meetings" to AppCategory.COMMUNICATION,
        "us.zoom.videomeetings" to AppCategory.COMMUNICATION,
        "com.facebook.mlite" to AppCategory.COMMUNICATION, // Messenger Lite
        "jp.softbank.mb.plusmessage" to AppCategory.COMMUNICATION, // +メッセージ

        // お金 (Money/Finance)
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
        "com.moneyforward.android.app" to AppCategory.MONEY, // MoneyForward
        "jp.co.rakuten.bank" to AppCategory.MONEY,
        "jp.coincheck.android" to AppCategory.MONEY,
        "jp.co.sbi.sec" to AppCategory.MONEY, // SBI証券
        "jp.co.nomura.android" to AppCategory.MONEY, // 野村証券
        "jp.co.netbk" to AppCategory.MONEY, // 住信SBIネット銀行
        "jp.co.japannetbank" to AppCategory.MONEY, // PayPay銀行
        "jp.co.seven_bank.smartphoneapp" to AppCategory.MONEY, // セブン銀行
        "com.zaim" to AppCategory.MONEY,
        "jp.co.moneytree" to AppCategory.MONEY,
        "com.squareup.cash" to AppCategory.MONEY,
        "jp.co.rakuten.edy" to AppCategory.MONEY, // 楽天Edy

        // 移動 (Transport/Navigation)
        "jp.co.jorudan.nrkj" to AppCategory.TRANSPORT,
        "jp.co.yahoo.android.apps.transit" to AppCategory.TRANSPORT,
        "com.google.android.apps.maps" to AppCategory.TRANSPORT,
        "jp.co.jr_central.timetable" to AppCategory.TRANSPORT,
        "com.sonyericsson.suica" to AppCategory.TRANSPORT,
        "jp.co.yahoo.android.apps.navi" to AppCategory.TRANSPORT,
        "com.navitime.local.navitime" to AppCategory.TRANSPORT,
        "jp.uber.driver" to AppCategory.TRANSPORT,
        "com.didi.passenger" to AppCategory.TRANSPORT,
        "com.ubercab" to AppCategory.TRANSPORT,
        "jp.co.jreast.mobilesuica" to AppCategory.TRANSPORT, // モバイルSuica
        "jp.co.pasmo.android" to AppCategory.TRANSPORT, // PASMO
        "com.waze" to AppCategory.TRANSPORT,
        "com.sygic.aura" to AppCategory.TRANSPORT,
        "jp.co.jr_central.ex" to AppCategory.TRANSPORT, // EX予約
        "com.grab.taxi" to AppCategory.TRANSPORT,
        "jp.co.ekitan.android" to AppCategory.TRANSPORT, // 駅探

        // 買い物 (Shopping)
        "jp.co.rakuten.android" to AppCategory.SHOPPING,
        "jp.amazon.mShop.android.shopping" to AppCategory.SHOPPING,
        "com.yahoo.android.yauction" to AppCategory.SHOPPING,
        "com.zozo.android" to AppCategory.SHOPPING,
        "jp.co.uniqlo.gu.android" to AppCategory.SHOPPING,
        "com.uniqlo.catalogue" to AppCategory.SHOPPING,
        "jp.co.yahoo.android.yshopping" to AppCategory.SHOPPING,
        "com.alibaba.aliexpresshd" to AppCategory.SHOPPING,
        "jp.co.shein.android" to AppCategory.SHOPPING,
        "com.qoo10.jp.android" to AppCategory.SHOPPING,
        "jp.co.sevenandihd.android" to AppCategory.SHOPPING, // セブン&アイ
        "jp.co.aeon.app" to AppCategory.SHOPPING, // イオン
        "com.coupang.global" to AppCategory.SHOPPING,

        // ニュース (News/SNS)
        "jp.smartnews.android" to AppCategory.NEWS,
        "com.yahoo.android.yjtop" to AppCategory.NEWS,
        "jp.gocro.smartnews" to AppCategory.NEWS,
        "com.twitter.android" to AppCategory.NEWS,
        "com.google.android.apps.magazines" to AppCategory.NEWS,
        "com.google.android.googlequicksearchbox" to AppCategory.NEWS,
        "com.facebook.katana" to AppCategory.NEWS,
        "com.reddit.frontpage" to AppCategory.NEWS,
        "jp.co.nhk.news" to AppCategory.NEWS, // NHKニュース
        "com.nikkei.paper" to AppCategory.NEWS, // 日経新聞
        "com.gunosy.android" to AppCategory.NEWS, // グノシー
        "com.newspicks" to AppCategory.NEWS,
        "jp.antenna.app" to AppCategory.NEWS, // Antenna
        "com.threads.android" to AppCategory.NEWS,

        // 仕事 (Work/Productivity)
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
        "com.microsoft.office.powerpoint" to AppCategory.WORK,
        "com.microsoft.office.onenote" to AppCategory.WORK,
        "com.google.android.keep" to AppCategory.WORK, // Google Keep
        "com.google.android.apps.tasks" to AppCategory.WORK,
        "com.todoist" to AppCategory.WORK,
        "com.evernote" to AppCategory.WORK,
        "com.dropbox.android" to AppCategory.WORK,
        "com.google.android.apps.dynamite" to AppCategory.WORK, // Google Chat
        "jp.co.cybozu.kintone" to AppCategory.WORK, // kintone
        "com.atlassian.android.jira.core" to AppCategory.WORK,
        "com.microsoft.office.officehubrow" to AppCategory.WORK, // Microsoft 365
        "com.adobe.reader" to AppCategory.WORK,

        // 写真・動画 (Media/Entertainment)
        "com.google.android.apps.photos" to AppCategory.MEDIA,
        "com.instagram.android" to AppCategory.MEDIA,
        "com.zhiliaoapp.musically" to AppCategory.MEDIA, // TikTok
        "com.google.android.youtube" to AppCategory.MEDIA,
        "com.netflix.mediaclient" to AppCategory.MEDIA,
        "com.amazon.avod.thirdpartyclient" to AppCategory.MEDIA, // Amazon Prime Video
        "jp.nicovideo.nicobox" to AppCategory.MEDIA,
        "com.abema" to AppCategory.MEDIA,
        "jp.co.tver" to AppCategory.MEDIA,
        "com.spotify.music" to AppCategory.MEDIA,
        "com.apple.android.music" to AppCategory.MEDIA,
        "jp.happyon.android" to AppCategory.MEDIA, // Hulu Japan
        "com.disney.disneyplus" to AppCategory.MEDIA,
        "com.dmm.app.dreader" to AppCategory.MEDIA, // DMMブックス
        "jp.co.bandainamcoent.BNEI0242" to AppCategory.MEDIA, // バンナム
        "com.dazn" to AppCategory.MEDIA,
        "com.snaptube.premium" to AppCategory.MEDIA,
        "com.google.android.apps.youtube.music" to AppCategory.MEDIA,
        "jp.co.dwango.niconico" to AppCategory.MEDIA, // ニコニコ動画
        "com.amazon.mp3" to AppCategory.MEDIA, // Amazon Music
        "jp.co.u_next.android" to AppCategory.MEDIA, // U-NEXT
        "com.pinterest" to AppCategory.MEDIA,
        "com.google.android.apps.youtube.kids" to AppCategory.MEDIA,
        "jp.co.radiko.player" to AppCategory.MEDIA, // radiko
        "fm.castbox.audiobook.radio.podcast" to AppCategory.MEDIA,
        "com.soundcloud.android" to AppCategory.MEDIA,

        // フード (Food/Delivery)
        "com.ubereats.waiter" to AppCategory.SHOPPING,
        "com.demaecan.android" to AppCategory.SHOPPING, // 出前館
        "jp.co.recruit.hotpepper.gourmet" to AppCategory.SHOPPING, // ホットペッパーグルメ
        "com.tabelog.android" to AppCategory.SHOPPING, // 食べログ
        "jp.gurunavi.android" to AppCategory.SHOPPING, // ぐるなび
        "com.wolt.android" to AppCategory.SHOPPING,
        "com.mcdonalds.mobileapp.jp" to AppCategory.SHOPPING,
        "com.starbucks.jp" to AppCategory.SHOPPING,
        "jp.co.cookpad" to AppCategory.SHOPPING, // クックパッド

        // ゲーム (Games)
        "jp.pokemon.pokemonunite" to AppCategory.GAME,
        "com.miHoYo.GenshinImpact" to AppCategory.GAME,
        "com.supercell.clashofclans" to AppCategory.GAME,
        "com.supercell.clashroyale" to AppCategory.GAME,
        "jp.konami.pesam" to AppCategory.GAME, // eFootball
        "com.activision.callofduty.shooter" to AppCategory.GAME,
        "com.nintendo.zaka" to AppCategory.GAME, // マリオカートツアー
        "com.nintendo.zara" to AppCategory.GAME, // どうぶつの森
        "jp.co.mixi.monsterstrike" to AppCategory.GAME, // モンスト
        "jp.gungho.padEN" to AppCategory.GAME, // パズドラ
        "com.aniplex.fategrandorder" to AppCategory.GAME, // FGO
        "com.mobilelegends.mi" to AppCategory.GAME,
        "jp.co.cygames.umamusume" to AppCategory.GAME, // ウマ娘
        "com.square_enix.android_googleplay.FFBEWW" to AppCategory.GAME, // FFBE
        "com.dena.a12026418" to AppCategory.GAME, // ポケモンマスターズ
        "com.bushiroad.en.bangdreamgbp" to AppCategory.GAME, // バンドリ
        "com.klab.lovelive.allstars" to AppCategory.GAME, // ラブライブ
        "com.kakaogames.umamusume" to AppCategory.GAME,
        "jp.co.craftegg.band" to AppCategory.GAME, // プロセカ
        "com.riotgames.league.wildrift" to AppCategory.GAME,

        // 健康 (Health/Fitness)
        "com.google.android.apps.fitness" to AppCategory.HEALTH,
        "jp.co.fincorporation.finc" to AppCategory.HEALTH,
        "com.nike.plusgps" to AppCategory.HEALTH,
        "com.strava" to AppCategory.HEALTH,
        "com.myfitnesspal.android" to AppCategory.HEALTH,
        "jp.co.ohisamaseikatsu.app" to AppCategory.HEALTH,
        "com.calm.android" to AppCategory.HEALTH,
        "com.headspace.android" to AppCategory.HEALTH,
        "jp.co.linkandcommunication.calendar" to AppCategory.HEALTH, // あすけん
        "com.fitbit.FitbitMobile" to AppCategory.HEALTH,

        // 学び (Learning/Education)
        "com.duolingo" to AppCategory.LEARN,
        "jp.co.benesse.touch" to AppCategory.LEARN, // ベネッセ
        "com.google.android.apps.classroom" to AppCategory.LEARN,
        "org.wikipedia" to AppCategory.LEARN,
        "com.quizlet.quizletandroid" to AppCategory.LEARN,
        "jp.studyplus" to AppCategory.LEARN,
        "com.udemy.android" to AppCategory.LEARN,
        "jp.co.alc" to AppCategory.LEARN, // アルク
        "com.khanacademy.android" to AppCategory.LEARN,
        "jp.mikan.and.mikan" to AppCategory.LEARN, // mikan英単語

        // 便利ツール (Utilities/Tools)
        "com.google.android.deskclock" to AppCategory.TOOL,
        "com.google.android.calculator" to AppCategory.TOOL,
        "com.google.android.apps.walletnfcrel" to AppCategory.TOOL,
        "com.android.vending" to AppCategory.TOOL, // Play Store
        "com.google.android.apps.translate" to AppCategory.TOOL,
        "com.google.android.contacts" to AppCategory.TOOL,
        "com.google.android.dialer" to AppCategory.TOOL,
        "com.google.android.apps.nbu.files" to AppCategory.TOOL, // Files by Google
        "com.google.android.gms" to AppCategory.TOOL,
        "com.android.chrome" to AppCategory.TOOL,
        "org.mozilla.firefox" to AppCategory.TOOL,
        "com.brave.browser" to AppCategory.TOOL,
        "com.google.android.inputmethod.latin" to AppCategory.TOOL, // Gboard
        "com.sec.android.inputmethod" to AppCategory.TOOL, // Samsung Keyboard
        "jp.co.yahoo.android.yweather" to AppCategory.TOOL, // Yahoo!天気
        "com.weather.Weather" to AppCategory.TOOL,
        "com.google.android.apps.authenticator2" to AppCategory.TOOL,
        "com.onepassword.android" to AppCategory.TOOL,
        "com.nordvpn.android" to AppCategory.TOOL,

        // キャリア (Mobile carrier apps → TOOL)
        "com.nttdocomo.android.docomoset" to AppCategory.TOOL,
        "com.nttdocomo.android.mypage" to AppCategory.TOOL, // My docomo
        "com.kddi.android.aufx.infonotice" to AppCategory.TOOL, // au
        "jp.softbank.mb.mysoftbank" to AppCategory.TOOL, // My SoftBank
        "jp.co.rakuten.mobile.mypage" to AppCategory.TOOL, // my楽天モバイル

        // 不動産・生活 (Real estate/lifestyle → SHOPPING)
        "jp.co.recruit.suumo.android" to AppCategory.SHOPPING, // SUUMO
        "jp.co.homes.android" to AppCategory.SHOPPING, // LIFULL HOME'S
    )
}
