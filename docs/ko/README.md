# Fess: 엔터프라이즈 검색 서버
[![Java CI with Maven](https://github.com/codelibs/fess/actions/workflows/maven.yml/badge.svg)](https://github.com/codelibs/fess/actions/workflows/maven.yml)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/gitbucket/gitbucket/blob/master/LICENSE)
![GitHub Release](https://img.shields.io/github/v/release/codelibs/fess)

## 개요

Fess는 매우 강력하고 쉽게 배포 가능한 엔터프라이즈 검색 서버입니다. 자바 런타임 환경(Java Runtime Environment)을 실행할 수 있는 모든 플랫폼에서 Fess를 빠르게 설치하고 실행할 수 있습니다. Fess는 [Apache License 2.0](LICENSE)에 따라 제공됩니다.

Fess는 [OpenSearch](https://github.com/opensearch-project/OpenSearch)를 기반으로 하지만, OpenSearch에 대한 지식이나 경험은 필요하지 않습니다. Fess는 브라우저를 통해 시스템을 구성할 수 있는 사용하기 쉬운 관리 GUI를 제공합니다. 또한 Fess는 [웹 서버](https://fess.codelibs.org/15.3/admin/webconfig-guide.html), [파일 시스템](https://fess.codelibs.org/15.3/admin/fileconfig-guide.html), 또는 [데이터 저장소](https://fess.codelibs.org/15.3/admin/dataconfig-guide.html)에서 문서를 크롤링할 수 있는 크롤러도 포함하고 있습니다(CSV 또는 데이터베이스와 같은). Microsoft Office, PDF, zip을 포함한 다양한 파일 형식을 지원합니다.

*[Fess 사이트 검색](https://github.com/codelibs/fess-site-search)*은 [Google 사이트 검색](https://enterprise.google.com/search/products/gss.html)의 무료 대안입니다. 자세한 내용은 [FSS JS 생성기 문서](https://fss-generator.codelibs.org/docs/manual)를 참조하십시오.

## 웹사이트

[fess.codelibs.org](https://fess.codelibs.org/)

## 문제/질문

[discuss.codelibs.org](https://discuss.codelibs.org/c/FessEN/)

## 시작하기

Fess를 사용해보는 방법은 두 가지가 있습니다. 첫 번째는 직접 다운로드하여 설치하는 방법이며, 두 번째는 [Docker](https://www.docker.com/products/docker-engine)를 사용하는 방법입니다.

### 다운로드 및 설치/실행

Fess 15.3이 현재 사용 가능하며, [릴리스 페이지](https://github.com/codelibs/fess/releases "download")에서 다운로드할 수 있습니다. 다운로드는 deb, rpm, zip의 세 가지 형식으로 제공됩니다.

다음 명령은 zip 다운로드 사용 방법을 보여줍니다:

    $ unzip fess-15.3.x.zip
    $ cd fess-15.3.x
    $ ./bin/fess

자세한 내용은 [설치 가이드](https://fess.codelibs.org/15.3/install/index.html)를 참조하십시오.

### Docker

우리는 [ghcr.io](https://github.com/orgs/codelibs/packages)에서 Docker 이미지를 제공합니다. 또한 [이 리포지토리](https://github.com/codelibs/docker-fess/tree/master/compose)에 Docker Compose (YAML) 파일도 제공합니다.

### 브라우저 UI

- 검색 UI: http://localhost:8080/

![검색 UI](https://fess.codelibs.org/_images/fess_search_result1.png)

- 관리자 UI: http://localhost:8080/admin/ (기본 사용자 이름/비밀번호는 admin/admin입니다)

![관리자 UI](https://fess.codelibs.org/_images/fess_admin_dashboard.png)

관리자 UI에서는 (웹, 파일, 데이터 저장소) 크롤러 구성 페이지에서 크롤링 대상을 등록한 다음, [스케줄러 페이지](https://fess.codelibs.org/15.3/admin/scheduler-guide.html)에서 크롤러를 수동으로 시작할 수 있습니다.

## 다른 검색 제공자에서의 마이그레이션

[MIGRATION.md](MIGRATION.md)를 참조하십시오.

## 데이터 저장소

현재 Fess는 다음 [저장소 위치 및 API](https://fess.codelibs.org/15.3/admin/dataconfig-guide.html)를 크롤링할 수 있습니다:

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

## 테마

 - [Simple](https://github.com/codelibs/fess-theme-simple)
 - [Classic](https://github.com/codelibs/fess-theme-classic)

## 인게스트

 - [Logger](https://github.com/codelibs/fess-ingest-logger)
 - [NDJSON](https://github.com/codelibs/fess-ingest-ndjson)

## 스크립트

 - [Groovy](https://github.com/codelibs/fess-script-groovy)
 - [OGNL](https://github.com/codelibs/fess-script-ognl)

## 개발 정보

### 소스 코드 얻기

1. Fess 리포지토리를 클론합니다:
    ```
    $ cd ~/workspace
    $ git clone https://github.com/codelibs/fess.git
    ```

2. 클론한 리포지토리를 [Maven](https://maven.apache.org/) 프로젝트로 [Eclipse](https://www.eclipse.org/eclipseide/) 또는 다른 IDE에서 가져옵니다.

### OpenSearch 플러그인 설정

antrun:run을 실행하여 플러그인을 플러그인 디렉토리에 다운로드합니다:

    $ mvn antrun:run

### Fess 실행

IDE에서 `org.codelibs.fess.FessBoot`을 실행하거나 디버그하고 http://localhost:8080/에 접속합니다.

### 패키지 빌드

`package` 목표를 실행하면 릴리스 파일이 target/releases 디렉토리에 생성됩니다.

    $ mvn package
    $ mvn rpm:rpm   # .rpm 패키지
    $ mvn jdeb:jdeb # .deb 패키지

### 소스 코드 생성

    $ mvn dbflute:download # (한 번만 실행)
    $ mvn dbflute:freegen
    $ mvn license:format

### 통합 테스트

Fess 서버를 시작하고 다음 명령을 실행합니다:

    $ mvn test -P integrationTests -Dtest.fess.url="http://localhost:8080" -Dtest.search_engine.url="http://localhost:9201"

단일 테스트 케이스를 실행하려면 다음을 사용할 수 있습니다:

    $ mvn test -P integrationTests -Dtest.fess.url="http://localhost:8080" -Dtest.search_engine.url="http://localhost:9201" -Dtest=SearchApiTests

### 언어 번역

Fess는 국제화된 소프트웨어입니다.

귀하의 언어로 라벨/메시지를 추가하려면 속성 파일을 번역한 후 fess\_\*\_[lang].properties로 이름을 바꾸십시오.

* [fess_label_en.properties](https://github.com/codelibs/fess/blob/master/src/main/resources/fess_label_en.properties)
* [fess_message_en.properties](https://github.com/codelibs/fess/blob/master/src/main/resources/fess_message_en.properties)

검색/인덱스 분석기와 관련해서, [doc.json](https://github.com/codelibs/fess/blob/master/src/main/resources/fess_indices/fess/doc.json)이 귀하의 언어에 대한 lang\_[lang]을 포함하는 경우, 해당 언어에 맞게 분석기를 수정하십시오. 분석기에 대한 자세한 내용은 [OpenSearch 문서](https://opensearch.org/docs/latest/analyzers/search-analyzers/)를 참조하십시오.

귀하의 언어로 된 풀 리퀘스트를 환영합니다.

### 번역

- [English](../../README.md)
- [日本語 (Japanese)](../ja/README.md)
- [简体中文 (Simplified Chinese)](../zh-CN/README.md)
- [Español (Spanish)](../es/README.md)
- [Português (Brazilian Portuguese)](../pt-BR/README.md)
- [Français (French)](../fr/README.md)
- [Deutsch (German)](../de/README.md)

## 제공된 기술

* [Lasta Di](https://github.com/lastaflute/lasta-di "Lasta Di"): DI 컨테이너
* [LastaFlute](https://github.com/lastaflute/lastaflute "LastaFlute"): 웹 프레임워크
* [Lasta Job](https://github.com/lastaflute/lasta-job "Lasta Job"): 작업 스케줄러
* [Fess Crawler](https://github.com/codelibs/fess-crawler "Fess Crawler"): 웹 크롤러
* [OpenSearch](https://opensearch.org/ "OpenSearch"): 검색 엔진
