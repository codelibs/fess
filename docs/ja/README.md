# Fess: エンタープライズ検索サーバー
[![Java CI with Maven](https://github.com/codelibs/fess/actions/workflows/maven.yml/badge.svg)](https://github.com/codelibs/fess/actions/workflows/maven.yml)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.codelibs.fess/fess/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.codelibs.fess/fess)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/gitbucket/gitbucket/blob/master/LICENSE)

## 概要

Fessは非常に強力で、簡単に導入できるエンタープライズ検索サーバーです。FessはJavaランタイム環境が動作する任意のプラットフォーム上で簡単にインストールして実行できます。Fessは[Apache License 2.0](LICENSE)の下で提供されています。

Fessは[OpenSearch](https://github.com/opensearch-project/OpenSearch)をベースにしていますが、OpenSearchに関する知識や経験は**不要**です。Fessは、ブラウザを介してシステムを簡単に設定できる管理者向けのGUIを提供しています。Fessにはクローラも含まれており、[Webサーバー](https://fess.codelibs.org/14.17/admin/webconfig-guide.html)、[ファイルシステム](https://fess.codelibs.org/14.17/admin/fileconfig-guide.html)、または[データストア](https://fess.codelibs.org/14.17/admin/dataconfig-guide.html)（CSVやデータベースなど）のドキュメントをクロールできます。Microsoft Office、PDF、zipなど、多くのファイル形式に対応しています。

*[Fess Site Search](https://github.com/codelibs/fess-site-search)*は、[Google Site Search](https://enterprise.google.com/search/products/gss.html)の無料代替です。詳細については、[FSS JS Generatorのドキュメント](https://fss-generator.codelibs.org/docs/manual)を参照してください。

## 公式サイト

[fess.codelibs.org](https://fess.codelibs.org/)

## 問題や質問

[discuss.codelibs.org](https://discuss.codelibs.org/c/FessEN/)

## はじめに

Fessを試す方法は2つあります。1つは自分でダウンロードしてインストールする方法、もう1つは[Docker](https://www.docker.com/products/docker-engine)を使用する方法です。

### ダウンロードおよびインストール/実行

Fess 14.17が利用可能で、[リリースページ](https://github.com/codelibs/fess/releases "download")からダウンロードできます。ダウンロードには、deb、rpm、zipの3つの形式があります。

以下のコマンドは、zipファイルを使用する例です：

    $ unzip fess-14.17.x.zip
    $ cd fess-14.17.x
    $ ./bin/fess

詳細については、[インストールガイド](https://fess.codelibs.org/14.17/install/index.html)を参照してください。

### Docker

[ghcr.io](https://github.com/orgs/codelibs/packages)にDockerイメージを提供しています。また、このリポジトリにはDocker Compose（YAML）ファイルも用意されています。[こちらのリポジトリ](https://github.com/codelibs/docker-fess/tree/master/compose)で確認できます。

### ブラウザUI

- 検索UI: http://localhost:8080/

![Search UI](https://fess.codelibs.org/_images/fess_search_result1.png)

- 管理者UI: http://localhost:8080/admin/ （デフォルトのユーザー名/パスワードはadmin/adminです）

![Admin UI](https://fess.codelibs.org/_images/fess_admin_dashboard.png)

管理者UIでは、（Web、ファイル、データストア）クローラの設定ページでクロール対象を登録し、[スケジューラページ](https://fess.codelibs.org/14.17/admin/scheduler-guide.html)から手動でクローラを開始できます。

## 他の検索プロバイダーからの移行

[MIGRATION.md](MIGRATION.md)を参照してください。

## データストア

現在、Fessは以下の[ストレージロケーションとAPI](https://fess.codelibs.org/14.17/admin/dataconfig-guide.html)のクロールをサポートしています：

 - [Confluence/Jira](https://github.com/codelibs/fess-ds-atlassian)
 - [Box](https://github.com/codelibs/fess-ds-box)
 - [CSV](https://github.com/codelibs/fess-ds-csv)
 - [Database](https://github.com/codelibs/fess-ds-db)
 - [Dropbox](https://github.com/codelibs/fess-ds-dropbox)
 - [Elasticsearch](https://github.com/codelibs/fess-ds-elasticsearch)
 - [Git](https://github.com/codelibs/fess-ds-git)
 - [Gitbucket](https://github.com/codelibs/fess-ds-gitbucket)
 - [G Suite](https://github.com/codelibs/fess-ds-gsuite)
 - [JSON](https://github.com/codelibs/fess-ds-json)
 - [Office 365](https://github.com/codelibs/fess-ds-office365)
 - [S3](https://github.com/codelibs/fess-ds-s3)
 - [Salesforce](https://github.com/codelibs/fess-ds-salesforce)
 - [SharePoint](https://github.com/codelibs/fess-ds-sharepoint)
 - [Slack](https://github.com/codelibs/fess-ds-slack)

## テーマ

 - [Simple](https://github.com/codelibs/fess-theme-simple)
 - [Classic](https://github.com/codelibs/fess-theme-classic)

## Ingest

 - [Logger](https://github.com/codelibs/fess-ingest-logger)
 - [NDJSON](https://github.com/codelibs/fess-ingest-ndjson)

## スクリプト

 - [Groovy](https://github.com/codelibs/fess-script-groovy)
 - [OGNL](https://github.com/codelibs/fess-script-ognl)

## 開発情報

### ソースコードの取得

1. Fessのリポジトリをクローンします：
    ```
    $ cd ~/workspace
    $ git clone https://github.com/codelibs/fess.git
    ```
    
2. クローンしたリポジトリを、[Maven](https://maven.apache.org/)プロジェクトとして[Eclipse](https://www.eclipse.org/eclipseide/)や他のIDEにインポートします。

### OpenSearchプラグインのセットアップ

antrun:runを実行して、プラグインをpluginsディレクトリにダウンロードします：

    $ mvn antrun:run

### Fessの実行

IDE上で`org.codelibs.fess.FessBoot`を実行またはデバッグし、http://localhost:8080/ にアクセスします。

### パッケージのビルド

`package`ゴールを実行すると、releaseファイルがtarget/releasesディレクトリに作成されます。

    $ mvn package
    $ mvn rpm:rpm   # .rpmパッケージ
    $ mvn jdeb:jdeb # .debパッケージ

### ソースコードの生成

    $ mvn dbflute:download # （一度だけ実行）
    $ mvn dbflute:freegen
    $ mvn license:format

### 統合テスト

Fessサーバーを起動し、以下のコマンドを実行します：

    $ mvn test -P integrationTests -Dtest.fess.url="http://localhost:8080" -Dtest.search_engine.url="http://localhost:9201"

単一のテストケースを実行するには、以下のコマンドを使用できます：

    $ mvn test -P integrationTests -Dtest.fess.url="http://localhost:8080" -Dtest.search_engine.url="http://localhost:9201" -Dtest=SearchApiTests

### あなたの言語への翻訳

Fessは国際化対応ソフトウェアです。

ラベルやメッセージをあなたの言語に追加したい場合、プロパティファイルを翻訳してから`fess\_\*\_[lang].properties`という名前に変更してください。

* [fess_label_en.properties](https://github.com/codelibs/fess/blob/master/src/main/resources/fess_label_en.properties)
* [fess_message_en.properties](https://github.com/codelibs/fess/blob/master/src/main/resources/fess_message_en.properties)

検索/インデックスアナライザーの設定については、[doc.json](https://github.com/codelibs/fess/blob/master/src/main/resources/fess_indices/fess/doc.json)に言語別の`lang\_[lang]`が含まれているか確認し、その言語に合わせてアナライザーを修正してください。アナライザーに関する詳細は、[OpenSearchのドキュメント](https://opensearch.org/docs/latest/analyzers/search-analyzers/)を参照してください。

私たちは、あなたの言語でのプルリクエストを歓迎します。

## Powered By

* [Lasta Di](https://github.com/lastaflute/lasta-di "Lasta Di"): DIコンテナ
* [LastaFlute](https://github.com/lastaflute/lastaflute "LastaFlute"): Webフレームワーク
* [Lasta Job](https://github.com/lastaflute/lasta-job "Lasta Job"): ジョブスケジューラ
* [Fess Crawler](https://github.com/codelibs/fess-crawler "Fess Crawler"): Webクローラ
* [OpenSearch](https://opensearch.org/ "OpenSearch"): 検索エンジン
