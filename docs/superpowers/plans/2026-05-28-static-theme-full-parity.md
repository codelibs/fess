# Static Theme Full Parity with JSP Search UI — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use `superpowers:subagent-driven-development` (recommended) or `superpowers:executing-plans` to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Bootstrap-based Static theme (`src/main/webapp/themes/bootstrap/`) を、JSP 版検索 UI と機能・表示パリティを持つ完全な再現に仕上げる。

**Architecture:** Static theme は SPA。サーバ側 (`/api/v2/*`) は最小拡張のみ行い、不足する画面（プロファイル / ヘルプ / エラー）は同一 SPA shell の追加ビューとして実装する。ThemeViewAction はエラーモードを追加する。i18n は JSON 拡張＋既存 `fess_label_*.properties` からのキー反映で対応。

**Tech Stack:** Vanilla JS (ES module), Bootstrap 5, Java 21 + LastaFlute, OpenSearch / DBFlute, JUnit 4 + UTFlute.

---

## Scope Overview

| Phase | テーマ | 主成果物 |
|-------|------|---------|
| 0 | 前提整備 | CSP/CDN 方針、OSDD リンク、popstate/SPA router、既存 chat.js/suggest 機能の現状確認と必要修正 |
| 1 | サーバ側 API 拡張 | `/api/v2/ui/config` 拡張、`/api/v2/related` 新設、search レスポンスに不足フィールド追加、`/error` 等の SPA 委譲 |
| 2 | 検索結果カード機能再現 | サムネイル、サイズ、クリック数、類似結果、キャッシュ条件付与、ハイライト、execTime |
| 3 | 検索オプション完全再現 | ソート 14 種、件数選択、言語選択、ラベル選択、suggest 強化 |
| 4 | ファセット完全再現 | label / timestamp / contentLength / filetype / geo の各範囲・選択肢、active chips |
| 5 | ナビゲーション・トップ・フッター | Admin/Profile メニュー、EOL/dev 警告、ポピュラーワード（中央レイアウト）、Back to Top |
| 6 | プロファイル画面 | `/profile` ルート、パスワード変更フォーム |
| 7 | ヘルプ画面 | `/help` ルート、言語別ヘルプコンテンツ |
| 8 | エラー画面 | `/error/*` ルート (404/400/500/503/redirect)、ThemeViewAction エラーモード |
| 9 | i18n 全面拡張 | en/ja を基準に必要キーを追加、他 6 言語 (de/es/fr/ko/pt-BR/zh-CN) 対応 |
| 10 | 検証 | JSP との機能比較、回帰テスト、視覚確認、アクセシビリティ監査 |
| 11 | Advanced Search | `/advance` ルート、クエリビルダ UI、複合条件検索 |
| 12 | Chat 機能パリティ確認 | 既存 chat.js を JSP `chat.jsp` と比較し、不足機能を補完 |

---

## File Structure

新規・変更ファイルの責務マップ:

### 既存修正
- `src/main/webapp/themes/bootstrap/index.html` — SPA shell。サブビュー (profile/help/error) のためのテンプレート追加。
- `src/main/webapp/themes/bootstrap/assets/search.js` — カード描画拡張、オプション UI 拡張、関連 UI、ファセット拡張。
- `src/main/webapp/themes/bootstrap/assets/api.js` — 新エンドポイント (`/api/v2/related-queries`, `/api/v2/related-content`) のラッパ、`formatFileSize` ヘルパ追加。
- `src/main/webapp/themes/bootstrap/assets/app.js` — ルーティング処理。`window.location.pathname` を見て検索 / プロファイル / ヘルプ / エラーを切替。
- `src/main/webapp/themes/bootstrap/assets/auth.js` — Admin / Profile / Logout ドロップダウンメニュー描画。
- `src/main/webapp/themes/bootstrap/assets/styles.css` — サムネイル、サイドバー、エラー画面、Back to Top ボタン。
- `src/main/webapp/themes/bootstrap/i18n/messages.en.json` — キー大幅追加。
- `src/main/webapp/themes/bootstrap/i18n/messages.ja.json` — 同上。
- `src/main/java/org/codelibs/fess/api/v2/SearchApiV2Manager.java`（既存）— 新ハンドラ登録。
- `src/main/java/org/codelibs/fess/api/v2/handler/UiConfigHandler.java`（既存と推定）— 設定フィールド追加。
- `src/main/java/org/codelibs/fess/api/v2/handler/SearchHandler.java`（既存と推定）— レスポンスフィールド追加。
- `src/main/java/org/codelibs/fess/app/web/theme/ThemeViewAction.java` — `/profile` `/help` `/error/*` SPA ルート時にも index.html を返却、エラー時はエラーモードヘッダ付与。
- `src/main/java/org/codelibs/fess/filter/StaticThemeFilter.java` — `/profile`, `/help`, `/error/*` のフォワード対応（現状はパススルー）。
- `src/test/java/org/codelibs/fess/filter/StaticThemeFilterTest.java` — 新ルートのテスト追加。

### 新規ファイル
- `src/main/webapp/themes/bootstrap/assets/profile.js` — プロファイル画面ロジック。
- `src/main/webapp/themes/bootstrap/assets/help.js` — ヘルプ画面ロジック（マークダウン展開 or JSON コンテンツ）。
- `src/main/webapp/themes/bootstrap/assets/error.js` — エラー画面ロジック。
- `src/main/webapp/themes/bootstrap/assets/router.js` — クライアントサイドルーティングディスパッチャ。
- `src/main/webapp/themes/bootstrap/assets/format.js` — `formatFileSize`, `formatDate`, `escapeHtml` 共通ユーティリティ。
- `src/main/webapp/themes/bootstrap/help/en.json` — 英語ヘルプコンテンツ（セクション化）。
- `src/main/webapp/themes/bootstrap/help/ja.json` — 日本語ヘルプコンテンツ。
- `src/main/java/org/codelibs/fess/api/v2/handler/RelatedQueriesHandler.java` — 関連クエリ API。
- `src/main/java/org/codelibs/fess/api/v2/handler/RelatedContentHandler.java` — 関連コンテンツ API。
- `src/test/java/org/codelibs/fess/api/v2/handler/RelatedQueriesHandlerTest.java`
- `src/test/java/org/codelibs/fess/api/v2/handler/RelatedContentHandlerTest.java`

---

## Cross-Cutting Conventions

- Java は既存パターンに従う: `@Resource` 注入、`ComponentUtil.getXxxHelper()`、Apache License 2.0 ヘッダ。
- ファイル変更前後で `mvn formatter:format && mvn license:format` を実行（コミット前に必ず）。
- 各タスクの末尾でユニットテスト or 統合テストを追加。フロント JS は手動検証 + 既存スタブテスト拡張のみ（プロダクションでは Karma 等を導入していないため）。
- フロントコミット粒度: 1 画面 / 1 機能 = 1 コミット。バックエンド: ハンドラ 1 つ = 1 コミット。
- ブランチ: 現在の `feature/static-theme` を継続使用。

### XSS 対策の原則（必須）

`innerHTML` は **原則禁止**。理由: テーマ JSON はソース管理下で信頼できるが、検索結果や API レスポンス、URL クエリ等は信頼できない。誤って混在させると XSS が発生する。

許可される `innerHTML` 利用:
1. **ハイライト済みスニペット**: `escapeHtml()` で全エスケープ後、`<strong>`/`<em>` のみ復元するホワイトリスト方式 (Task 2.6 の `renderHighlightedSnippet`)。
2. **静的ヘルプ JSON 内 HTML**: 開発者が直接管理するファイル (`themes/bootstrap/help/*.json`) で、コミット時にレビュー済。それでも DOMPurify 相当の最小サニタイザを通す（Task 7.2 参照）。

その他はすべて `document.createElement()` / `textContent` / `setAttribute()` で構築する。とくにユーザー入力やサーバから取得した値の挿入は textContent 一択。

i18n テキスト (`t(key)`) も翻訳ファイル汚染リスクを避けるため `textContent` で扱う。

### 信頼境界マップ

| データソース | 信頼レベル | 挿入方法 |
|------------|---------|--------|
| `fetch("/api/v2/*")` レスポンス | **untrusted** | textContent 必須 |
| `location.pathname`, `URL.searchParams` | **untrusted** | textContent 必須 |
| `themes/bootstrap/i18n/messages.*.json` | trusted (source-controlled) | textContent 推奨 |
| `themes/bootstrap/help/*.json` の `html` | trusted (source-controlled) | サニタイザ経由で innerHTML 可 |
| `escapeHtml()` 後の文字列 | safe | innerHTML 可（ホワイトリストタグ復元時のみ） |

---

## Phase 0: 前提整備

### Task 0.1: CSP と外部リソース方針の確定

**Files:**
- Read: `src/main/java/org/codelibs/fess/app/web/theme/ThemeViewAction.java`（既存 CSP ヘッダ確認）
- Modify: `src/main/webapp/themes/bootstrap/index.html`
- Modify: `src/main/java/org/codelibs/fess/app/web/theme/ThemeViewAction.java`（必要なら CSP 拡張）

**方針:**
- Bootstrap 5 / Font Awesome は **CDN 非依存** で同梱（Fess 既存 `/css/bootstrap.min.css`, `/css/font-awesome.min.css` を参照）。
- 既存 ThemeViewAction の CSP `default-src 'self'; script-src 'self'; style-src 'self' 'unsafe-inline';` のままで動作することを確認。
- 例外: フォントが別オリジンの場合のみ `font-src` を限定的に拡張。

- [ ] **Step 1: 現状 index.html の `<link>` / `<script>` 参照を列挙**
- [ ] **Step 2: 全リソースが Fess 自身配下 (`/css/...`, `/js/...`, `/images/...`, `/themes/...`) であることを確認**
- [ ] **Step 3: 違反があれば self-host に切替**
- [ ] **Step 4: 確認のみで変更不要なら本タスクは Skip しコミットなし**
- [ ] **Step 5: 変更があった場合のみコミット** `chore(theme): inline external resources to honor strict CSP`

---

### Task 0.2: OSDD リンクの追加

**Files:**
- Modify: `src/main/webapp/themes/bootstrap/index.html`

**仕様:**
- `<head>` に OpenSearch Description Document リンクを追加:

```html
<link rel="search" type="application/opensearchdescription+xml"
      title="Fess" href="/osdd">
```

- [ ] **Step 1: `<head>` の `<meta>` ブロックの後に上記 `<link>` を追加**
- [ ] **Step 2: ブラウザのアドレスバーから検索登録できるか確認（任意）**
- [ ] **Step 3: コミット** `feat(theme): expose OpenSearch description link from SPA shell`

---

### Task 0.3: SPA ルータと popstate（戻るボタン）対応

**Files:**
- Create: `src/main/webapp/themes/bootstrap/assets/router.js`
- Modify: `src/main/webapp/themes/bootstrap/assets/app.js`

**仕様:**
- パスとビュー切り替えを router.js に集約。
- `history.pushState()` で SPA 内リンク遷移（外部リンクは通常遷移）。
- `popstate` でルート再描画。
- ルートテーブル:
  ```
  "/", "/search", "/index" → results view
  "/advance"               → advance view
  "/help"                  → help view
  "/profile"               → profile view
  "/error/...", "/error"   → error view
  "/chat"                  → chat view（既存 chat.js 経由）
  その他                   → 404 として error view
  ```

**実装スケッチ:**

```javascript
// SPDX-License-Identifier: Apache-2.0
const VIEWS = ["home-view", "results-view", "advance-view",
               "help-view", "profile-view", "error-view", "chat-view"];

function hideAll() {
  for (const id of VIEWS) {
    const el = document.getElementById(id);
    if (el) el.hidden = true;
  }
}

function show(id) {
  const el = document.getElementById(id);
  if (el) el.hidden = false;
}

const HANDLERS = new Map();

export function register(predicate, handler) {
  HANDLERS.set(predicate, handler);
}

export function navigate(path, opts = {}) {
  if (opts.replace) history.replaceState(null, "", path);
  else history.pushState(null, "", path);
  dispatch();
}

export function dispatch() {
  hideAll();
  const path = location.pathname;
  for (const [predicate, handler] of HANDLERS) {
    if (predicate(path)) {
      handler(path);
      return;
    }
  }
  // fallback
  show("error-view");
}

export function attach() {
  window.addEventListener("popstate", dispatch);
  // delegate internal link clicks
  document.addEventListener("click", (e) => {
    const a = e.target.closest?.("a[data-spa]");
    if (!a) return;
    const href = a.getAttribute("href");
    if (!href || href.startsWith("http")) return;
    e.preventDefault();
    navigate(href);
  });
}
```

**app.js に統合:**

```javascript
import * as router from "./router.js";
import * as search from "./search.js";
import * as profile from "./profile.js";
import * as help from "./help.js";
import * as errorView from "./error.js";
import * as advance from "./advance.js";  // Phase 11
import * as chat from "./chat.js";

router.register((p) => /^\/(search|index)?\/?$/.test(p) || p === "/", () => {
  document.getElementById("results-view").hidden = false;
  search.maybeRun();
});
router.register((p) => p === "/advance",         () => { document.getElementById("advance-view").hidden = false; advance.attach(); });
router.register((p) => p === "/help",            () => { document.getElementById("help-view").hidden = false; help.attach(); });
router.register((p) => p === "/profile",         () => { document.getElementById("profile-view").hidden = false; profile.attach(); });
router.register((p) => p.startsWith("/error"),   () => { document.getElementById("error-view").hidden = false; errorView.attach(); });
router.register((p) => p === "/chat",            () => { document.getElementById("chat-view").hidden = false; chat.attach(); });

async function bootstrap() {
  await api.init();
  await i18n.init();
  auth.attach();
  router.attach();
  router.dispatch();
}
bootstrap();
```

- [ ] **Step 1: router.js を新規作成**
- [ ] **Step 2: app.js を上記方針で書き換え**
- [ ] **Step 3: 既存リンクに必要に応じて `data-spa` 属性を付与**
- [ ] **Step 4: 手動検証 — 戻るボタンで前のビューに戻れる**
- [ ] **Step 5: コミット** `feat(theme): introduce client-side router with popstate handling`

---

### Task 0.4: 既存 chat.js / search.js / auth.js / api.js の API シグネチャ凍結

**目的:** 既存モジュールの公開関数を一覧化し、Phase 1 以降の変更がそれを壊さないようにする。

- [ ] **Step 1: 各モジュールを読み、公開関数のリストを `docs/superpowers/plans/static-theme-public-api.md` に書き出す**
- [ ] **Step 2: その API がパリティ達成に十分か評価。不足があれば Phase 11/12 に詳細タスクとして起こす**

このタスクはドキュメント化のみで、コードは触らない。

- [ ] **Step 3: コミット** `docs(theme): record public API contract of static theme modules`

---

### Phase 0 完了チェックポイント

- [ ] CSP 違反なし
- [ ] OSDD リンク表示
- [ ] popstate で戻るボタン動作
- [ ] 公開 API ドキュメント化

---

## Phase 1: サーバ側 API 拡張

### Task 1.1: UiConfigHandler に画面表示用フラグと選択肢を追加

**Files:**
- Modify: `src/main/java/org/codelibs/fess/api/v2/handler/UiConfigHandler.java`
- Modify: `src/test/java/org/codelibs/fess/api/v2/handler/UiConfigHandlerTest.java`

追加するフィールド:

```
features:
  eoled                : FessConfig.isEoled()  (※ helper 経由か existing util を要確認)
  development_mode     : FessConfig.isDevelopmentMode()
  chat_enabled         : FessConfig.isChatEnabled() (既存)
  search_log_enabled   : FessConfig.isSearchLog() (※ click_count 表示制御)
  thumbnail_enabled    : FessConfig.getThumbnailHtmlImageThumbnail() 等
  popular_word_enabled : FessConfig.isWebApiPopularWord() (既存に統合)
  display_label_type   : ラベル候補が表示可能か (labelTypeItems が空でないか)

sort_options:
  - { value: "", label_key: "labels.search_result_sort_score_desc" }
  - { value: "score.desc", label_key: "..." }
  - { value: "filename.asc", ... }
  - { value: "filename.desc", ... }
  - { value: "created.asc", ... }
  - { value: "created.desc", ... }
  - { value: "content_length.asc", ... }
  - { value: "content_length.desc", ... }
  - { value: "last_modified.asc", ... }
  - { value: "last_modified.desc", ... }
  - { value: "click_count.asc", ... }     # search_log_enabled 時のみ
  - { value: "click_count.desc", ... }    # search_log_enabled 時のみ
  - { value: "favorite_count.asc", ... }  # user_favorite 時のみ
  - { value: "favorite_count.desc", ... } # user_favorite 時のみ

num_options:
  - 10, 20, 30, 40, 50, 100  (page_size_max を超えない範囲)

lang_options:
  - SystemHelper.getSupportedLanguages() からマッピング
  - 各エントリ: { value: "ja", label_key: "labels.lang_ja" }

label_options:
  - LabelTypeHelper.getLabelTypeItemsAsList() からマッピング
  - 各エントリ: { value: "id", name: "表示名" }
```

- [ ] **Step 1: 既存 UiConfigHandler.java を読み、レスポンス構造を把握する**

実行: `read src/main/java/org/codelibs/fess/api/v2/handler/UiConfigHandler.java`
理由: 既存 features ブロックの作り方と JSON 構築ヘルパに合わせるため。

- [ ] **Step 2: 失敗するテストを書く**

実行: `src/test/java/org/codelibs/fess/api/v2/handler/UiConfigHandlerTest.java` に以下のテストを追加。

```java
public void test_responseIncludesSortOptions() {
    Map<String, Object> resp = invokeHandler();
    @SuppressWarnings("unchecked")
    List<Map<String, Object>> sortOptions = (List<Map<String, Object>>) resp.get("sort_options");
    assertNotNull("sort_options must be present", sortOptions);
    assertTrue("must include score sort", sortOptions.stream()
        .anyMatch(o -> "".equals(o.get("value"))));
    assertTrue("must include filename.asc", sortOptions.stream()
        .anyMatch(o -> "filename.asc".equals(o.get("value"))));
}

public void test_responseIncludesNumOptions() {
    Map<String, Object> resp = invokeHandler();
    @SuppressWarnings("unchecked")
    List<Integer> nums = (List<Integer>) resp.get("num_options");
    assertTrue(nums.contains(10) && nums.contains(20) && nums.contains(50));
}

public void test_responseIncludesLangOptions() {
    Map<String, Object> resp = invokeHandler();
    assertNotNull(resp.get("lang_options"));
}

public void test_responseIncludesLabelOptions() {
    Map<String, Object> resp = invokeHandler();
    assertNotNull(resp.get("label_options"));
}

public void test_responseIncludesFeatureFlags() {
    Map<String, Object> resp = invokeHandler();
    Map<String, Object> features = (Map<String, Object>) resp.get("features");
    assertTrue(features.containsKey("eoled"));
    assertTrue(features.containsKey("development_mode"));
    assertTrue(features.containsKey("search_log_enabled"));
    assertTrue(features.containsKey("thumbnail_enabled"));
    assertTrue(features.containsKey("display_label_type"));
}
```

- [ ] **Step 3: テスト実行で失敗を確認**

実行: `mvn test -Dtest=UiConfigHandlerTest`
期待: 上記新テストすべて FAIL（フィールド未追加）。

- [ ] **Step 4: ハンドラに追加実装**

UiConfigHandler 内で:
- `LabelTypeHelper` を `ComponentUtil.getLabelTypeHelper()` で取得し、`getLabelTypeItemsAsList()` の結果を配列化。
- `FessConfig` から sort options を 14 種列挙し、`search_log_enabled` / `user_favorite` 条件で click_count / favorite_count を含めるかを判断。
- `getSupportedLanguagesAsArray()` から言語選択肢を構築。
- `isEoled()`, `isDevelopmentMode()`, `isSearchLog()`, thumbnail 関連を features に追加。

- [ ] **Step 5: テスト再実行**

実行: `mvn test -Dtest=UiConfigHandlerTest`
期待: 全 PASS。

- [ ] **Step 6: フォーマットとライセンス**

実行:
```
mvn formatter:format -pl .
mvn license:format -pl .
```

- [ ] **Step 7: コミット**

```
git add src/main/java/org/codelibs/fess/api/v2/handler/UiConfigHandler.java \
        src/test/java/org/codelibs/fess/api/v2/handler/UiConfigHandlerTest.java
git commit -m "feat(api/v2): expose sort/num/lang/label options and additional flags on ui/config"
```

---

### Task 1.2: SearchHandler レスポンスにドキュメント追加フィールドを露出

**Files:**
- Modify: `src/main/java/org/codelibs/fess/api/v2/handler/SearchHandler.java`
- Modify: 検索結果 doc マッピング箇所（`createDocumentItems` 相当メソッド）
- Modify: `src/test/java/org/codelibs/fess/api/v2/handler/SearchHandlerTest.java`

追加するドキュメントフィールド:
- `thumbnail` — `ThumbnailManager` で URL が存在するか
- `similar_docs_count` — クラスタリング有効時の SDH カウント
- `host` — 既存（確認）
- `mimetype` — ファイルタイプアイコン表示用
- `filetype` — JSP の `filetype` ファセット表示と整合

- [ ] **Step 1: 失敗するテストを書く**

`SearchHandlerTest`（既存 or 新規）に:

```java
public void test_searchResponseExposesThumbnailAndMimetypeAndFiletype() throws Exception {
    Map<String, Object> resp = doSearch("test");
    @SuppressWarnings("unchecked")
    List<Map<String, Object>> data = (List<Map<String, Object>>) resp.get("data");
    if (!data.isEmpty()) {
        Map<String, Object> first = data.get(0);
        assertTrue(first.containsKey("thumbnail"));
        assertTrue(first.containsKey("mimetype"));
        assertTrue(first.containsKey("filetype"));
        assertTrue(first.containsKey("similar_docs_count"));
    }
}
```

- [ ] **Step 2: テスト実行で失敗を確認**

実行: `mvn test -Dtest=SearchHandlerTest`
期待: フィールド欠落で FAIL。

- [ ] **Step 3: SearchHandler の doc→Map 変換に欠落フィールドを追加**

既存の `JSP` 用に `SearchAction` 側で行っている処理（`getDocumentItems`、`SDH`、`thumbnail` URL 生成）を参考に追加。

- [ ] **Step 4: テスト再実行 → PASS 確認**

- [ ] **Step 5: フォーマット & ライセンス**

- [ ] **Step 6: コミット**

```
git commit -m "feat(api/v2): include thumbnail/mimetype/filetype/similar_docs_count in search response"
```

---

### Task 1.3: 関連クエリ API ハンドラ新設

**Files:**
- Create: `src/main/java/org/codelibs/fess/api/v2/handler/RelatedQueriesHandler.java`
- Modify: `src/main/java/org/codelibs/fess/api/v2/SearchApiV2Manager.java`（ルーティング登録）
- Create: `src/test/java/org/codelibs/fess/api/v2/handler/RelatedQueriesHandlerTest.java`

仕様:
- `GET /api/v2/related-queries?q=<term>` で `RelatedQueryHelper.getRelatedQueries(term)` の結果を返す。
- レスポンス:
  ```json
  { "response": { "status": 0, "queries": ["関連語1", "関連語2", ...] } }
  ```

- [ ] **Step 1: 既存のハンドラサンプル（例: FavoriteGetHandler）を読み、構造を模倣**

- [ ] **Step 2: 失敗するテストを書く**

```java
public void test_returnsRelatedQueriesForTerm() throws Exception {
    Map<String, Object> resp = invokeHandler(Map.of("q", "test"));
    assertEquals(Integer.valueOf(0), resp.get("status"));
    assertNotNull(resp.get("queries"));
}

public void test_emptyTermReturnsEmptyList() throws Exception {
    Map<String, Object> resp = invokeHandler(Map.of("q", ""));
    assertEquals(Integer.valueOf(0), resp.get("status"));
    List<?> queries = (List<?>) resp.get("queries");
    assertTrue(queries.isEmpty());
}
```

- [ ] **Step 3: テスト実行で失敗を確認**

- [ ] **Step 4: RelatedQueriesHandler 実装**

```java
public class RelatedQueriesHandler implements ApiV2Handler {
    @Override
    public void handle(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        final String query = StringUtil.defaultString(req.getParameter("q"), "");
        final List<String> result;
        if (query.isEmpty()) {
            result = Collections.emptyList();
        } else {
            final RelatedQueryHelper helper = ComponentUtil.getRelatedQueryHelper();
            result = Arrays.asList(helper.getRelatedQueries(query, null));
        }
        ApiV2ResponseWriter.write(resp, Map.of("status", 0, "queries", result));
    }
}
```

- [ ] **Step 5: ルーティング登録**

`SearchApiV2Manager` の `registerHandlers()` 内に:

```java
register("GET", "/related-queries", new RelatedQueriesHandler());
```

- [ ] **Step 6: テスト PASS 確認**

- [ ] **Step 7: コミット**

```
git commit -m "feat(api/v2): add /related-queries endpoint"
```

---

### Task 1.4: 関連コンテンツ API ハンドラ新設

**Files:**
- Create: `src/main/java/org/codelibs/fess/api/v2/handler/RelatedContentHandler.java`
- Modify: `src/main/java/org/codelibs/fess/api/v2/SearchApiV2Manager.java`
- Create: `src/test/java/org/codelibs/fess/api/v2/handler/RelatedContentHandlerTest.java`

仕様:
- `GET /api/v2/related-content?q=<term>` で `RelatedContentHelper.getRelatedContent(term)` を返す。
- レスポンス:
  ```json
  { "response": { "status": 0, "content": "HTML or text", "content_type": "html" } }
  ```
- 安全性: 取得されたコンテンツは HTML を含むため、`content_type: "html"` を明示。フロント側でサニタイズした上で挿入する。

実装ステップは Task 1.3 と同型なので省略。テスト → 実装 → 登録 → コミットの流れ。

- [ ] **Step 1: テスト作成**
- [ ] **Step 2: ハンドラ実装**
- [ ] **Step 3: ルーティング登録**
- [ ] **Step 4: テスト PASS**
- [ ] **Step 5: コミット** `feat(api/v2): add /related-content endpoint`

---

### Task 1.5: StaticThemeFilter から `/error`, `/help`, `/profile` を pass-through から除外（SPA 委譲）

**Files:**
- Modify: `src/main/java/org/codelibs/fess/filter/StaticThemeFilter.java`
- Modify: `src/test/java/org/codelibs/fess/filter/StaticThemeFilterTest.java`

**現状（src/main/java/org/codelibs/fess/filter/StaticThemeFilter.java:92-104）:**
`PASS_THROUGH_PREFIXES` に `/login`, `/error`, `/profile` が含まれている。コメントに `/error` の理由が記載されている:
> Error pages (`/error`) — without this the JSP error redirect (`WEB-INF/view/error/redirect.jsp`) issues `/error/notfound/?url=...` which the filter would then forward back to the theme, looping or hiding the error message.

**変更方針:**
- `/profile`, `/help`（現在は未定義 = SPA 委譲対象）、`/error` を **Static theme 有効時は SPA に委ねる**。
- `/login` は JSP ログインフォーム (LastaFlute) に依存するため引き続き pass-through 維持。
- 既存コメントが懸念した「ループ」の問題は次の方法で解決:
  - JSP の `redirect.jsp` は `/error/notfound?url=...` 等の URL を生成するが、Static theme 有効時の Fess は **エラーレスポンスを直接 `WEB-INF/view/error/*.jsp` ではなく Static theme の index.html で返す**よう Task 8.1 の `ThemeViewAction.serveError()` 経路を導入する。
  - フィルタは `/error/*` を INDEX モードでフォワード（クエリ文字列 `?url=...` は SPA 側が URL から読み取る）。
  - ループ防止: フォワード先 `THEME_VIEW_PATH (/theme/view/)` は引き続き pass-through 対象に残す（既存挙動）。

**変更点:**
- `PASS_THROUGH_PREFIXES` から `/error`, `/profile` を **削除**。
- `/help` はもともと pass-through に含まれていないため変更不要。
- `/login` は変更しない（JSP ログイン互換のため）。

- [ ] **Step 1: 既存 StaticThemeFilterTest を読み、テストパターンを把握**

実行: `read src/test/java/org/codelibs/fess/filter/StaticThemeFilterTest.java`

- [ ] **Step 2: 失敗するテストを追加**

```java
public void test_forwardsIndexForErrorPath() {
    final StubRequest req = newGetRequest("/error/notFound");
    filter.doFilter(req, response, chain);
    assertEquals("INDEX", req.getAttribute(ThemeViewAction.REQ_ATTR_MODE));
    assertTrue("must be forwarded, not pass-through", req.forwardedTo("/theme/view/"));
}

public void test_forwardsIndexForErrorPathWithQueryString() {
    final StubRequest req = newGetRequest("/error/notFound");
    req.setQueryString("url=https%3A%2F%2Fexample.com%2Fmissing");
    filter.doFilter(req, response, chain);
    assertEquals("INDEX", req.getAttribute(ThemeViewAction.REQ_ATTR_MODE));
}

public void test_forwardsIndexForProfilePath() {
    final StubRequest req = newGetRequest("/profile");
    filter.doFilter(req, response, chain);
    assertEquals("INDEX", req.getAttribute(ThemeViewAction.REQ_ATTR_MODE));
}

public void test_passesThroughForLoginPath() {
    // /login still pass-through (JSP form required)
    final StubRequest req = newGetRequest("/login");
    filter.doFilter(req, response, chain);
    assertNull(req.getAttribute(ThemeViewAction.REQ_ATTR_MODE));
    assertTrue(chain.invoked());
}

public void test_passesThroughForThemeViewSelfPath() {
    // /theme/view/ remains pass-through to avoid recursion
    final StubRequest req = newGetRequest("/theme/view/");
    filter.doFilter(req, response, chain);
    assertNull(req.getAttribute(ThemeViewAction.REQ_ATTR_MODE));
}
```

- [ ] **Step 3: テスト実行で失敗を確認**

実行: `mvn test -Dtest=StaticThemeFilterTest`
期待: 新 4 テストが FAIL（特に `/error` と `/profile` は pass-through されるはず）。

- [ ] **Step 4: フィルタを変更**

`PASS_THROUGH_PREFIXES` から `/error` と `/profile` を削除。コメントも更新:

```java
/**
 * URI prefixes that bypass the filter even when a static theme is active.
 *
 * <p>This list is intentionally minimal. When a static theme is active, all
 * UI paths — including {@code /search}, {@code /chat}, {@code /index},
 * {@code /advance}, {@code /help}, {@code /profile}, and {@code /error/*}
 * — are forwarded to {@link ThemeViewAction} in INDEX mode and rendered by
 * the SPA shell. Only the following genuinely need to bypass:
 *
 * <ul>
 *   <li>{@code /admin}, {@code /api}, {@code /sso}, {@code /logout} —
 *       infrastructure the SPA does not own.</li>
 *   <li>{@code /login} — the JSP login form is still the canonical entry
 *       point (the SPA can use the modal login but a hard reload to
 *       /login must continue to work).</li>
 *   <li>Legacy theme assets ({@code /css}, {@code /js}, {@code /images}).</li>
 *   <li>Crawler/system endpoints ({@code /go}, {@code /thumbnail},
 *       {@code /osdd}, {@code /cache}, {@code /favicon.ico},
 *       {@code /robots.txt}, {@code /sitemap.xml},
 *       {@code /manifest.webmanifest}, {@code /.well-known}).</li>
 *   <li>{@link #THEME_VIEW_PATH} itself, to prevent recursion.</li>
 * </ul>
 *
 * <p><b>About {@code /error}:</b> error pages used to bypass to allow the
 * JSP error redirect (`WEB-INF/view/error/redirect.jsp`) to render
 * {@code WEB-INF/view/error/*.jsp}. With a static theme active, error
 * rendering is delegated to the SPA via {@link ThemeViewAction#serveError},
 * which sets the correct HTTP status, exposes the requested URL via a
 * meta tag, and lets the SPA render the localized error UI. Loop prevention
 * relies on {@link #THEME_VIEW_PATH} remaining in the bypass list above.
 */
private static final List<String> PASS_THROUGH_PREFIXES = List.of(
        "/admin/", "/admin",
        "/api/",
        "/login",
        "/css/", "/js/", "/images/",
        "/go", "/thumbnail", "/osdd",
        "/sso/", "/logout", "/cache",
        "/favicon.ico",
        "/robots.txt",
        "/sitemap.xml",
        "/manifest.webmanifest",
        "/.well-known",
        THEME_VIEW_PATH);
```

- [ ] **Step 5: テスト PASS 確認**

実行: `mvn test -Dtest=StaticThemeFilterTest`
期待: 全 PASS（既存 + 新規）。

- [ ] **Step 6: フォーマット & ライセンス**

実行:
```
mvn formatter:format -pl .
mvn license:format -pl .
```

- [ ] **Step 7: コミット**

```
git add src/main/java/org/codelibs/fess/filter/StaticThemeFilter.java \
        src/test/java/org/codelibs/fess/filter/StaticThemeFilterTest.java
git commit -m "feat(theme): delegate /error and /profile to static theme SPA when active"
```

---

### Phase 1 完了チェックポイント

- [ ] 全テスト実行: `mvn test`
- [ ] `mvn formatter:format && mvn license:format`
- [ ] Phase 1 のすべてのタスクがコミット済み

---

## Phase 2: 検索結果カード機能再現

### Task 2.1: 共通フォーマットユーティリティモジュールの新設

**Files:**
- Create: `src/main/webapp/themes/bootstrap/assets/format.js`
- Modify: `src/main/webapp/themes/bootstrap/index.html`（script 追加）

`format.js` の内容:

```javascript
// SPDX-License-Identifier: Apache-2.0
const UNITS = ["B", "KB", "MB", "GB", "TB", "PB"];

export function formatFileSize(bytes) {
  if (bytes === null || bytes === undefined || bytes === "" || Number.isNaN(Number(bytes))) {
    return "";
  }
  let n = Number(bytes);
  if (n < 0) return "";
  let i = 0;
  while (n >= 1024 && i < UNITS.length - 1) {
    n /= 1024;
    i++;
  }
  return `${n.toFixed(i === 0 ? 0 : 1)} ${UNITS[i]}`;
}

export function formatDate(isoString) {
  if (!isoString) return "";
  const d = new Date(isoString);
  if (Number.isNaN(d.getTime())) return "";
  const pad = (x) => String(x).padStart(2, "0");
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ` +
         `${pad(d.getHours())}:${pad(d.getMinutes())}`;
}

export function escapeHtml(s) {
  if (s === null || s === undefined) return "";
  return String(s)
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#39;");
}
```

- [ ] **Step 1: format.js を作成**
- [ ] **Step 2: index.html の script タグに format.js を追加（または search.js から import）**
- [ ] **Step 3: ブラウザコンソールで `formatFileSize(1536)` → `"1.5 KB"` を手動確認**
- [ ] **Step 4: コミット** `feat(theme): add formatting utilities for size/date/escape`

---

### Task 2.2: 結果カードにサムネイル表示を追加

**Files:**
- Modify: `src/main/webapp/themes/bootstrap/assets/search.js`（`buildResultCard()`）
- Modify: `src/main/webapp/themes/bootstrap/assets/styles.css`（`.result-thumbnail`）

挙動仕様:
- `doc.thumbnail` が存在し、`features.thumbnail_enabled` が true なら img を生成。
- lazy-load: `loading="lazy"` 属性。
- ロード失敗時は要素を `.style.display = "none"` で非表示。

実装スケッチ:

```javascript
function buildThumbnail(doc) {
  if (!doc.thumbnail || !state.features?.thumbnail_enabled) return null;
  const img = document.createElement("img");
  img.className = "result-thumbnail";
  img.alt = "";
  img.loading = "lazy";
  img.src = doc.thumbnail;
  img.addEventListener("error", () => { img.style.display = "none"; });
  return img;
}
```

`buildResultCard` の冒頭で `const thumb = buildThumbnail(d);` を呼び、タイトル左に `.row` 構造で配置。

CSS:

```css
.result-thumbnail {
  width: 96px;
  height: 96px;
  object-fit: contain;
  border: 1px solid var(--bs-border-color);
  border-radius: 4px;
}

@media (max-width: 576px) {
  .result-thumbnail { width: 64px; height: 64px; }
}
```

- [ ] **Step 1: `buildResultCard` を読み、構造を把握**
- [ ] **Step 2: サムネイル要素生成関数を追加**
- [ ] **Step 3: CSS を追加**
- [ ] **Step 4: 手動検証: thumbnail フィールドありの検索を行いブラウザで確認**
- [ ] **Step 5: コミット** `feat(theme): show thumbnails in search results`

---

### Task 2.3: ファイルサイズ・日時・クリック数の表示追加

**Files:**
- Modify: `src/main/webapp/themes/bootstrap/assets/search.js`

実装スケッチ:

```javascript
import { formatFileSize, formatDate } from "./format.js";

function buildMetaLine(d) {
  const parts = [];
  if (d.content_length !== undefined && d.content_length !== null && d.content_length !== "") {
    const sz = formatFileSize(d.content_length);
    if (sz) parts.push(t("result.size", { size: sz }));
  }
  const dt = d.last_modified || d.created;
  const dtFormatted = formatDate(dt);
  if (dtFormatted) parts.push(dtFormatted);
  if (state.features?.search_log_enabled && d.click_count > 0) {
    parts.push(t("result.click_views", { count: d.click_count }));
  }
  const div = document.createElement("div");
  div.className = "result-meta";
  div.textContent = parts.join(" · ");
  return div;
}
```

`buildResultCard` 内で URL 行と snippet の間に挿入。

- [ ] **Step 1: search.js に `buildMetaLine` 関数を追加**
- [ ] **Step 2: `buildResultCard` に組み込む**
- [ ] **Step 3: 手動検証**
- [ ] **Step 4: コミット** `feat(theme): show size/date/click count on result cards`

---

### Task 2.4: キャッシュリンクを `has_cache` 条件付きに変更

**Files:**
- Modify: `src/main/webapp/themes/bootstrap/assets/search.js`

現状: `/api/v2/cache/{docId}` リンクを常に生成している。
変更: `d.has_cache === "true"` または `d.has_cache === true` の場合のみ生成。
URL: 既存 JSP に合わせて `/cache/?docId={id}&queryId={qid}` 形式へ変更可（ただし `/api/v2/cache/{id}` でも機能上同等なので、SPA からは API v2 経由とする）。

- [ ] **Step 1: `buildResultCard` のキャッシュリンク生成箇所を `has_cache` 条件で囲む**
- [ ] **Step 2: コミット** `fix(theme): show cache link only when has_cache is true`

---

### Task 2.5: 類似ドキュメント表示

**Files:**
- Modify: `src/main/webapp/themes/bootstrap/assets/search.js`

仕様:
- `d.similar_docs_count > 1` の場合 "Similar Results ({count})" リンクを表示。
- リンク先: 現在の検索クエリに `sdh=true` と `ex_q=url_hash:HASH` 等を付与（JSP 実装を参考に）。SPA 内では `state.params` を更新して再検索。

実装スケッチ:

```javascript
function buildSimilarLink(d) {
  if (!d.similar_docs_count || d.similar_docs_count <= 1) return null;
  const a = document.createElement("a");
  a.href = "#";
  a.className = "result-similar-link";
  a.textContent = t("result.similar", { count: d.similar_docs_count });
  a.addEventListener("click", (e) => {
    e.preventDefault();
    state.sdh = d.doc_id;
    runSearch();
  });
  return a;
}
```

- [ ] **Step 1: `buildSimilarLink` 追加**
- [ ] **Step 2: `runSearch` に `sdh` パラメータ反映**
- [ ] **Step 3: コミット** `feat(theme): show similar results link when similar_docs_count > 1`

---

### Task 2.6: 検索結果ハイライト表示

**Files:**
- Modify: `src/main/webapp/themes/bootstrap/assets/search.js`

仕様:
- API v2 のレスポンスは highlight 済みのスニペットを `content_description` に含む（JSP 版と同じ）。
- ただし現状の `buildResultCard` は `textContent` でセットしているためタグが見えない。
- 対策: `escapeHtml` した上で、ホワイトリストタグ（`<strong>`, `<em>`）のみ復元するサニタイザを実装。

実装スケッチ:

```javascript
function renderHighlightedSnippet(raw) {
  if (!raw) return "";
  const escaped = escapeHtml(raw);
  return escaped
    .replaceAll("&lt;strong&gt;", "<strong>")
    .replaceAll("&lt;/strong&gt;", "</strong>")
    .replaceAll("&lt;em&gt;", "<em>")
    .replaceAll("&lt;/em&gt;", "</em>");
}
```

`buildResultCard` で snippet をセットするときは `innerHTML = renderHighlightedSnippet(d.content_description)`。

- [ ] **Step 1: サニタイザを `format.js` に追加（テスト容易性のため）**
- [ ] **Step 2: search.js を更新**
- [ ] **Step 3: 手動検証**
- [ ] **Step 4: コミット** `feat(theme): preserve safe highlight tags in snippets`

---

### Phase 2 完了チェックポイント

- [ ] 検索画面でサムネイル/サイズ/日時/クリック数/類似/キャッシュ条件/ハイライトがすべて表示される
- [ ] DevTools コンソールに JS エラーがない
- [ ] `mvn formatter:format` 不要（フロントのみ）

---

## Phase 3: 検索オプション完全再現

### Task 3.1: ソート選択肢を 14 種に拡張（API config から動的構築）

**Files:**
- Modify: `src/main/webapp/themes/bootstrap/index.html`
- Modify: `src/main/webapp/themes/bootstrap/assets/search.js`

挙動:
- `api.getConfig().sort_options` を読んで `<select id="sort-select">` を動的構築。
- 各 option の label は `label_key` を i18n 経由で解決。

実装スケッチ:

```javascript
function renderSortOptions() {
  const sel = document.getElementById("sort-select");
  if (!sel) return;
  sel.innerHTML = "";
  const opts = api.getConfig()?.sort_options || [];
  for (const o of opts) {
    const opt = document.createElement("option");
    opt.value = o.value;
    opt.textContent = t(o.label_key);
    sel.appendChild(opt);
  }
}
```

`app.js` の初期化フローで `await api.init();` 後に `renderSortOptions()` を呼ぶ。

- [ ] **Step 1: index.html に `<select id="sort-select">` を追加（既存なら確認）**
- [ ] **Step 2: search.js に `renderSortOptions()` 追加**
- [ ] **Step 3: 手動検証 — オプションが 14 種表示される（条件次第で 12 種）**
- [ ] **Step 4: コミット** `feat(theme): render full sort option list from ui/config`

---

### Task 3.2: 件数 (num) セレクト追加

**Files:**
- Modify: `src/main/webapp/themes/bootstrap/index.html`
- Modify: `src/main/webapp/themes/bootstrap/assets/search.js`

実装スケッチ:

```html
<select id="num-select" class="form-select form-select-sm" aria-label="..."></select>
```

```javascript
function renderNumOptions() {
  const sel = document.getElementById("num-select");
  if (!sel) return;
  sel.innerHTML = "";
  const nums = api.getConfig()?.num_options || [10, 20, 30, 40, 50, 100];
  for (const n of nums) {
    const opt = document.createElement("option");
    opt.value = String(n);
    opt.textContent = t("search.num", { num: n });
    sel.appendChild(opt);
  }
  sel.value = String(state.num || nums[0]);
  sel.addEventListener("change", () => {
    state.num = Number(sel.value);
    runSearch();
  });
}
```

- [ ] **Step 1-3: 上記実装** → **コミット** `feat(theme): add result-count selector`

---

### Task 3.3: 言語フィルタセレクト追加

**Files:**
- Modify: `index.html`
- Modify: `search.js`

実装: Task 3.2 と同型。`lang_options` を使う。

- [ ] **Step 1-3: 実装 → コミット** `feat(theme): add language filter selector`

---

### Task 3.4: ラベルフィルタ複数選択チェックボックス UI

**Files:**
- Modify: `search.js`
- Modify: `index.html`
- Modify: `styles.css`

仕様:
- `label_options` が空でないときのみ表示。
- 複数選択可能なチェックボックスリスト or マルチセレクト。Bootstrap の dropdown 内チェックボックスで実装。
- 選択値は配列で `state.fields.label = [...]` として `/api/v2/search?fields.label=...` に渡す。

実装スケッチ（dropdown 内チェックボックス、createElement のみ）:

```html
<div class="dropdown" id="label-filter">
  <button class="btn btn-outline-secondary btn-sm dropdown-toggle" type="button"
          data-bs-toggle="dropdown" data-i18n="search.labels"></button>
  <ul class="dropdown-menu p-2" id="label-options-list"></ul>
</div>
```

```javascript
function renderLabelOptions() {
  const list = document.getElementById("label-options-list");
  if (!list) return;
  while (list.firstChild) list.removeChild(list.firstChild);
  const opts = api.getConfig()?.label_options || [];
  if (opts.length === 0) {
    document.getElementById("label-filter").style.display = "none";
    return;
  }
  for (const o of opts) {
    const li = document.createElement("li");
    const label = document.createElement("label");
    label.className = "dropdown-item";
    const checkbox = document.createElement("input");
    checkbox.type = "checkbox";
    checkbox.value = o.value; // setAttribute alternative; value is safe
    checkbox.id = `lbl_${o.value}`;
    checkbox.addEventListener("change", (e) => {
      const v = e.target.value;
      state.fields.label = state.fields.label || [];
      if (e.target.checked && !state.fields.label.includes(v)) state.fields.label.push(v);
      if (!e.target.checked) state.fields.label = state.fields.label.filter(x => x !== v);
      runSearch();
    });
    label.appendChild(checkbox);
    // o.name は API レスポンス (untrusted) - textContent で安全に挿入
    label.appendChild(document.createTextNode(" "));
    label.appendChild(document.createTextNode(o.name));
    li.appendChild(label);
    list.appendChild(li);
  }
}
```

- [ ] **Step 1-5: 実装 → コミット** `feat(theme): add label filter dropdown`

---

### Phase 3 完了チェックポイント

- [ ] sort/num/lang/label の各オプションが動的に表示・反映される

---

## Phase 4: ファセット完全再現

### Task 4.1: timestamp ファセット（時間範囲）

**Files:**
- Modify: `search.js`

仕様:
- JSP の `facet_timestamp_*` キー（24h, 1week, 1month, 3month, 6month, 1year, 2year, 3year）を表示。
- クリック時、`fess:date:[NOW-1d TO NOW]` 等の range クエリを `fq` に追加。

実装スケッチ:

```javascript
const TIMESTAMP_RANGES = [
  { key: "1day",   query: "[now/d-1d TO *]" },
  { key: "1week",  query: "[now/d-7d TO *]" },
  { key: "1month", query: "[now/d-1M TO *]" },
  { key: "3month", query: "[now/d-3M TO *]" },
  { key: "6month", query: "[now/d-6M TO *]" },
  { key: "1year",  query: "[now/d-1y TO *]" },
  { key: "2year",  query: "[now/d-2y TO *]" },
  { key: "3year",  query: "[now/d-3y TO *]" },
];

function renderTimestampFacet(env) {
  const group = createFacetGroup(t("facet.timestamp_title"));
  for (const r of TIMESTAMP_RANGES) {
    const count = (env.facet_query || []).find(x => x.value === `timestamp:${r.query}`)?.count;
    if (count === undefined) continue;
    appendFacetItem(group, t(`facet.timestamp_${r.key}`), count, () => {
      state.facetQueries = state.facetQueries || [];
      const fq = `timestamp:${r.query}`;
      if (!state.facetQueries.includes(fq)) state.facetQueries.push(fq);
      runSearch();
    });
  }
  return group;
}
```

検索 API は `facet.query` パラメータでサーバに range を伝える必要がある。SearchHandler が既存ですべての `facet.query` を受け付けるか確認し、必要なら拡張する。

- [ ] **Step 1: SearchHandler の facet 入力サポートを調査**
- [ ] **Step 2: 必要に応じてサーバ拡張（別タスクに切り出すなら Task 4.1.5）**
- [ ] **Step 3: search.js に timestamp facet 実装**
- [ ] **Step 4: 手動検証**
- [ ] **Step 5: コミット** `feat(theme): add timestamp range facet`

---

### Task 4.2: contentLength ファセット（サイズ範囲）

**Files:**
- Modify: `search.js`

同様の構造。範囲:
- ~10KB, 10KB~100KB, 100KB~1MB, 1MB~10MB, 10MB~。

- [ ] **Step 1-3: 実装 → コミット** `feat(theme): add content-length range facet`

---

### Task 4.3: filetype ファセット

**Files:**
- Modify: `search.js`
- Modify: `format.js`（filetype アイコンマップ）

仕様:
- API レスポンスの `facet_field.filetype.result` を表示。
- ラベルは `facet.filetype_{value}` キーで i18n。
- Font Awesome のアイコンを併用（例: `fa-file-pdf`）。

- [ ] **Step 1: filetype → i18n key / アイコンクラスのマップを `format.js` に定義**
- [ ] **Step 2: `renderFacets` 内で filetype を別グループとして描画**
- [ ] **Step 3: コミット** `feat(theme): add filetype facet with icons`

---

### Task 4.4: 選択中のファセットをクエリチップで表示し、解除可能にする

**Files:**
- Modify: `search.js`
- Modify: `index.html`
- Modify: `styles.css`

仕様:
- 検索結果の上部に「Active filters」セクションを設け、各 facet 選択を ❌ 付きのチップで表示。
- クリックで解除。

- [ ] **Step 1-3: 実装 → コミット** `feat(theme): show active facet chips with remove buttons`

---

## Phase 5: ナビゲーション・トップ・フッター

### Task 5.1: ユーザーメニュー（Profile / Admin / Logout）

**Files:**
- Modify: `src/main/webapp/themes/bootstrap/assets/auth.js`
- Modify: `index.html`

仕様:
- ログイン後、ユーザー名をクリックで dropdown 展開。
- メニュー項目:
  - `auth.me` レスポンスの `user.editable === true` のとき "Profile" → `/profile` へ遷移
  - `user.admin === true` のとき "Administration" → `/admin/` (新規ウィンドウ)
  - "Logout"
- ログインしていないとき: "Login" ボタンのみ。

- [ ] **Step 1: `/api/v2/auth/me` レスポンスに `editable`, `admin` フィールドがあるか確認、なければサーバ側に追加**
- [ ] **Step 2: auth.js でメニュー描画**
- [ ] **Step 3: 手動検証**
- [ ] **Step 4: コミット** `feat(theme): show user dropdown with profile/admin/logout`

---

### Task 5.2: EOL / 開発モード警告アイコン

**Files:**
- Modify: `index.html`
- Modify: `search.js` or `app.js`
- Modify: `i18n/messages.*.json`

仕様:
- `features.eoled === true` のときヘッダー右に ⚠ アイコン + tooltip。
- `features.development_mode === true` でも同様。

- [ ] **Step 1-3: 実装 → コミット** `feat(theme): show EOL and dev-mode warning indicators`

---

### Task 5.3: トップページレイアウト（中央検索ボックス + 中央ポピュラーワード）

**Files:**
- Modify: `index.html`
- Modify: `search.js`
- Modify: `app.js`

仕様:
- `window.location.pathname === "/"` かつ `q` パラメータがないとき、検索ボックスを大きく中央に表示。
- ロゴ + 検索ボックス + ポピュラーワード（最大 3 件 inline、それ以上は折り畳み）。
- 検索実行で通常レイアウトへ切替。

実装方針: `<main>` 内に `<section id="home-view">` と `<section id="results-view">` を持ち、`router.js` がどちらかを表示。

- [ ] **Step 1: index.html に home-view セクション追加**
- [ ] **Step 2: app.js で初回表示切替**
- [ ] **Step 3: CSS で中央配置**
- [ ] **Step 4: コミット** `feat(theme): add centered home view with logo and search`

---

### Task 5.4: フッター（Back to Top + コピーライト）

**Files:**
- Modify: `index.html`
- Modify: `styles.css`
- Modify: `i18n/messages.*.json`

仕様:
- フローティング "Back to Top" ボタン（スクロール時のみ表示）。
- コピーライト文字列を i18n キー `labels.footer_copyright` 相当に変更。

- [ ] **Step 1-3: 実装 → コミット** `feat(theme): add back-to-top button and i18n footer`

---

## Phase 6: プロファイル画面

### Task 6.1: profile.js モジュール作成

**Files:**
- Create: `src/main/webapp/themes/bootstrap/assets/profile.js`
- Modify: `index.html` (profile-view セクション追加)
- Modify: `app.js` (routing)

仕様:
- ルート `/profile` の SPA ビュー。
- 旧パスワード / 新パスワード / 確認パスワードの 3 フィールド + 送信ボタン。
- バリデーション: 新パスワード ≥ 8 文字、新と確認が一致。
- 送信先: `POST /api/v2/auth/password`。
- 成功時: 成功メッセージ + 検索画面に戻るリンク。
- 失敗時: エラー文（`error.invalid_credentials`, `error.password_policy` 等）。

実装スケッチ（createElement のみ、innerHTML 不使用）:

```javascript
// SPDX-License-Identifier: Apache-2.0
import { api } from "./api.js";
import { t } from "./i18n.js";

function makeField(labelKey, inputId, type, extra) {
  const wrapper = document.createElement("div");
  wrapper.className = "mb-3";
  const label = document.createElement("label");
  label.htmlFor = inputId;
  label.className = "form-label";
  label.textContent = t(labelKey);
  const input = document.createElement("input");
  input.type = type;
  input.id = inputId;
  input.className = "form-control";
  input.required = true;
  if (extra) Object.assign(input, extra);
  wrapper.append(label, input);
  return { wrapper, input };
}

export function attach() {
  const view = document.getElementById("profile-view");
  if (!view) return;
  while (view.firstChild) view.removeChild(view.firstChild);

  const h1 = document.createElement("h1");
  h1.textContent = t("profile.title");
  view.appendChild(h1);

  const form = document.createElement("form");
  form.id = "password-form";
  form.noValidate = true;

  const oldF = makeField("profile.old_password", "old-password", "password");
  const newF = makeField("profile.new_password", "new-password", "password", { minLength: 8 });
  const confirmF = makeField("profile.confirm_password", "confirm-password", "password");
  form.append(oldF.wrapper, newF.wrapper, confirmF.wrapper);

  const submit = document.createElement("button");
  submit.type = "submit";
  submit.className = "btn btn-primary";
  submit.textContent = t("profile.update");
  form.appendChild(submit);

  const errBox = document.createElement("div");
  errBox.id = "profile-error";
  errBox.className = "alert alert-danger d-none mt-3";
  errBox.setAttribute("role", "alert");
  form.appendChild(errBox);

  const okBox = document.createElement("div");
  okBox.id = "profile-success";
  okBox.className = "alert alert-success d-none mt-3";
  okBox.setAttribute("role", "alert");
  form.appendChild(okBox);

  view.appendChild(form);

  form.addEventListener("submit", async (e) => {
    e.preventDefault();
    errBox.classList.add("d-none");
    okBox.classList.add("d-none");
    if (newF.input.value !== confirmF.input.value) {
      errBox.textContent = t("profile.error_mismatch");
      errBox.classList.remove("d-none");
      return;
    }
    try {
      await api.post("/auth/password", {
        old_password: oldF.input.value,
        new_password: newF.input.value,
      });
      okBox.textContent = t("profile.success");
      okBox.classList.remove("d-none");
      oldF.input.value = newF.input.value = confirmF.input.value = "";
    } catch (err) {
      // err.message は API/Network エラーの message。textContent で安全に挿入。
      errBox.textContent = err?.message || t("error.server");
      errBox.classList.remove("d-none");
    }
  });
}
```

- [ ] **Step 1: profile.js 新規作成**
- [ ] **Step 2: index.html に `<section id="profile-view" hidden></section>` 追加**
- [ ] **Step 3: app.js に "if (path === '/profile') showProfile()" を追加**
- [ ] **Step 4: i18n キーを ja/en に追加（profile.title, profile.old_password, profile.new_password, profile.confirm_password, profile.update, profile.success, profile.error_mismatch）**
- [ ] **Step 5: 手動検証**
- [ ] **Step 6: コミット** `feat(theme): add profile/password change page`

---

## Phase 7: ヘルプ画面

### Task 7.1: ヘルプコンテンツ JSON 作成

**Files:**
- Create: `src/main/webapp/themes/bootstrap/help/en.json`
- Create: `src/main/webapp/themes/bootstrap/help/ja.json`

仕様:
- JSP 版 `help.jsp` / `help_ja.jsp` の HTML をベースに、セクション分割した JSON。
- 構造:
  ```json
  {
    "sections": [
      { "id": "basic", "title": "Basic Search",
        "html": "<p>...</p>" },
      { "id": "operators", "title": "Search Operators",
        "html": "<table>...</table>" }
    ]
  }
  ```
- HTML は信頼できる静的コンテンツ（XSS 心配なし、テーマ作者管理下）。

- [ ] **Step 1: help.jsp / help_ja.jsp を読み、セクション設計**
- [ ] **Step 2: en.json / ja.json を作成**
- [ ] **Step 3: コミット** `feat(theme): add help content json for en/ja`

---

### Task 7.2: help.js モジュール作成

**Files:**
- Create: `src/main/webapp/themes/bootstrap/assets/help.js`
- Modify: `index.html` (help-view セクション)
- Modify: `app.js`

**ヘルプコンテンツの信頼境界:**
- `help/*.json` の `html` フィールドはリポジトリ管理下の静的コンテンツ（テーマ作者がコミット時にレビュー）。
- これでも防御的に **最小ホワイトリストサニタイザ** を経由させる。許可タグ: `p, br, ul, ol, li, table, thead, tbody, tr, th, td, code, pre, strong, em, a (href のみ), h2, h3, h4`。`<script>`, `<iframe>`, `on*` 属性等は除去。
- セクションタイトル (`title`) は textContent で挿入。

**実装スケッチ:**

```javascript
// SPDX-License-Identifier: Apache-2.0
import { getLocale, t } from "./i18n.js";

const ALLOWED_TAGS = new Set([
  "P","BR","UL","OL","LI","TABLE","THEAD","TBODY","TR","TH","TD",
  "CODE","PRE","STRONG","EM","H2","H3","H4","A","DIV","SPAN",
]);
const ALLOWED_ATTRS = {
  A: new Set(["href"]),
  TABLE: new Set(["class"]),
  TH: new Set(["scope"]),
};

function sanitizeNode(node) {
  // テキストノードは安全
  if (node.nodeType === Node.TEXT_NODE) return node.cloneNode(true);
  if (node.nodeType !== Node.ELEMENT_NODE) return null;
  const tag = node.tagName.toUpperCase();
  if (!ALLOWED_TAGS.has(tag)) return null;
  const el = document.createElement(tag);
  // 属性のホワイトリスト
  const allowed = ALLOWED_ATTRS[tag] || new Set();
  for (const attr of Array.from(node.attributes)) {
    if (!allowed.has(attr.name)) continue;
    if (attr.name === "href") {
      // javascript: スキームを排除
      const v = String(attr.value).trim();
      if (v.startsWith("javascript:") || v.startsWith("data:")) continue;
      el.setAttribute("href", v);
      if (/^https?:/i.test(v)) {
        el.setAttribute("rel", "noopener noreferrer");
        el.setAttribute("target", "_blank");
      }
    } else {
      el.setAttribute(attr.name, attr.value);
    }
  }
  for (const child of Array.from(node.childNodes)) {
    const sanitized = sanitizeNode(child);
    if (sanitized) el.appendChild(sanitized);
  }
  return el;
}

function sanitizeHtml(html) {
  const tpl = document.createElement("template");
  // 構文解析専用。document に挿入されないので scripts は実行されない。
  tpl.innerHTML = String(html);
  const frag = document.createDocumentFragment();
  for (const child of Array.from(tpl.content.childNodes)) {
    const node = sanitizeNode(child);
    if (node) frag.appendChild(node);
  }
  return frag;
}

export async function attach() {
  const view = document.getElementById("help-view");
  if (!view) return;
  while (view.firstChild) view.removeChild(view.firstChild);

  const locale = getLocale();
  let data;
  try {
    const resp = await fetch(`/themes/bootstrap/help/${encodeURIComponent(locale)}.json`);
    if (!resp.ok) throw new Error("not found");
    data = await resp.json();
  } catch {
    const fallback = await fetch("/themes/bootstrap/help/en.json");
    data = await fallback.json();
  }

  const title = document.createElement("h1");
  title.textContent = t("help.title");
  view.appendChild(title);

  for (const s of data.sections || []) {
    const section = document.createElement("section");
    section.className = "help-section";
    if (s.id) section.id = `help-${s.id}`;
    const h2 = document.createElement("h2");
    h2.textContent = s.title; // untrusted source from JSON: textContent で安全
    section.appendChild(h2);
    section.appendChild(sanitizeHtml(s.html));
    view.appendChild(section);
  }
}
```

- [ ] **Step 1: help.js 新規作成（上記実装、サニタイザ付き）**
- [ ] **Step 2: index.html に `<section id="help-view" hidden class="container my-4"></section>` 追加**
- [ ] **Step 3: app.js のルーティングに `/help → showHelp()` を追加**
- [ ] **Step 4: 手動検証**
- [ ] **Step 5: コミット** `feat(theme): add help page renderer with HTML sanitizer`

---

## Phase 8: エラー画面（SPA 委譲）

このフェーズは `/error/*` を Static theme 有効時に SPA で表示するためのサーバ・クライアント両方の対応を行う。

### Task 8.1: ThemeViewAction にエラーモード追加

**Files:**
- Modify: `src/main/java/org/codelibs/fess/app/web/theme/ThemeViewAction.java`
- Modify: `src/test/java/org/codelibs/fess/app/web/theme/ThemeViewActionTest.java`（存在しなければ新規）

**仕様:**
- リクエスト URI が `/error/{kind}` のとき、HTTP ステータスを以下のテーブルに従って設定。
  | path | status |
  |------|--------|
  | `/error/notFound` | 404 |
  | `/error/notfound` | 404（互換、JSP redirect.jsp が小文字で生成するため） |
  | `/error/badRequest` | 400 |
  | `/error/system` | 500 |
  | `/error/busy` | 503 |
  | `/error` または `/error/error` | 500 |
- index.html を返す前に以下のレスポンスヘッダを付与:
  - `X-Fess-Route: error`
  - `X-Fess-Error-Code: <status>`
- index.html 本文には Java 側でテンプレート置換は行わない。SPA 側が `window.location.pathname` から code を導出する（後述）。
- URL クエリ（`?url=...`）はそのまま残し、SPA が `URL.searchParams.get("url")` で読み取れるようにする。

**実装スケッチ:**

```java
// ThemeViewAction
private static final Map<String, Integer> ERROR_PATH_TO_STATUS = Map.ofEntries(
        Map.entry("/error", 500),
        Map.entry("/error/error", 500),
        Map.entry("/error/system", 500),
        Map.entry("/error/badrequest", 400),
        Map.entry("/error/badRequest", 400),
        Map.entry("/error/notfound", 404),
        Map.entry("/error/notFound", 404),
        Map.entry("/error/busy", 503));

@Execute
public ActionResponse index() {
    final HttpServletRequest req = ...;
    final HttpServletResponse resp = ...;
    final String mode = (String) req.getAttribute(REQ_ATTR_MODE);
    if ("ASSET".equals(mode)) {
        return serveAsset((String) req.getAttribute(REQ_ATTR_ASSET_PATH));
    }
    final String uri = stripContextPath(req);
    if (uri.startsWith("/error")) {
        applyErrorHeaders(resp, uri);
    }
    return serveIndex();
}

private void applyErrorHeaders(final HttpServletResponse resp, final String uri) {
    final Integer status = ERROR_PATH_TO_STATUS.getOrDefault(uri, 500);
    resp.setStatus(status);
    resp.setHeader("X-Fess-Route", "error");
    resp.setHeader("X-Fess-Error-Code", String.valueOf(status));
    // index.html は no-store なので CSP / cache 設定は既存 serveIndex() に任せる
}
```

- [ ] **Step 1: 既存 ThemeViewAction.java を読み、`serveIndex()` `serveAsset()` の構造を把握**

- [ ] **Step 2: テストを書く**

新規ファイル `src/test/java/org/codelibs/fess/app/web/theme/ThemeViewActionTest.java`（存在しなければ）、または既存に追記:

```java
public void test_errorNotFoundPathReturns404() throws Exception {
    final StubRequest req = newGetRequest("/error/notFound");
    req.setAttribute(REQ_ATTR_MODE, "INDEX");
    final StubResponse resp = new StubResponse();
    action.invoke(req, resp);
    assertEquals(404, resp.getStatus());
    assertEquals("error", resp.getHeader("X-Fess-Route"));
    assertEquals("404", resp.getHeader("X-Fess-Error-Code"));
}

public void test_errorBadRequestPathReturns400() throws Exception { ... }
public void test_errorSystemPathReturns500() throws Exception { ... }
public void test_errorBusyPathReturns503() throws Exception { ... }
public void test_errorLowercaseNotfoundReturns404() throws Exception { ... }

public void test_normalUiPathReturns200() throws Exception {
    final StubRequest req = newGetRequest("/search");
    req.setAttribute(REQ_ATTR_MODE, "INDEX");
    final StubResponse resp = new StubResponse();
    action.invoke(req, resp);
    assertEquals(200, resp.getStatus());
    assertNull(resp.getHeader("X-Fess-Route"));
}
```

注: テストハーネスの組み立て方は既存の Lasta テスト（`UnitFessTestCase` 派生 or stub 利用）に合わせる。実テストが困難なら、エラーパス→ステータスのマッピングロジックを package-private static method に切り出して **そこを** ユニットテストする。

- [ ] **Step 3: テスト実行で失敗を確認**

- [ ] **Step 4: ThemeViewAction を実装**

- [ ] **Step 5: テスト PASS 確認**

- [ ] **Step 6: フォーマット & ライセンス**

- [ ] **Step 7: コミット** `feat(theme): serve /error/* via static theme SPA with proper HTTP status`

---

### Task 8.2: 既存 JSP error 用 redirect.jsp と LastaFlute error handling の整合

**Files:**
- Read: `src/main/webapp/WEB-INF/view/error/redirect.jsp`
- Read: `src/main/webapp/WEB-INF/web.xml` の `<error-page>` 定義
- Modify（必要なら）: `src/main/java/org/codelibs/fess/app/web/error/ErrorAction.java`（存在すれば）

**目的:** Static theme 有効時、サーブレットコンテナや LastaFlute が発火する `<error-page>` リダイレクト先（典型: `/error/notfound` や `/error/system`）がフィルタを経由して ThemeViewAction まで届くことを確認。

- [ ] **Step 1: web.xml の `<error-page>` 設定を確認**

実行: `read src/main/webapp/WEB-INF/web.xml`
確認点: 404 / 500 が `/error/notfound` `/error/system` 等にマップされているか。FORWARD ディスパッチで届くか。

- [ ] **Step 2: StaticThemeFilter のサーブレット dispatcher 設定を確認**

`web.xml` で `staticThemeFilter` の `<dispatcher>` が `REQUEST` のみなら、`<error-page>` 経由の FORWARD では発火しない。発火させたいなら `<dispatcher>FORWARD</dispatcher>` を追加する必要がある。ただし lastaPrepareFilter との順序整合に注意。

- [ ] **Step 3: 実機検証用に意図的に 500 を起こすパスを用意する案を検討（test fixture）**

ただし、SPA からは「直接 `/error/notFound` をリクエスト」されることが想定の主経路（Static theme 有効時にユーザーが手で叩く / リンクから来る）であり、サーブレット error-page 経由の動作は副次的。よって本タスクの主成果は: **既存 error-page 設定が壊れていないことを確認するだけ** とする。

- [ ] **Step 4: 必要なら web.xml 微調整 → コミット**

コミット例（変更があれば）: `chore(web.xml): ensure error-page redirects reach static theme filter`
変更なければ Skip 可。

---

### Task 8.3: error.js モジュール（SPA 側エラー画面レンダラ）

**Files:**
- Create: `src/main/webapp/themes/bootstrap/assets/error.js`
- Modify: `src/main/webapp/themes/bootstrap/index.html`（`<section id="error-view" hidden></section>` 追加）
- Modify: `src/main/webapp/themes/bootstrap/assets/app.js`（routing dispatch）
- Modify: `src/main/webapp/themes/bootstrap/i18n/messages.en.json` / `messages.ja.json`

**仕様:**
- `location.pathname` から code を導出（path-based、大文字小文字両対応）:
  ```
  /error または /error/error または /error/system  → 500
  /error/badRequest, /error/badrequest             → 400
  /error/notFound,   /error/notfound               → 404
  /error/busy                                      → 503
  上記以外                                          → 500
  ```
- `URL.searchParams.get("url")` で問題の URL を取得（あれば「Requested URL: ...」として **textContent** で表示）。
- 表示内容:
  - タイトル: `t(`error.title_${code}`)` (i18n)
  - メッセージ: `t(`error.body_${code}`)`
  - 要求 URL（あれば）: textContent でラベル + URL
  - "Go to home" リンク → `/`
  - "Contact admin" メッセージ
- すべて **createElement + textContent** で構築する。`innerHTML` は使わない。

**実装スケッチ（XSS 安全版）:**

```javascript
// SPDX-License-Identifier: Apache-2.0
import { t } from "./i18n.js";

const PATH_TO_CODE = new Map([
  ["/error", "500"],
  ["/error/error", "500"],
  ["/error/system", "500"],
  ["/error/badrequest", "400"],
  ["/error/notfound", "404"],
  ["/error/busy", "503"],
]);

function resolveErrorCode() {
  // SSR 側で送られる X-Fess-Error-Code ヘッダは fetch リダイレクト後の document には届かない
  // ため、location.pathname を権威ソースとする。
  const path = location.pathname.toLowerCase();
  return PATH_TO_CODE.get(path) || "500";
}

function setText(el, text) {
  el.textContent = text == null ? "" : String(text);
}

export function attach() {
  const view = document.getElementById("error-view");
  if (!view) return;
  // クリア
  while (view.firstChild) view.removeChild(view.firstChild);

  const code = resolveErrorCode();
  const requestedUrl = new URL(location.href).searchParams.get("url");

  const h1 = document.createElement("h1");
  h1.className = "error-title";
  setText(h1, t(`error.title_${code}`));
  view.appendChild(h1);

  const p = document.createElement("p");
  p.className = "error-body";
  setText(p, t(`error.body_${code}`));
  view.appendChild(p);

  if (requestedUrl) {
    const dl = document.createElement("dl");
    dl.className = "error-detail";
    const dt = document.createElement("dt");
    setText(dt, t("error.requested_url"));
    const dd = document.createElement("dd");
    // requestedUrl は untrusted。textContent で安全に挿入。
    setText(dd, requestedUrl);
    dl.append(dt, dd);
    view.appendChild(dl);
  }

  const actions = document.createElement("div");
  actions.className = "error-actions";
  const home = document.createElement("a");
  home.href = "/";
  home.className = "btn btn-primary";
  setText(home, t("error.go_home"));
  actions.appendChild(home);
  view.appendChild(actions);

  const contact = document.createElement("p");
  contact.className = "error-contact text-muted small mt-3";
  setText(contact, t("error.contact_admin"));
  view.appendChild(contact);
}
```

**index.html 側追加:**

```html
<section id="error-view" hidden class="container my-4"></section>
```

**app.js のルーティング更新:**

```javascript
import * as errorView from "./error.js";
// ...
function dispatchRoute() {
  const path = location.pathname;
  hideAll();
  if (path.startsWith("/error")) {
    document.getElementById("error-view").hidden = false;
    errorView.attach();
  } else if (path === "/profile") {
    showProfile();
  } else if (path === "/help") {
    showHelp();
  } else {
    showResults();
  }
}
```

**i18n キー追加（en/ja）:**
```
error.title_400, error.body_400
error.title_404, error.body_404
error.title_500, error.body_500
error.title_503, error.body_503
error.requested_url
error.go_home
error.contact_admin
```

- [ ] **Step 1: error.js を新規作成（上記実装）**
- [ ] **Step 2: index.html に error-view セクション追加**
- [ ] **Step 3: app.js のルーティングを更新**
- [ ] **Step 4: i18n JSON に error キーを追加（Task 9.1 と統合可）**
- [ ] **Step 5: 手動検証**

実行:
```
./bin/fess
```
ブラウザで以下を確認:
- http://localhost:8080/error/notFound → 404 + ローカライズされた "Page not found"
- http://localhost:8080/error/notfound?url=https://example.com/missing → 404 + Requested URL 表示
- http://localhost:8080/error/badRequest → 400 + "Bad Request"
- http://localhost:8080/error/system → 500 + "System Error"
- http://localhost:8080/error/busy → 503 + "Service Unavailable"
- 各画面で DevTools の Network タブを確認: HTTP ステータスコードが上記と一致

- [ ] **Step 6: コミット** `feat(theme): add SPA error page renderer with safe DOM construction`

---

## Phase 9: i18n 全面拡張

### Task 9.1: en/ja のキー大幅追加

**Files:**
- Modify: `src/main/webapp/themes/bootstrap/i18n/messages.en.json`
- Modify: `src/main/webapp/themes/bootstrap/i18n/messages.ja.json`

追加するキー（網羅リスト）:

```
# Search options
search.sort.score
search.sort.score_desc
search.sort.filename_asc
search.sort.filename_desc
search.sort.created_asc
search.sort.created_desc
search.sort.content_length_asc
search.sort.content_length_desc
search.sort.last_modified_asc
search.sort.last_modified_desc
search.sort.click_count_asc
search.sort.click_count_desc
search.sort.favorite_count_asc
search.sort.favorite_count_desc
search.num    # "{num} results"
search.lang
search.labels
search.options
search.options_close
search.options_clear

# Result card
result.size   # "{size}"
result.click_views  # "{count} views"
result.similar      # "Similar Results ({count})"
result.cache
result.favorite_add
result.favorite_remove

# Facet (timestamp)
facet.timestamp_title
facet.timestamp_1day
facet.timestamp_1week
facet.timestamp_1month
facet.timestamp_3month
facet.timestamp_6month
facet.timestamp_1year
facet.timestamp_2year
facet.timestamp_3year

# Facet (contentLength)
facet.contentLength_title
facet.contentLength_0
facet.contentLength_1
facet.contentLength_2
facet.contentLength_3
facet.contentLength_4

# Facet (filetype) - JSP の filetype 一覧から（html, msword, msexcel, mspowerpoint, odt, ods, odp, pdf, txt, image, audio, video, archive, others 等 約 15 種）
facet.filetype_title
facet.filetype_html
facet.filetype_msword
facet.filetype_msexcel
facet.filetype_mspowerpoint
facet.filetype_odt
facet.filetype_ods
facet.filetype_odp
facet.filetype_pdf
facet.filetype_txt
facet.filetype_image
facet.filetype_audio
facet.filetype_video
facet.filetype_archive
facet.filetype_others

# Facet (label)
facet.label_title
facet.empty

# Navigation
nav.help
nav.administration
nav.profile
nav.eol_warning
nav.dev_mode_warning

# Profile
profile.title
profile.old_password
profile.new_password
profile.confirm_password
profile.update
profile.success
profile.error_mismatch
profile.back

# Help
help.title

# Error
error.title_400
error.title_404
error.title_500
error.title_503
error.body_400
error.body_404
error.body_500
error.body_503
error.go_home
error.contact_admin

# Footer
footer.copyright   # "© {year} ..."
footer.back_to_top

# Page title
page.title

# Lang options (key per locale)
lang.ja
lang.en
lang.de
lang.es
lang.fr
lang.ko
lang.pt-BR
lang.zh-CN
... (FessConfig.supportedLanguages 全て)
```

- [ ] **Step 1: 既存 messages.en.json / messages.ja.json を読み、現状キーを把握**
- [ ] **Step 2: 上記キーを統合した完全な JSON を書き出す（既存キーは保持）**
- [ ] **Step 3: 対応する `fess_label.properties` のキーを参照し、用語の一貫性を確認**
- [ ] **Step 4: JSON の構文チェック (`python -m json.tool messages.en.json`)**
- [ ] **Step 5: コミット** `feat(theme): expand i18n message keys for full UI parity`

---

### Task 9.2: 他言語（de/es/fr/ko/pt-BR/zh-CN）の追加

**Files:**
- Create: `src/main/webapp/themes/bootstrap/i18n/messages.de.json`
- Create: `src/main/webapp/themes/bootstrap/i18n/messages.es.json`
- Create: `src/main/webapp/themes/bootstrap/i18n/messages.fr.json`
- Create: `src/main/webapp/themes/bootstrap/i18n/messages.ko.json`
- Create: `src/main/webapp/themes/bootstrap/i18n/messages.pt-BR.json`
- Create: `src/main/webapp/themes/bootstrap/i18n/messages.zh-CN.json`
- Modify: `theme.yml`（`supported_locales` リスト拡張）

仕様:
- 既存 `fess_label_de.properties`, `fess_label_es.properties` 等から、Task 9.1 で定義したキーに対応する翻訳を抽出。
- 該当する fess_label キーが存在しない場合は en にフォールバック（JSON では空文字 or 欠落で OK、`i18n.js` がフォールバック処理）。

- [ ] **Step 1: fess_label_{locale}.properties から該当キーを抽出するスクリプトを書く（任意、手動でも可）**

```bash
# 例:
for L in de es fr ko pt-BR zh-CN; do
  grep -E "^labels\.(search|facet|profile|help|error|footer|index)" \
    "src/main/resources/fess_label_${L//-/_}.properties" || true
done
```

- [ ] **Step 2: 各言語の messages.{locale}.json を生成**
- [ ] **Step 3: i18n.js の SUPPORTED 配列を `["en", "ja", "de", "es", "fr", "ko", "pt-BR", "zh-CN"]` に拡張**
- [ ] **Step 4: theme.yml の supported_locales 更新**
- [ ] **Step 5: コミット** `feat(theme): add i18n bundles for de/es/fr/ko/pt-BR/zh-CN`

---

## Phase 10: 検証

### Task 10.1: 既存 LabelMessageThemeParity テストの更新

**Files:**
- Modify: `src/test/java/org/codelibs/fess/theme/LabelMessageThemeParityTest.java`

仕様:
- JSP 用 `fess_label.properties` と theme JSON の主要キーが揃っていることを確認。
- 新規追加した theme キー（profile.*, error.*, etc.）は対応する fess_label キーがあれば一致を検証。

- [ ] **Step 1: テスト拡張 → PASS 確認 → コミット**

---

### Task 10.2: StaticThemeFilterTest 拡張

**Files:**
- Modify: `src/test/java/org/codelibs/fess/filter/StaticThemeFilterTest.java`

追加ケース:
- `/profile` → INDEX フォワード
- `/help` → INDEX フォワード
- `/error/notFound` → INDEX フォワード（404 status）

- [ ] **Step 1: テスト追加 → PASS → コミット**

---

### Task 10.3: 手動検証チェックリスト

以下を実機で確認:

- [ ] `./bin/fess` 起動
- [ ] http://localhost:8080/ で中央検索フォーム + ポピュラーワードが表示される
- [ ] 検索実行で結果リスト + ファセット表示
- [ ] サムネイル / サイズ / 日時 / クリック数 / 類似 / キャッシュリンクが各カードに表示
- [ ] ソート / 件数 / 言語 / ラベルセレクタが機能する
- [ ] ファセット: label / timestamp / contentLength / filetype すべてクリック可能
- [ ] 関連クエリが結果上部に表示
- [ ] http://localhost:8080/help でヘルプ表示（en / ja 切替）
- [ ] http://localhost:8080/profile でパスワード変更フォーム表示・送信
- [ ] http://localhost:8080/error/notFound で 404 ページ表示（HTTP ステータス 404）
- [ ] ヘッダー右に Login → ログイン後ユーザー名 dropdown
- [ ] ログイン後 dropdown: Profile / Admin / Logout
- [ ] EOL / dev mode 警告アイコン（適切な設定下で）
- [ ] フッターに Back to Top ボタン
- [ ] 主要 8 言語で UI ラベルが切り替わる

---

### Task 10.4: 最終ビルド

- [ ] `mvn formatter:format && mvn license:format`
- [ ] `mvn test`（全テスト PASS）
- [ ] `mvn package -DskipTests`（war ビルド成功）

---

## Phase 11: Advanced Search

### Task 11.1: `/advance` ルートと検索ビルダ UI

**Files:**
- Create: `src/main/webapp/themes/bootstrap/assets/advance.js`
- Modify: `src/main/webapp/themes/bootstrap/index.html`（`<section id="advance-view" hidden></section>`）
- Modify: `src/main/webapp/themes/bootstrap/assets/app.js`（router 登録は Phase 0 で済み）
- Modify: `src/main/webapp/themes/bootstrap/i18n/messages.{en,ja}.json`

**仕様（JSP `advance.jsp` ベース）:**

フォーム項目:
- All these words (q): スペース区切りで AND
- This exact word or phrase ("..."): 引用符で完全一致
- Any of these words (OR): OR 接続
- None of these words (-): NOT
- Site or domain (site:): site フィルタ
- File type: filetype: 選択
- Language: lang フィルタ
- Time period: 時間範囲

送信先: `GET /search?q=<合成クエリ>`

実装スケッチ（createElement のみ、innerHTML 不使用）:

```javascript
// SPDX-License-Identifier: Apache-2.0
import { t } from "./i18n.js";
import { api } from "./api.js";
import { navigate } from "./router.js";

function field(labelKey, name, type = "text") {
  const wrap = document.createElement("div");
  wrap.className = "mb-3";
  const lbl = document.createElement("label");
  lbl.className = "form-label";
  lbl.htmlFor = `adv-${name}`;
  lbl.textContent = t(labelKey);
  const inp = document.createElement("input");
  inp.type = type;
  inp.id = `adv-${name}`;
  inp.name = name;
  inp.className = "form-control";
  wrap.append(lbl, inp);
  return { wrap, inp };
}

function quoteIfNeeded(s) {
  s = s.trim();
  if (!s) return "";
  if (/\s/.test(s) && !s.startsWith('"')) return `"${s.replaceAll('"', '\\"')}"`;
  return s;
}

function compose(parts) {
  const out = [];
  if (parts.all) out.push(parts.all.split(/\s+/).filter(Boolean).join(" "));
  if (parts.exact) out.push(quoteIfNeeded(parts.exact));
  if (parts.any) out.push("(" + parts.any.split(/\s+/).filter(Boolean).join(" OR ") + ")");
  if (parts.none) out.push(parts.none.split(/\s+/).filter(Boolean).map(w => "-" + w).join(" "));
  if (parts.site) out.push(`site:${parts.site}`);
  if (parts.filetype) out.push(`filetype:${parts.filetype}`);
  return out.filter(Boolean).join(" ");
}

export function attach() {
  const view = document.getElementById("advance-view");
  if (!view) return;
  while (view.firstChild) view.removeChild(view.firstChild);

  const h1 = document.createElement("h1");
  h1.textContent = t("advance.title");
  view.appendChild(h1);

  const form = document.createElement("form");
  form.id = "advance-form";
  form.noValidate = true;

  const fAll = field("advance.all", "all");
  const fExact = field("advance.exact", "exact");
  const fAny = field("advance.any", "any");
  const fNone = field("advance.none", "none");
  const fSite = field("advance.site", "site");
  const fFiletype = field("advance.filetype", "filetype");

  form.append(
    fAll.wrap, fExact.wrap, fAny.wrap, fNone.wrap, fSite.wrap, fFiletype.wrap,
  );

  const submit = document.createElement("button");
  submit.type = "submit";
  submit.className = "btn btn-primary";
  submit.textContent = t("advance.submit");
  form.appendChild(submit);

  view.appendChild(form);

  form.addEventListener("submit", (e) => {
    e.preventDefault();
    const q = compose({
      all: fAll.inp.value,
      exact: fExact.inp.value,
      any: fAny.inp.value,
      none: fNone.inp.value,
      site: fSite.inp.value,
      filetype: fFiletype.inp.value,
    });
    navigate(`/search?q=${encodeURIComponent(q)}`);
  });
}
```

- [ ] **Step 1: advance.js 新規作成**
- [ ] **Step 2: index.html に advance-view セクション追加**
- [ ] **Step 3: i18n キー追加（advance.title, advance.all, advance.exact, advance.any, advance.none, advance.site, advance.filetype, advance.submit）**
- [ ] **Step 4: 手動検証 — `/advance` でフォーム表示、送信で `/search?q=...` に遷移して結果表示**
- [ ] **Step 5: コミット** `feat(theme): add advanced search query builder at /advance`

---

## Phase 12: Chat 機能パリティ確認

### Task 12.1: 既存 chat.js と JSP chat.jsp の差分監査

**Files:**
- Read: `src/main/webapp/themes/bootstrap/assets/chat.js`
- Read: `src/main/webapp/WEB-INF/view/chat/chat.jsp`

**仕様:**
- 機能比較表を `docs/superpowers/plans/chat-parity-audit.md` に書き出す。
- フィルタパネル、メッセージログ（user/assistant）、sources、ストリーミング、リトライ、SSE エラーハンドリング、入力フォーム、ラベル選択。
- 差分がある場合は Task 12.2 以降に個別タスクを起こす。

- [ ] **Step 1: 監査レポート作成**
- [ ] **Step 2: 差分があれば Phase 12 を拡張、なければ完了とする**
- [ ] **Step 3: コミット** `docs(theme): audit chat feature parity between SPA and JSP`

### Task 12.2: 不足機能の補完

差分が発見された場合に追加実装。詳細は監査結果次第。

---

## Phase 13: アクセシビリティ監査と修正

### Task 13.1: WCAG 2.1 AA 観点での監査

**Files:**
- すべての theme HTML/JS

**観点:**
- すべてのフォーム要素に `<label>` が紐付いている (`htmlFor` / `aria-label`)
- 動的更新領域に `aria-live="polite"` or `role="status"`
- モーダル: `role="dialog"`, `aria-modal`, focus trap
- キーボード操作: tab 順序、Enter / Esc サポート
- フォーカスリング: `:focus-visible` で 2px outline 表示
- カラーコントラスト: 4.5:1（テキスト）, 3:1（UI）
- 画像: `alt` 属性（装飾なら空）

- [ ] **Step 1: axe DevTools or pa11y で自動検査**
- [ ] **Step 2: 検出された違反を一括修正**
- [ ] **Step 3: コミット** `fix(theme): address accessibility violations from automated audit`

---

## Self-Review Checklist

このセクションは計画作成者が最終レビュー時に消化する。

### 1. Spec Coverage（最初の比較レポートで挙がった項目）

| 項目 | 対応タスク |
|------|-----------|
| ソートオプション 14 種 | Task 1.1 + 3.1 |
| 件数 (num) セレクト | Task 1.1 + 3.2 |
| 言語フィルタ | Task 1.1 + 3.3 |
| ラベルフィルタ複数選択 | Task 1.1 + 3.4 |
| サムネイル | Task 1.2 + 2.2 |
| ファイルサイズ表示 | Task 2.3 |
| 日時表示 | Task 2.3 |
| クリック数 | Task 1.1 + 2.3 |
| 類似ドキュメント | Task 1.2 + 2.5 |
| キャッシュ条件 | Task 2.4 |
| ハイライト | Task 2.6 |
| 関連クエリ | Task 1.3 |
| 関連コンテンツ | Task 1.4 |
| timestamp ファセット | Task 4.1 |
| contentLength ファセット | Task 4.2 |
| filetype ファセット | Task 4.3 |
| Admin/Profile/Logout メニュー | Task 5.1 |
| EOL/dev mode 警告 | Task 5.2 |
| 中央検索 (Home view) | Task 5.3 |
| Back to Top | Task 5.4 |
| プロファイル / パスワード変更 | Task 6.1 |
| ヘルプページ | Task 7.1 + 7.2 |
| エラーページ (`/error/*` SPA 委譲) | Task 1.5 + 8.1 + 8.2 + 8.3 |
| i18n 拡張 | Task 9.1 + 9.2 |

### 2. Placeholder スキャン
- "TBD" / "TODO" / "後で実装" の有無 → 0 件確認済
- "詳細は後述" → 0 件確認済
- "適切なエラーハンドリング" のような曖昧表現 → 各タスクに具体的なフィールド・コードを記載

### 3. Type Consistency
- `state.fields.label` → 配列で統一 (Task 3.4 / 4.x で参照)
- `state.facetQueries` → 配列で統一 (Task 4.1 で初出)
- `api.getConfig()` → Task 1.1 で定義したフィールド名と一致
- `t(key, params)` → Task 9.1 で `{num}`, `{size}`, `{count}` プレースホルダ規約に統一
