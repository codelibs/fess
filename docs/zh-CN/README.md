# Fess: 企业搜索服务器
[![Java CI with Maven](https://github.com/codelibs/fess/actions/workflows/maven.yml/badge.svg)](https://github.com/codelibs/fess/actions/workflows/maven.yml)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/gitbucket/gitbucket/blob/master/LICENSE)
![GitHub Release](https://img.shields.io/github/v/release/codelibs/fess)

## 概述

Fess 是一个功能强大且易于部署的企业搜索服务器。您可以在任何可以运行 Java 运行时环境的平台上快速安装和运行 Fess。Fess 根据 [Apache 许可证 2.0](LICENSE) 提供。

Fess 基于 [OpenSearch](https://github.com/opensearch-project/OpenSearch)，但不需要 OpenSearch 的知识或经验。Fess 提供了一个易于使用的管理 GUI，您可以通过浏览器配置系统。Fess 还包含一个爬虫，能够抓取 [Web 服务器](https://fess.codelibs.org/15.3/admin/webconfig-guide.html)、[文件系统](https://fess.codelibs.org/15.3/admin/fileconfig-guide.html)或[数据存储](https://fess.codelibs.org/15.3/admin/dataconfig-guide.html)（如 CSV 或数据库）中的文档。Fess 支持多种文件格式，包括但不限于：Microsoft Office、PDF 和 zip。

*[Fess 网站搜索](https://github.com/codelibs/fess-site-search)* 是 [Google 网站搜索](https://enterprise.google.com/search/products/gss.html)的免费替代品。更多详情请参阅 [FSS JS 生成器文档](https://fss-generator.codelibs.org/docs/manual)。

## 官网

[fess.codelibs.org](https://fess.codelibs.org/)

## 问题/疑问

[discuss.codelibs.org](https://discuss.codelibs.org/c/FessEN/)

## 入门

您可以通过两种方式尝试 Fess。第一种是自行下载并安装，第二种是使用 [Docker](https://www.docker.com/products/docker-engine)。

### 下载并安装/运行

Fess 15.3 现已发布，可在 [发布页面](https://github.com/codelibs/fess/releases "download") 下载。提供三种下载形式：deb、rpm、zip。

以下命令展示了如何使用 zip 下载：

    $ unzip fess-15.3.x.zip
    $ cd fess-15.3.x
    $ ./bin/fess

更多详情请参阅 [安装指南](https://fess.codelibs.org/15.3/install/index.html)。

### Docker

我们在 [ghcr.io](https://github.com/orgs/codelibs/packages) 上提供了 Docker 镜像。我们还在[此存储库](https://github.com/codelibs/docker-fess/tree/master/compose)中提供了 Docker Compose (YAML) 文件。

### 浏览器 UI

- 搜索 UI: http://localhost:8080/

![Search UI](https://fess.codelibs.org/_images/fess_search_result1.png)

- 管理 UI: http://localhost:8080/admin/ （默认用户名/密码为 admin/admin）

![Admin UI](https://fess.codelibs.org/_images/fess_admin_dashboard.png)

您可以在管理 UI 的 (Web、文件、数据存储) 爬虫配置页面中注册爬取目标，然后在 [调度器页面](https://fess.codelibs.org/15.3/admin/scheduler-guide.html)手动启动爬虫。

## 从其他搜索提供商迁移

请参阅 [MIGRATION.md](MIGRATION.md)。

## 数据存储

当前，Fess 支持抓取以下[存储位置和 API](https://fess.codelibs.org/15.3/admin/dataconfig-guide.html)：

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

## 主题

 - [Simple](https://github.com/codelibs/fess-theme-simple)
 - [Classic](https://github.com/codelibs/fess-theme-classic)

## 数据处理

 - [Logger](https://github.com/codelibs/fess-ingest-logger)
 - [NDJSON](https://github.com/codelibs/fess-ingest-ndjson)

## 脚本

 - [Groovy](https://github.com/codelibs/fess-script-groovy)
 - [OGNL](https://github.com/codelibs/fess-script-ognl)

## 开发信息

### 获取源代码

1. 克隆 Fess 的存储库：
    ```
    $ cd ~/workspace
    $ git clone https://github.com/codelibs/fess.git
    ```
    
2. 将克隆的存储库作为 [Maven](https://maven.apache.org/) 项目导入 [Eclipse](https://www.eclipse.org/eclipseide/) 或其他 IDE。

### 设置 OpenSearch 插件

运行 antrun:run 将插件下载到插件目录：

    $ mvn antrun:run

### 运行 Fess

在您的 IDE 上运行或调试 `org.codelibs.fess.FessBoot`，然后访问 http://localhost:8080/

### 构建包

运行 `package` 目标，然后在 target/releases 中创建发布文件。

    $ mvn package
    $ mvn rpm:rpm   # .rpm 包
    $ mvn jdeb:jdeb # .deb 包

### 生成源代码

    $ mvn dbflute:download # （仅运行一次）
    $ mvn dbflute:freegen
    $ mvn license:format

### 集成测试

启动 Fess 服务器并运行以下命令：

    $ mvn test -P integrationTests -Dtest.fess.url="http://localhost:8080" -Dtest.search_engine.url="http://localhost:9201"

要运行单个测试用例，您可以使用：

    $ mvn test -P integrationTests -Dtest.fess.url="http://localhost:8080" -Dtest.search_engine.url="http://localhost:9201" -Dtest=SearchApiTests

### 翻译为您的语言

Fess 是国际化软件。

如果您想为您的语言添加标签/消息，请翻译属性文件，然后将其重命名为 fess\_\*\_[lang].properties。

* [fess_label_en.properties](https://github.com/codelibs/fess/blob/master/src/main/resources/fess_label_en.properties)
* [fess_message_en.properties](https://github.com/codelibs/fess/blob/master/src/main/resources/fess_message_en.properties)

对于搜索/索引分析器，如果 [doc.json](https://github.com/codelibs/fess/blob/master/src/main/resources/fess_indices/fess/doc.json) 包含您语言的 lang\_[lang]，请为您的语言修改分析器。有关分析器的更多详细信息，请参阅 [OpenSearch 文档](https://opensearch.org/docs/latest/analyzers/search-analyzers/)。

我们欢迎您的语言的 Pull Request。

### 翻译

- [English](../../README.md)
- [日本語 (Japanese)](../ja/README.md)
- [Español (Spanish)](../es/README.md)
- [Português (Brazilian Portuguese)](../pt-BR/README.md)
- [Français (French)](../fr/README.md)
- [Deutsch (German)](../de/README.md)
- [한국어 (Korean)](../ko/README.md)

## 技术支持

* [Lasta Di](https://github.com/lastaflute/lasta-di "Lasta Di"): DI 容器
* [LastaFlute](https://github.com/lastaflute/lastaflute "LastaFlute"): Web 框架
* [Lasta Job](https://github.com/lastaflute/lasta-job "Lasta Job"): 任务调度器
* [Fess Crawler](https://github.com/codelibs/fess-crawler "Fess Crawler"): Web 爬虫
* [OpenSearch](https://opensearch.org/ "OpenSearch"): 搜索引擎
