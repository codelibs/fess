# Fess検索画面 Next.js実装 要件仕様書

## 1. プロジェクト概要

### 1.1 目的
現在JSPで実装されているFessの検索画面を、TypeScript + Next.jsで別プロジェクトとして再実装する。
Fess本体のOpenAPI（`/api/v1/*`）を利用してバックエンドと連携する。

### 1.2 対象範囲
以下のJSPページの機能を実装する：
- `index.jsp` - トップ/検索開始画面
- `search.jsp` - 検索結果表示画面
- `advance.jsp` - 詳細検索画面
- `login/index.jsp` - ログイン画面
- `help.jsp` - ヘルプ画面

### 1.3 技術スタック
- **フロントエンド**: Next.js 14+ (App Router)
- **言語**: TypeScript
- **スタイリング**: CSS Modules / Tailwind CSS（検討）
- **状態管理**: React Hooks + Context API / Zustand（検討）
- **HTTPクライアント**: fetch / axios
- **バックエンドAPI**: Fess OpenAPI v1 (`/api/v1/*`)

---

## 2. 機能要件

### 2.1 共通機能

#### 2.1.1 ヘッダー機能
- **ロゴ表示**
  - クリックでトップページに遷移
  - 画像パス: `/images/logo-head.png`

- **検索ボックス**（検索結果画面のみ）
  - 検索クエリ入力フィールド（最大1000文字）
  - オートコンプリート/サジェスト機能
  - 検索ボタン
  - オプション開閉ボタン

- **ユーザーメニュー**
  - **未ログイン時**: ログインリンク（`pageLoginLink`フラグによる表示制御）
  - **ログイン時**:
    - ユーザー名表示
    - ドロップダウンメニュー:
      - プロフィール（`editableUser`フラグで制御）
      - 管理画面（`adminUser`フラグで制御）
      - ログアウト

- **ヘルプリンク**
  - ヘルプページへのリンク

- **警告表示**
  - EOL（End of Life）警告（`eoled`フラグ）
  - 開発モード警告（`developmentMode`フラグ）

#### 2.1.2 フッター機能
- 著作権表示
- 国際化対応（i18n）のラベル表示

#### 2.1.3 国際化（i18n）
- 多言語対応（17言語）:
  - en, ja, ar, cs, de, es, fr, hu, id, it, ko, nl, pl, pt, ro, ru, th, tr, zh_CN, zh_TW
- ラベルファイル（`fess_label_*.properties`）の内容をJSON化
- 動的言語切り替え機能

#### 2.1.4 通知・メッセージ表示
- 通知バナー（`notification`変数）
- 情報メッセージ（`info`）
- エラーメッセージ（`errors`）

---

### 2.2 トップページ（Index）機能

#### 2.2.1 レイアウト
- センタリングされた検索ボックス
- ロゴ画像表示（`/images/logo.png`）

#### 2.2.2 検索フォーム
- **検索クエリ入力**
  - プロパティ: `q`
  - 最大長: 1000文字
  - オートコンプリート: off
  - ID: `contentQuery`
  - フォーカス自動設定

- **検索ボタン**
  - ラベル: `labels.index_form_search_btn`
  - 送信後3秒間ボタン無効化（二重送信防止）

- **オプションボタン**
  - 検索オプションパネルを開閉（collapse）
  - ラベル: `labels.index_form_option_btn`

#### 2.2.3 検索オプションパネル（Collapsible）
検索オプション詳細は後述（2.4節参照）

#### 2.2.4 人気ワード表示
- API: `GET /api/v1/popular-words`
- 表示件数: 最大10件（モバイルは3件）
- クリック時: 該当ワードで検索実行
- ラベル: `labels.search_popular_word_word`

#### 2.2.5 サジェスト機能
- API: `GET /api/v1/suggest-words`
- リクエストパラメータ:
  - `q`: 入力文字列
  - `num`: 10
  - `field`: `["_default", "content", "title"]`
  - `lang`: 選択中の言語
- キーボード操作:
  - ↓: サジェストリスト表示/次の候補へ
  - ↑: 前の候補へ
  - Enter: 候補選択/検索実行
- マウス操作:
  - ホバー: 候補ハイライト
  - クリック: 候補選択して検索実行
- デバウンス: 300ms

#### 2.2.6 OpenSearch Description Document（OSDD）
- `osddLink`フラグによる表示制御
- リンク: `/osdd`

---

### 2.3 検索結果画面機能

#### 2.3.1 検索実行
- **API**: `GET /api/v1/documents`
- **リクエストパラメータ**:
  - `q`: 検索クエリ
  - `start`: 開始位置（デフォルト: 0）
  - `num`: 表示件数（10/20/30/40/50/100、デフォルト: 設定値）
  - `sort`: ソート順
  - `lang`: 言語フィルタ（配列）
  - `fields.label`: ラベルフィルタ（配列）
  - `ex_q`: 除外クエリ（配列）
  - `facet.field`: ファセットフィールド
  - `facet.query`: ファセットクエリ
  - その他のパラメータ（geo検索、preference等）

#### 2.3.2 検索結果ヘッダー
- **現在の検索条件表示**（バッジ形式）:
  - ソート順（デフォルト: スコア）
  - 表示件数（デフォルト: 設定値）
  - 言語（デフォルト: すべて）
  - ラベル（`displayLabelTypeItems`フラグで制御）

- **人気ワード表示**
  - トップページと同様

- **関連クエリ表示**
  - `relatedQueries`配列
  - ラベル: `labels.search_related_queries`
  - クリック時: 該当クエリで再検索

- **関連コンテンツ表示**
  - `relatedContents`配列（HTML形式）

#### 2.3.3 検索結果ステータス
- **結果あり時**:
  - `labels.search_result_status`: 検索クエリ、総件数、表示範囲
  - `labels.search_result_time`: 実行時間
  - 総件数の関係性（`allRecordCountRelation`）:
    - `EQUAL_TO`: 正確な件数
    - その他: `labels.search_result_status_over`（以上）

- **部分結果警告**:
  - `partialResults`フラグが`true`の場合
  - メッセージ: `labels.process_time_is_exceeded`

- **類似文書検索時**:
  - `sdh`（similar document hash）パラメータ存在時
  - メッセージ: `labels.similar_doc_result_status`

#### 2.3.4 検索結果リスト
各ドキュメント項目に以下を表示：

- **タイトル**:
  - `doc.content_title`
  - リンク: `doc.url_link`
  - クリック時: `/go/`経由でトラッキング
    - パラメータ: `rt`, `docId`, `queryId`, `order`, `hash`

- **サムネイル**（`thumbnailSupport`フラグ）:
  - 遅延読み込み（lazy loading）
  - API: `/thumbnail/?docId={docId}&queryId={queryId}`
  - プレースホルダ: `/images/blank.png`
  - ローディング画像: `/images/loading.gif`
  - モバイルでは非表示（`d-none d-sm-flex`）

- **説明文**:
  - `doc.content_description`（HTMLエスケープ済み、ハイライトあり）

- **サイトパス**:
  - `doc.site_path`
  - URLコピー機能（`clipboardCopyIcon`フラグ）
    - アイコン: FontAwesome `far fa-copy`
    - クリック時: URLをクリップボードにコピー
    - 成功時: アイコン変更（`fas fa-check`）、2秒後に元に戻る

- **詳細情報（More）リンク**:
  - クリック時: 詳細情報を展開表示

- **詳細情報（Info）**:
  - **更新日時**: `doc.last_modified`（フォーマット: `yyyy-MM-dd HH:mm`）
    - なければ`doc.created`を表示
  - **ファイルサイズ**: `doc.content_length`（人間が読みやすい形式）
  - **クリック数**（`searchLogSupport`フラグ）: `doc.click_count`
  - **キャッシュリンク**: `doc.has_cache`が`true`の場合
    - リンク: `/cache/?docId={docId}{appendHighlightParams}`
  - **類似文書リンク**: `doc.similar_docs_count > 1`の場合
    - リンク: `/search?q={q}&ex_q={ex_q}&sdh={similar_docs_hash}&...`
    - 表示: 類似文書数（-1）
  - **お気に入り機能**（`favoriteSupport`フラグ）:
    - 未お気に入り: `far fa-star`アイコン
    - お気に入り済み: `fas fa-star`アイコン
    - クリック時: `POST /api/v1/documents/{docId}/favorite`
    - 成功時: アイコン切り替え（フェードアニメーション）
    - 初回読み込み時: `GET /api/v1/favorites?queryId={queryId}`でお気に入り状態取得

#### 2.3.5 ファセット（サイドバー）
- **ラベルファセット**:
  - `facetResponse.fieldList`の`label`フィールド
  - 表示: ラベル名と件数
  - クリック時: `ex_q=label:{value}`を追加して再検索

- **カスタムファセット**:
  - `facetQueryView`のリスト
  - タイトル: i18nキー（`labels.*`）
  - 各クエリの件数を表示
  - クリック時: `ex_q={query}`を追加して再検索

- **ファセットリセット**:
  - `ex_q`パラメータがある場合のみ表示
  - クリック時: `ex_q`を削除して再検索

#### 2.3.6 ページネーション
- **前ページボタン**（`existPrevPage`フラグ）:
  - リンク: `/search/prev?q={q}&pn={currentPageNumber}&num={pageSize}&...`

- **ページ番号リスト**:
  - `pageNumberList`配列
  - 現在ページ: `active`クラス
  - 現在ページから±2以外: モバイルで非表示（`d-none d-sm-inline-block`）
  - リンク: `/search/move?q={q}&pn={pageNumber}&num={pageSize}&...`

- **次ページボタン**（`existNextPage`フラグ）:
  - リンク: `/search/next?q={q}&pn={currentPageNumber}&num={pageSize}&...`

#### 2.3.7 結果なし表示
- メッセージ: `labels.did_not_match`
- 表示クエリ: `displayQuery`

#### 2.3.8 「トップに戻る」リンク
- ページ下部に表示
- ラベル: `labels.footer_back_to_top`

---

### 2.4 検索オプション機能

#### 2.4.1 表示件数（num）
- セレクトボックス
- 選択肢: 10, 20, 30, 40, 50, 100
- デフォルト: サーバー設定値
- セッション保存対応（`viewHelper.isUseSession()`）

#### 2.4.2 ソート順（sort）
- セレクトボックス
- 選択肢:
  - スコア降順（`score.desc`、デフォルト）
  - ファイル名昇順/降順（`filename.asc/desc`）
  - 作成日時昇順/降順（`created.asc/desc`）
  - ファイルサイズ昇順/降順（`content_length.asc/desc`）
  - 更新日時昇順/降順（`last_modified.asc/desc`）
  - クリック数昇順/降順（`click_count.asc/desc`、`searchLogSupport`フラグ）
  - お気に入り数昇順/降順（`favorite_count.asc/desc`、`favoriteSupport`フラグ）

#### 2.4.3 言語フィルタ（lang）
- 複数選択セレクトボックス
- 選択肢: `langItems`配列（動的）
- デフォルト: すべて

#### 2.4.4 ラベルフィルタ（fields.label）
- 複数選択セレクトボックス
- 選択肢: `labelTypeItems`配列（動的）
- 表示制御: `displayLabelTypeItems`フラグ
- デフォルト: すべて

#### 2.4.5 オプションクリアボタン
- クリック時: すべてのオプションをデフォルト値にリセット

---

### 2.5 詳細検索画面機能

#### 2.5.1 レイアウト
- フォーム形式の詳細検索条件入力画面
- ヘッダー・フッター共通

#### 2.5.2 詳細検索フィールド
- **必須ワード（as.q）**:
  - すべてのワードを含む検索
  - 複数ワード対応（配列）

- **フレーズ（as.epq）**:
  - 完全一致フレーズ検索
  - 複数フレーズ対応（配列）

- **いずれかのワード（as.oq）**:
  - OR検索
  - 複数ワード対応（配列）

- **除外ワード（as.nq）**:
  - NOT検索
  - 複数ワード対応（配列）

- **表示件数（num）**:
  - 検索オプションと同様

- **ソート順（sort）**:
  - 検索オプションと同様

- **言語（lang）**:
  - 検索オプションと同様

- **ラベル（fields.label）**:
  - 検索オプションと同様

- **タイムスタンプフィルタ（as.timestamp）**:
  - セレクトボックス
  - 選択肢:
    - すべて（デフォルト）
    - 過去1日（`[now-1d/d TO *]`）
    - 過去1週間（`[now-1w/d TO *]`）
    - 過去1ヶ月（`[now-1M/d TO *]`）
    - 過去1年（`[now-1y/d TO *]`）

- **ファイルタイプ（as.filetype）**:
  - セレクトボックス
  - 選択肢:
    - すべて（デフォルト）
    - HTML
    - PDF
    - Word
    - Excel
    - PowerPoint

- **検索対象（as.occt）**:
  - セレクトボックス
  - 選択肢:
    - すべて（デフォルト）
    - タイトル（`allintitle`）
    - URL（`allinurl`）

- **サイト内検索（as.sitesearch）**:
  - テキスト入力
  - 特定サイトに限定した検索

#### 2.5.3 検索ボタン
- 検索実行後、検索結果画面に遷移
- 詳細検索条件を`as.*`パラメータとしてクエリ化

---

### 2.6 ログイン画面機能

#### 2.6.1 レイアウト
- センタリングされたログインフォーム
- ロゴ表示（`/images/logo-top.png`）

#### 2.6.2 ログインフォーム
- **ユーザー名入力**:
  - プロパティ: `username`
  - プレースホルダ: `labels.login.placeholder_username`

- **パスワード入力**:
  - プロパティ: `password`
  - プレースホルダ: `labels.login.placeholder_password`
  - タイプ: `password`

- **ログインボタン**:
  - ラベル: `labels.login`
  - 送信先: サーバー側ログインエンドポイント（Fess本体）

#### 2.6.3 エラー・メッセージ表示
- ログイン失敗時のエラーメッセージ
- 情報メッセージ

---

### 2.7 ヘルプ画面機能

#### 2.7.1 レイアウト
- ヘッダー・フッター共通
- ヘルプコンテンツエリア

#### 2.7.2 ヘルプコンテンツ
- 動的にインクルードされるヘルプページ（`helpPage`変数）
- サーバー側で管理されるヘルプコンテンツを表示
- 「トップに戻る」リンク

---

## 3. 非機能要件

### 3.1 パフォーマンス
- 初回描画時間: 2秒以内
- APIレスポンス時間: 通常時1秒以内
- サジェスト応答時間: 300ms以内（デバウンス含む）
- 画像遅延読み込み実装

### 3.2 レスポンシブデザイン
- ブレークポイント: Bootstrap 5準拠
  - xs: < 576px
  - sm: >= 576px
  - md: >= 768px
  - lg: >= 992px
  - xl: >= 1200px
  - xxl: >= 1400px
- モバイルファースト設計

### 3.3 アクセシビリティ
- ARIA属性の適切な使用
- キーボードナビゲーション対応
- スクリーンリーダー対応
- コントラスト比: WCAG AA準拠

### 3.4 ブラウザ対応
- Chrome（最新版）
- Firefox（最新版）
- Safari（最新版）
- Edge（最新版）

### 3.5 セキュリティ
- XSS対策: 入力値のサニタイズ
- CSRF対策: 必要に応じてトークン実装
- HTTPS通信
- APIキー管理（環境変数）

### 3.6 SEO
- メタタグ設定
- 構造化データ
- サイトマップ
- robots.txt

---

## 4. API連携仕様

### 4.1 使用するFess API

#### 4.1.1 検索API
- **エンドポイント**: `GET /api/v1/documents`
- **用途**: 検索実行
- **レスポンスフィールド**:
  - `q`, `query_id`, `exec_time`, `query_time`, `page_size`, `page_number`, `record_count`, `page_count`, `highlight_params`, `next_page`, `prev_page`, `start_record_number`, `end_record_number`, `page_numbers`, `partial`, `search_query`, `requested_time`, `related_query`, `related_contents`, `data[]`

#### 4.1.2 ラベル一覧API
- **エンドポイント**: `GET /api/v1/labels`
- **用途**: ラベルフィルタ選択肢取得

#### 4.1.3 人気ワードAPI
- **エンドポイント**: `GET /api/v1/popular-words`
- **用途**: トップページ・検索結果ページの人気ワード表示

#### 4.1.4 サジェストAPI
- **エンドポイント**: `GET /api/v1/suggest-words`
- **用途**: 検索ボックスのオートコンプリート

#### 4.1.5 お気に入り一覧API
- **エンドポイント**: `GET /api/v1/favorites`
- **用途**: お気に入り状態の初期読み込み

#### 4.1.6 お気に入り設定API
- **エンドポイント**: `POST /api/v1/documents/{docId}/favorite`
- **用途**: お気に入りマーク設定

### 4.2 エラーハンドリング
- 400 Bad Request: クエリパラメータバリデーションエラー
- 401 Unauthorized: 認証エラー → ログイン画面へリダイレクト
- 404 Not Found: リソース未検出
- 500 Internal Server Error: サーバーエラー → エラーページ表示

---

## 5. データモデル

### 5.1 検索フォームデータ
```typescript
interface SearchFormData {
  q?: string;              // 検索クエリ
  num?: number;            // 表示件数
  sort?: string;           // ソート順
  lang?: string[];         // 言語フィルタ
  'fields.label'?: string[]; // ラベルフィルタ
  ex_q?: string[];         // 除外クエリ
  start?: number;          // 開始位置
  pn?: number;             // ページ番号
  sdh?: string;            // 類似文書ハッシュ
  // ファセット関連
  'facet.field'?: string[];
  'facet.query'?: string[];
  // Geo検索関連（オプション）
  'geo.location.point'?: string;
  'geo.location.distance'?: string;
}
```

### 5.2 詳細検索フォームデータ
```typescript
interface AdvancedSearchFormData extends SearchFormData {
  'as.q'?: string[];       // 必須ワード
  'as.epq'?: string[];     // フレーズ
  'as.oq'?: string[];      // いずれかのワード
  'as.nq'?: string[];      // 除外ワード
  'as.timestamp'?: string; // タイムスタンプフィルタ
  'as.filetype'?: string;  // ファイルタイプ
  'as.occt'?: string;      // 検索対象
  'as.sitesearch'?: string; // サイト内検索
}
```

### 5.3 検索結果データ
```typescript
interface SearchResult {
  q: string;
  query_id: string;
  exec_time: number;
  query_time: number;
  page_size: number;
  page_number: number;
  record_count: number;
  page_count: number;
  highlight_params: string;
  next_page: boolean;
  prev_page: boolean;
  start_record_number: number;
  end_record_number: number;
  page_numbers: string[];
  partial: boolean;
  search_query: string;
  requested_time: number;
  related_query?: string[];
  related_contents?: string[];
  data: DocumentItem[];
}

interface DocumentItem {
  filetype: string;
  title: string;
  content_title: string;
  digest: string;
  host: string;
  last_modified: string;
  content_length: string;
  timestamp: string;
  url_link: string;
  created: string;
  site_path: string;
  doc_id: string;
  url: string;
  content_description: string;
  site: string;
  boost: string;
  mimetype: string;
  // 追加フィールド（条件付き）
  click_count?: number;
  has_cache?: string;
  similar_docs_count?: number;
  similar_docs_hash?: string;
}
```

### 5.4 ユーザー情報
```typescript
interface UserInfo {
  username: string;
  editableUser: boolean;  // プロフィール編集可否
  adminUser: boolean;     // 管理者権限
}
```

---

## 6. 画面遷移図

```
[トップページ (index)]
  ├─ 検索実行 → [検索結果 (search)]
  ├─ 詳細検索リンク → [詳細検索 (advance)]
  ├─ ログインリンク → [ログイン (login)]
  └─ ヘルプリンク → [ヘルプ (help)]

[検索結果 (search)]
  ├─ 検索実行 → [検索結果 (search)]（再検索）
  ├─ ページネーション → [検索結果 (search)]
  ├─ ファセットクリック → [検索結果 (search)]
  ├─ 関連クエリクリック → [検索結果 (search)]
  ├─ ドキュメントリンククリック → 外部サイト（/go/経由）
  ├─ キャッシュリンク → [キャッシュページ (/cache)]
  └─ トップに戻る → [トップページ (index)]

[詳細検索 (advance)]
  └─ 検索実行 → [検索結果 (search)]

[ログイン (login)]
  └─ ログイン成功 → [トップページ (index)] or リダイレクト元

[ヘルプ (help)]
  └─ トップに戻る → [トップページ (index)]
```

---

## 7. 制約事項・前提条件

### 7.1 前提条件
- Fessサーバーが稼働していること
- OpenAPI v1が利用可能であること
- `/api/v1/*`エンドポイントにアクセス可能であること
- CORS設定が適切に行われていること

### 7.2 制約事項
- Fessサーバーの設定値（表示件数上限、ページネーション設定等）に依存
- ファセット設定はFess側の設定に依存
- 認証・認可機能はFess本体に依存（Next.js側は状態表示のみ）
- サーバー側レンダリング（SSR）を使用する場合、APIアクセスのネットワーク遅延を考慮
- キャッシュページ（`/cache`）はFess本体の機能を利用

### 7.3 将来的な拡張性
- 独自の認証機能追加の可能性
- お気に入り機能のローカルストレージ対応
- 検索履歴機能
- 高度なフィルタリング機能
- ダークモード対応
- PWA対応

---

## 8. テスト要件

### 8.1 単体テスト
- コンポーネント単位のテスト（Jest + React Testing Library）
- API連携ロジックのモックテスト
- フォームバリデーションテスト

### 8.2 統合テスト
- ページ遷移テスト
- API連携テスト（実際のFessサーバーまたはモックサーバー）

### 8.3 E2Eテスト
- Playwrightによるシナリオテスト
  - 検索フロー
  - ページネーション
  - フィルタリング
  - ログイン/ログアウト

### 8.4 パフォーマンステスト
- Lighthouseスコア: 90以上
- Core Web Vitals準拠

---

## 9. 運用要件

### 9.1 ログ
- エラーログ（APIエラー、クライアントエラー）
- アクセスログ（Next.jsサーバーログ）

### 9.2 モニタリング
- エラー監視（Sentry等）
- パフォーマンス監視（Vercel Analytics等）

### 9.3 デプロイ
- ビルド: `npm run build`
- 起動: `npm run start`
- 開発: `npm run dev`
- 環境変数:
  - `NEXT_PUBLIC_FESS_API_URL`: Fess API URL
  - `NEXT_PUBLIC_BASE_URL`: Next.jsアプリのベースURL

---

## 10. 付録

### 10.1 参考資料
- Fess公式ドキュメント: https://fess.codelibs.org/
- Fess OpenAPI仕様: `src/main/config/openapi/openapi-user.yaml`
- Next.js公式ドキュメント: https://nextjs.org/docs

### 10.2 用語集
- **Fess**: 全文検索システム
- **OpenSearch**: 検索エンジン（Elasticsearchフォーク）
- **ファセット**: 検索結果の絞り込み機能
- **サジェスト**: 検索候補の自動表示
- **OSDD**: OpenSearch Description Document
- **i18n**: 国際化（Internationalization）
