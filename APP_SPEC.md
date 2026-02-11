# Insight Launcher - アプリ仕様書

> **バージョン:** 1.0.0
> **開発:** Harmonic Insight
> **パッケージ名:** `com.harmonic.insight.launcher`
> **対象:** Android 8.0+ (API 26) / ターゲット Android 15 (API 35)

---

## 1. プロダクト概要

### 1.1 コンセプト
日本のAndroidユーザー向けの**AI自動分類ランチャー**。インストールされたアプリを自動的にカテゴリ・サブカテゴリに分類し、階層表示する。外部API不使用、すべての分類処理は端末上で完結。

### 1.2 ターゲットユーザー
- アプリが多くて整理に困っている人
- カテゴリごとにアプリを素早く見つけたい人
- 日本語環境のAndroidユーザー

### 1.3 主な特徴
- アプリの完全自動分類（手動設定不要）
- 12カテゴリ + 16サブカテゴリの階層表示
- 使用頻度順ソート
- 壁紙透過のミニマルデザイン
- Material You (Material 3) 対応

---

## 2. 画面構成

### 2.1 ホーム画面 (`HomeScreen`)

| 要素 | 説明 |
|------|------|
| 設定ボタン | 右上、半透明白アイコン |
| 時計ウィジェット | 中央、日本語形式（M月d日(E) / HH:mm）、1秒更新 |
| カテゴリタブ | 横スクロール、トップレベルカテゴリ一覧 |
| カテゴリページ | 水平ページャー（スワイプ切替）、4列グリッド |
| サブカテゴリヘッダー | 半透明バッジ（例: "🍔 フード"） |
| 検索バー | タップで検索画面へ遷移 |
| ドック | 下部固定4枠（電話・カメラ・ブラウザ・設定） |

**初回起動時:** オンボーディングダイアログを表示。

**ページ構造例:**
```
[💬 連絡] [💰 お金] [💼 仕事] [🚃 移動] [🛒 買い物] ...
───────────────────────────────────────────
💳 決済
  PayPay  LINE Pay  d払い  au PAY

🏦 銀行
  三菱UFJ  SMBC  みずほ

📈 投資・資産
  SBI証券  楽天証券  コインチェック
```

### 2.2 アプリドロワー (`AppDrawerScreen`)

| 要素 | 説明 |
|------|------|
| 検索バー | テキスト入力可、リアルタイムフィルタ |
| カテゴリタブ | トップレベルカテゴリ横スクロール |
| 表示切替 | リスト / グリッドトグル |
| アプリ一覧 | リスト: アイコン+名前 / グリッド: 4列 |
| コンテキストメニュー | 長押し → カテゴリ変更 / アプリ情報 / アンインストール |

**操作:**
- 下スワイプ（100px以上）でホームに戻る
- アンインストール時は確認ダイアログ表示

### 2.3 検索画面 (`SearchScreen`)

| 要素 | 説明 |
|------|------|
| 検索入力欄 | 自動フォーカス、戻るボタン付き |
| 最近のアプリ | クエリ空欄時、使用頻度上位5件表示 |
| 検索結果 | インクリメンタル検索、アイコン+名前+カテゴリ表示 |

### 2.4 設定画面 (`SettingsScreen`)

| セクション | 項目 | 内容 |
|------------|------|------|
| 表示 | ドロワー表示 | リスト / グリッド（ラジオボタン） |
| 表示 | アイコンサイズ | 小 / 中 / 大 |
| カテゴリ管理 | 再分類 | 全アプリを再分類（確認ダイアログ、手動変更は保持） |
| アプリ情報 | バージョン | BuildConfig.VERSION_NAME |
| アプリ情報 | 開発者 | Harmonic Insight |
| その他 | デフォルトランチャー | 端末のホームアプリ設定を開く |

---

## 3. 自動分類システム

### 3.1 分類フロー（3段階フォールバック）

```
アプリインストール / 起動時
  │
  ├─ ① JapaneseAppRules（約80件の例外ルール）
  │    パッケージ名の完全一致 → カテゴリ確定
  │    例: jp.naver.line.android → MESSAGING
  │
  ├─ ② Android システムカテゴリ（8マッピング）
  │    ApplicationInfo.category を参照
  │    例: CATEGORY_GAME → GAME
  │
  ├─ ③ パッケージ名キーワード（140+パターン）
  │    パッケージ名にキーワードが含まれるか判定
  │    例: "bank" を含む → BANKING
  │
  └─ ④ フォールバック → OTHER（その他）
```

### 3.2 カテゴリ体系

| トップレベル | サブカテゴリ |
|-------------|-------------|
| 💬 連絡 (COMMUNICATION) | メッセージ (MESSAGING), ビデオ通話 (VIDEO_CALL), メール (EMAIL) |
| 💰 お金 (MONEY) | 決済 (PAYMENT), 銀行 (BANKING), 投資・資産 (INVEST) |
| 💼 仕事 (WORK) | オフィス (OFFICE), コラボ (COLLABORATION) |
| 🚃 移動 (TRANSPORT) | ― |
| 🛒 買い物 (SHOPPING) | フード (FOOD), ファッション (FASHION), ネット通販 (EC) |
| 📰 ニュース (NEWS) | ― |
| 📷 写真・動画 (MEDIA) | 動画配信 (STREAMING), 音楽 (MUSIC), 写真 (PHOTO), SNS |
| 🎮 ゲーム (GAME) | ― |
| ❤️ 健康 (HEALTH) | ― |
| 🔧 便利ツール (TOOL) | ブラウザ (BROWSER), システム (SYSTEM) |
| 📚 学び (LEARN) | ― |
| 📱 その他 (OTHER) | ― |

### 3.3 キーワードマッピング（抜粋）

| キーワード | 分類先 | 例 |
|-----------|--------|-----|
| `bank`, `ginko` | 🏦 銀行 | jp.co.smbc.direct |
| `pay`, `wallet` | 💳 決済 | jp.ne.paypay.android.app |
| `invest`, `stock`, `crypto` | 📈 投資 | jp.coincheck.android |
| `messenger`, `chat` | 💬 メッセージ | com.whatsapp |
| `camera`, `photo` | 📸 写真 | com.google.android.apps.photos |
| `video`, `stream`, `tv` | 🎬 動画配信 | com.google.android.youtube |
| `music`, `radio`, `podcast` | 🎵 音楽 | com.spotify.music |
| `food`, `restaurant`, `delivery` | 🍔 フード | com.ubereats.waiter |
| `shop`, `store`, `amazon` | 📦 ネット通販 | jp.amazon.mShop.android.shopping |
| `game`, `puzzle` | 🎮 ゲーム | （各ゲームアプリ） |
| `browser`, `chrome` | 🌐 ブラウザ | com.android.chrome |

### 3.4 手動分類の保護
- ユーザーが手動で変更したカテゴリは `isUserCategorized = true` フラグで保護
- 再分類実行時でも手動変更は上書きされない
- 個別のカテゴリ変更はホーム画面・ドロワーの長押しメニューから可能

---

## 3.5 カスタマイズ機能

### アプリ長押しメニュー（ホーム画面）
ホーム画面のカテゴリページ内のアプリアイコンを長押しすると、以下の操作が可能:

| 操作 | 説明 |
|------|------|
| カテゴリを移動 | 全カテゴリ一覧から移動先を選択。`isUserCategorized` フラグが設定される |
| アプリ情報 | 端末のアプリ情報画面を開く |
| アンインストール | 確認ダイアログ後、端末のアンインストール画面を開く |

### カテゴリピッカー
- 全28カテゴリ（トップレベル12 + サブカテゴリ16）を一覧表示
- 現在のカテゴリに ✓ マーク + 太字で表示
- 各カテゴリはアイコン + 表示名で識別

### ドックカスタマイズ
ドック（下部固定4枠）のアプリアイコンを長押しすると:

| 操作 | 説明 |
|------|------|
| ドックから削除 | 該当スロットを空にする |
| アプリを選択 | 全アプリ一覧からドックに配置するアプリを選択 |

**初期ドック:** 電話・カメラ・ブラウザ・設定（端末のデフォルトアプリを自動検出）

---

## 4. データモデル

### 4.1 Room データベース

**データベース名:** `insight_launcher.db`
**バージョン:** 1

#### apps テーブル

| カラム | 型 | 説明 |
|--------|-----|------|
| packageName | TEXT (PK) | パッケージ名 |
| appName | TEXT | 表示名 |
| category | TEXT | AppCategory enum名 |
| isUserCategorized | INTEGER | 手動分類フラグ (0/1) |
| lastUsedTimestamp | INTEGER | 最終使用タイムスタンプ |

#### dock テーブル

| カラム | 型 | 説明 |
|--------|-----|------|
| position | INTEGER (PK) | ドック位置 (0-3) |
| packageName | TEXT | パッケージ名 |

#### favorites テーブル

| カラム | 型 | 説明 |
|--------|-----|------|
| packageName | TEXT (PK) | パッケージ名 |
| position | INTEGER | 表示順 |

#### settings テーブル

| カラム | 型 | 説明 |
|--------|-----|------|
| key | TEXT (PK) | 設定キー |
| value | TEXT | 設定値 |

**使用中の設定キー:**
- `category_order` — カテゴリ表示順（カンマ区切り）
- `drawer_view_mode` — `"list"` or `"grid"`
- `icon_size` — `"small"` / `"medium"` / `"large"`
- `onboarding_completed` — `"true"` / 未設定

### 4.2 データクラス

```kotlin
data class AppInfo(
    val packageName: String,
    val appName: String,
    val icon: Drawable?,
    val category: AppCategory,
    val isUserCategorized: Boolean = false,
    val lastUsedTimestamp: Long = 0L,
)

data class SubCategoryGroup(
    val subCategory: AppCategory?,  // nullなら親カテゴリ直属
    val apps: List<AppInfo>,
)
```

---

## 5. 技術アーキテクチャ

### 5.1 技術スタック

| レイヤー | 技術 |
|----------|------|
| 言語 | Kotlin |
| UI | Jetpack Compose + Material 3 (Material You) |
| DI | Hilt (Dagger) |
| DB | Room |
| 非同期 | Coroutines + Flow |
| ナビゲーション | Navigation Compose |
| ビルド | Gradle KTS, KSP |

### 5.2 アーキテクチャパターン

```
UI Layer (Compose)
  ├── Screen (Composable)
  └── ViewModel (StateFlow)
         │
Domain Layer
  ├── UseCase
  └── Classifier
         │
Data Layer
  ├── Repository (Singleton)
  ├── Room DAO
  └── BroadcastReceiver
```

**MVVM + Repository パターン** を採用。

### 5.3 パッケージ構成

```
com.harmonic.insight.launcher/
├── InsightLauncherApp.kt       # Hilt Application
├── MainActivity.kt             # ランチャーActivity + Navigation
├── data/
│   ├── model/                  # AppCategory, AppInfo, CategoryMapping
│   ├── local/                  # Room DB, DAO, Entity, Converters
│   ├── repository/             # AppRepository, CategoryRepository
│   └── receiver/               # PackageReceiver
├── domain/
│   ├── classifier/             # AppClassifier, JapaneseAppRules, PlayStoreCategoryMapper
│   └── usecase/                # GetCategorizedApps, LaunchApp, SearchApps
├── ui/
│   ├── home/                   # HomeScreen + ViewModel
│   ├── drawer/                 # AppDrawerScreen + ViewModel
│   ├── search/                 # SearchScreen + ViewModel
│   ├── settings/               # SettingsScreen + ViewModel
│   ├── components/             # AppIcon, CategoryTab, ClockWidget, SearchBar
│   └── theme/                  # Color, Type, Theme
├── di/                         # Hilt AppModule
└── util/                       # PackageUtils, IconUtils
```

### 5.4 画面遷移

```
                    ┌──────────┐
        ┌──────────│  HOME    │──────────┐
        │          │ (start)  │          │
        │          └──────────┘          │
        ▼               │               ▼
  ┌──────────┐          │         ┌──────────┐
  │  SEARCH  │          │         │ SETTINGS │
  │ (fade)   │          │         │ (slide←) │
  └──────────┘          ▼         └──────────┘
                  ┌──────────┐
                  │  DRAWER  │
                  │ (slide↑) │
                  └──────────┘
```

すべて `popBackStack()` で戻る。

### 5.5 データフロー

```
1. 起動時: refreshInstalledApps() → DB更新 → Flow emit
2. 画面表示: ViewModel.collect → UI State更新 → Recompose
3. アプリ起動: launchApp() → recordAppLaunch() → lastUsedTimestamp更新
4. アプリ追加/削除: PackageReceiver → onPackageAdded/Removed → DB更新 → Flow emit
5. 再分類: reclassifyApps() → 全アプリ再スキャン（手動変更保持）→ DB更新
```

### 5.6 パフォーマンス考慮

| 対策 | 内容 |
|------|------|
| IO スレッド | アイコン読み込みを `flowOn(Dispatchers.IO)` で実行 |
| バッチ処理 | `insertApps()` で一括DB挿入 |
| N+1回避 | タイムスタンプをバッチ取得してから分類 |
| 並行初期化 | `loadCategorizedApps()` と dock 観察を別コルーチンで実行 |
| 検索デバウンス | 前回のJobをキャンセルして再実行 |

---

## 6. 権限

| 権限 | 用途 | 必須 |
|------|------|------|
| `QUERY_ALL_PACKAGES` | インストール済みアプリの列挙 | 必須 |
| `PACKAGE_USAGE_STATS` | 使用統計の取得 | 任意 |

---

## 7. 多言語対応

| 言語 | リソース | 説明 |
|------|----------|------|
| 日本語 | `values/strings.xml` | デフォルト（52文字列） |
| 英語 | `values-en/strings.xml` | フォールバック（52文字列） |

カテゴリ名は `AppCategory.displayName` に日本語で直接定義。

---

## 8. ビルド・リリース

### 8.1 ビルド設定
- **compileSdk:** 35
- **minSdk:** 26 (Android 8.0 Oreo)
- **targetSdk:** 35 (Android 15)
- **Java:** 17
- **Compose BOM:** 2024.12.01

### 8.2 署名
- リリースビルドは環境変数から keystore 情報を取得
  - `KEYSTORE_PATH`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`

### 8.3 主要依存ライブラリ

| ライブラリ | バージョン | 用途 |
|-----------|----------|------|
| Compose BOM | 2024.12.01 | UI フレームワーク |
| Navigation Compose | 2.8.5 | 画面遷移 |
| Hilt | 2.53.1 | 依存性注入 |
| Room | 2.6.1 | ローカルDB |
| Coroutines | 1.9.0 | 非同期処理 |
| Core KTX | 1.15.0 | Android拡張 |

---

## 9. 今後の拡張予定

- [x] ドックのカスタマイズ（長押しで入替・削除）
- [x] アプリのカテゴリ手動変更（長押しメニュー）
- [ ] カスタムフォルダ機能（ユーザー定義のアプリグループ）
- [ ] ドラッグ&ドロップでのアプリ並び替え
- [ ] ウィジェット対応
- [ ] アプリ使用統計ダッシュボード
- [ ] プライバシーポリシーページ
- [ ] アプリ非表示機能
