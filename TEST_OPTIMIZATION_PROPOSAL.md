# テスト実行時間の最適化提案

## 現状分析

### 実行時間の内訳
- **FailureUrlTests**: 42.74秒
- **BadWordTests**: 15.46秒
- **JobLogTests**: 46.78秒
- **CrawlerLogTests**: 57.09秒（1件失敗）
- **SearchApiTests**: 84.90秒

**合計**: 約247秒（4分7秒）

### 時間がかかる主な原因

#### 1. クローラーの実行（最大の原因）
- `@BeforeAll`で実際のWebクロールまたはファイルクロールを実行
- `waitJob()`で最大300秒（5分）まで待機可能
- 外部URL（https://www.codelibs.org/）への実際のHTTPリクエスト
- interval_time=100msでの遅延

#### 2. ポーリング処理
- `waitJob()`: 100msごとにスケジューラーの状態をチェック（最大300秒）
- `startJob()`: 100msごとにリトライ（最大300回）
- 一定間隔のポーリングは非効率

#### 3. テストデータ量
- CrudTestBase: NUM=20のCRUD操作
- SearchApiTests: 100件のファイルをクロール

## 最適化案

### 【高効果】1. クローラー設定の最適化

**効果**: 30-50%の時間短縮が期待できる

#### 変更内容:
- `max_access_count`: 100 → **2** （SearchApiTests）
- `max_access_count`: 2 → **1** （その他のテスト）
- クロール対象URLを最小限にする
- 不要な外部URLアクセスを避ける

#### 実装例（SearchApiTests）:
```java
// 現状: max_access_count = 100, depth = 0
requestBody.put("max_access_count", 100);

// 最適化後: 検索テストに必要な最小限のドキュメント数のみ
requestBody.put("max_access_count", 5);  // 検索テストに十分な件数
requestBody.put("depth", 0);              // 既存のまま
```

#### 実装例（FailureUrlTests, JobLogTests）:
```java
// 現状: 外部URL + 失敗URL
final String urls = "https://www.codelibs.org/" + "\n" + "http://failure.url";

// 最適化後: ローカルまたはモックURLのみ
final String urls = "http://localhost:8080/dummy" + "\n" + "http://failure.url";
requestBody.put("max_access_count", 1);  // 最小限
```

### 【中効果】2. waitJob()のポーリング最適化

**効果**: 5-10%の時間短縮が期待できる

#### 変更内容:
Exponential backoff方式を導入し、初期はすぐにチェック、徐々に間隔を広げる

#### 実装例:
```java
protected static void waitJob(final String namePrefix) {
    Boolean isRunning = false;
    int count = 0;
    int sleepTime = 50;  // 初期は50ms

    while (count < 1500 && !isRunning) {
        ThreadUtil.sleep(sleepTime);
        count++;
        final Map<String, Object> scheduler = getSchedulerItem(namePrefix);
        isRunning = (Boolean) scheduler.get("running");

        // Exponential backoff: 最大500msまで
        if (sleepTime < 500 && count % 10 == 0) {
            sleepTime = Math.min(sleepTime * 2, 500);
        }
    }

    // 同様に終了待機も最適化
    count = 0;
    sleepTime = 100;
    while (count < 3000 && isRunning) {
        ThreadUtil.sleep(sleepTime);
        count++;
        isRunning = (Boolean) getSchedulerItem(namePrefix).get("running");

        // より頻繁にチェック（クローラは通常すぐ終わる）
        if (sleepTime < 1000 && count % 5 == 0) {
            sleepTime = Math.min(sleepTime * 1.5, 1000);
        }
    }
}
```

### 【中効果】3. テストデータ量の削減

**効果**: 10-15%の時間短縮が期待できる

#### CrudTestBase:
```java
// 現状
protected static final int NUM = 20;

// 最適化後: テストの網羅性を保ちつつ削減
protected static final int NUM = 5;  // CRUD機能の検証には十分
```

#### SearchApiTests:
```java
// 現状: testWithSort, testWithRange等で100件要求
params.put("num", "100");

// 最適化後: テストに必要な最小限の件数
params.put("num", "10");  // ソートやレンジの検証には10件で十分
```

### 【低効果】4. 並列テスト実行（注意が必要）

**効果**: 理論上は大幅短縮だが、リスクあり

#### JUnit 5の並列実行設定:
```properties
# src/test/resources/junit-platform.properties
junit.jupiter.execution.parallel.enabled = true
junit.jupiter.execution.parallel.mode.default = concurrent
junit.jupiter.execution.parallel.mode.classes.default = concurrent
```

#### 注意点:
- OpenSearchへの同時書き込みで競合する可能性
- テスト間のデータ汚染リスク
- **推奨**: まず上記1-4を実施し、それでも不十分な場合のみ検討

## 最適化の優先順位

### Phase 1（即座に実施可能）
1. ✅ クローラー設定の最小化（設定値変更のみ）
2. ✅ テストデータ量の削減（NUM値変更）

### Phase 2（テスト後に実施）
3. ✅ waitJob()のポーリング最適化（ロジック変更）

### Phase 3（慎重に検討）
4. ⚠️ 並列実行（リスクあり、最後の手段）

## 期待される効果

### 控えめな見積もり:
- Phase 1: **35-45%短縮** → 約135-160秒（2.2-2.7分）
- Phase 2: **追加5-10%短縮** → 約120-140秒（2-2.3分）

### 最適化後の目標:
**合計実行時間**: 247秒 → **120-140秒**（約40-50%短縮）

## 実装内容

### 実装済みの最適化

#### 1. クローラー設定の最適化
- **max_access_count削減**: 100 → 20（SearchApiTests）、2（CrawlerLogTests）、1（JobLogTests, FailureUrlTests）
- **interval_time削減**: 100ms → 0ms（リクエスト間の遅延なし）
- **外部URLは維持**: テストの安定性のため、https://www.codelibs.org/を使用し続ける

#### 2. waitJob()のExponential Backoff
- クローラー起動待機: 50ms → 最大300msまで段階的に増加
- クローラー終了待機: 100ms → 最大500msまで段階的に増加
- 効率的なポーリングで無駄な待機時間を削減

#### 3. テストデータ量の削減
- CrudTestBase NUM: 20 → 10
- CRUD機能の検証には10件で十分

### 検討したが実装しなかった最適化

#### refresh()呼び出しの削減（却下）
- OpenSearchのeventual consistencyの問題により、refresh()は必要
- 各書き込み操作後にrefresh()しないと、後続の読み取りで不整合が発生する可能性
- テスト挙動の変更リスクが高いため見送り

#### 外部URLからlocalhostへの変更（却下）
- 当初、ネットワーク遅延削減のため、https://www.codelibs.org/ → http://localhost:8080/への変更を試みた
- しかし、Fessサーバー自体をクロールすると予期しない動作が発生
- テストの安定性を優先し、元の外部URLを維持
- max_access_count削減とinterval_time削減で十分な時間短縮が達成できる

## テストの網羅性維持

以下の点を確認することで、テストの網羅性を維持：

### ✅ 機能カバレッジ
- すべてのAPIエンドポイント（GET, POST, PUT, DELETE）をテスト
- パラメータのバリエーション（検索、ページング、フィルタリング）をテスト
- エラーケースをテスト

### ✅ データバリエーション
- NUM=5でも、CRUD操作の基本動作は十分検証可能
- ページングテストは5件で十分（1件ずつ取得して5ページ確認）

### ✅ 検索パターン
- SearchApiTestsの13個のテストケースはすべて維持
- データ量を減らしても検索機能の検証は可能

## 実装順序の推奨

1. **まず簡単なものから**:
   - クローラー設定の変更（設定値のみ）
   - NUM値の変更（容易に戻せる）

2. **効果測定**:
   - 各変更後にテスト実行時間を計測
   - 失敗するテストがないか確認

3. **段階的に適用**:
   - 1つのテストクラスで検証
   - 問題なければ他のテストクラスにも適用

4. **最終検証**:
   - すべての最適化を適用した状態でCI環境でテスト
   - カバレッジレポートで網羅性を確認

## 適用された最適化の詳細

### ファイル別の変更内容

#### FailureUrlTests.java
- `max_access_count`: 1（既に最小化済み）
- `interval_time`: 0ms（遅延なし）
- コメント追加で意図を明確化

#### JobLogTests.java
- `max_access_count`: 1（変更なし）
- `interval_time`: 0ms（100ms → 0ms）
- URL: https://www.codelibs.org/を維持（テスト安定性のため）

#### CrawlerLogTests.java
- `max_access_count`: 2（変更なし）
- `interval_time`: 0ms（100ms → 0ms）
- URL: https://www.codelibs.org/を維持（テスト安定性のため）

#### SearchApiTests.java
- `max_access_count`: 100 → 20
- `interval_time`: 100ms → 0ms

#### CrudTestBase.java
- `NUM`: 20 → 10

#### CrawlTestBase.java
- `waitJob()`にexponential backoffを実装
- クローラー起動待機: 50ms開始、300msまで段階増加
- クローラー終了待機: 100ms開始、500msまで段階増加
