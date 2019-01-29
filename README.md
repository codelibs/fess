Fess Enterprise Search Server [![Tweet](https://img.shields.io/twitter/url/http/shields.io.svg?style=social)](https://twitter.com/intent/tweet?text=Fess+is+very+powerful+and+easily+deployable+Enterprise+Search+Server.&url=https://github.com/codelibs/fess)
====
[![Build Status](https://travis-ci.org/codelibs/fess.svg?branch=master)](https://travis-ci.org/codelibs/fess) 
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.codelibs.fess/fess/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.codelibs.fess/fess)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/gitbucket/gitbucket/blob/master/LICENSE)

## Overview

Fess is a very powerful and easily deployable Enterprise Search Server. You can quickly install and run Fess on any platform where you can run the Java Runtime Environment. Fess is provided under the [Apache License 2.0](LICENSE).

Fess is based on [Elasticsearch](https://www.elastic.co/products/elasticsearch), but knowledge/experience about Elasticsearch is _not_ required. Fess provides an easy to use Administration GUI to configure the system via your browser.
Fess also contains a Crawler, which can crawl documents on a [web server](https://fess.codelibs.org/12.4/admin/webconfig-guide.html), [file system](https://fess.codelibs.org/12.4/admin/fileconfig-guide.html), or [Data Store](https://fess.codelibs.org/12.4/admin/dataconfig-guide.html) (such as a CSV or database). Many file formats are supported including (but not limited to): Microsoft Office, PDF, and zip.

*Fess Site Search* is a free alternative to [Google Site Search](https://enterprise.google.com/search/products/gss.html). For more details, see the [FSS JS Generator documentation](https://fss-generator.codelibs.org/docs/manual).

## Website

[fess.codelibs.org](https://fess.codelibs.org/)

## Issues/Questions

Please check existing [questions](https://github.com/codelibs/fess/issues?q=label%3Aquestion), and then file an [issue](https://github.com/codelibs/fess/issues "issue") if your question has not been addressed.

## Getting Started

There are 2 ways to try Fess. The first is to download and install yourself. The second is to use [Docker](https://www.docker.com/products/docker-engine).

### Download and Install/Run

Fess 12.4 is now available and can be downloaded on the [Releases page](https://github.com/codelibs/fess/releases "download"). Downloads come in 3 flavors: deb, rpm, zip.

The following commands show how to use the zip download:

    $ unzip fess-12.4.x.zip
    $ cd fess-12.4.x
    $ ./bin/fess

For more details, see the [Installation Guide](https://fess.codelibs.org/12.4/install/index.html).

### Docker

We provide Docker images on [Docker Hub](https://hub.docker.com/r/codelibs/fess/). We also provide a Docker Compose (YAML) file in [this repository](https://github.com/codelibs/docker-fess/tree/master/compose). 

### Browser UI

- Search UI: http://localhost:8080/

![Search UI](https://fess.codelibs.org/_images/fess_search_result1.png)

- Admin UI: http://localhost:8080/admin/ (default username/password is admin/admin)

![Admin UI](https://fess.codelibs.org/_images/fess_admin_dashboard.png)

You can register crawling targets in the Admin UI on the (Web, File, Data Store) crawler configuration pages, and then start the Crawler manually on the [Scheduler page](https://fess.codelibs.org/12.4/admin/scheduler-guide.html).

## Migration from another search provider

Please see [MIGRATION.md](MIGRATION.md).

## X-Pack Support

Please see [fess-xpack](https://github.com/codelibs/fess-xpack).

## Data Store

Currently, Fess supports crawling the following [storage locations and APIs](https://fess.codelibs.org/12.4/admin/dataconfig-guide.html):

 - [Confluence/Jira](https://github.com/codelibs/fess-ds-atlassian)
 - [CSV](https://github.com/codelibs/fess-ds-csv)
 - [Database](https://github.com/codelibs/fess-ds-db)
 - [Elasticsearch](https://github.com/codelibs/fess-ds-elasticsearch)
 - [Gitbucket](https://github.com/codelibs/fess-ds-gitbucket)
 - [G Suite](https://github.com/codelibs/fess-ds-gsuite)
 - [JSON](https://github.com/codelibs/fess-ds-json)
 - [Office 365](https://github.com/codelibs/fess-ds-office365)
 - [Slack](https://github.com/codelibs/fess-ds-slack)

## Localization

### Japanese

 - [Website](https://fess.codelibs.org/ja/)

### Korean

 - [PDF](https://github.com/nocode2k/fess/releases/download/11.0.1-ko/Fess-ko_11.0.1_manual.pdf)
 - [Forum](https://github.com/nocode2k/fess-kr-forum)

## Development Information

### Get Source Code

1. Clone Fess's repository:
    ```
    $ cd ~/workspace
    $ git clone https://github.com/codelibs/fess.git
    ```
    
2. Import the cloned repository as a [Maven](https://maven.apache.org/) project on [Eclipse](https://www.eclipse.org/eclipseide/) or another IDE.

### Setup for Elasticsearch Plugins

Run antrun:run to download plugins into the plugins directory:

    $ mvn antrun:run

### Run Fess

Run or debug org.codelibs.fess.FessBoot on your IDE, and then access http://localhost:8080/

### Build Package

Run the `package` goal and then the release file will be created in target/releases.

    $ mvn package
    $ mvn rpm:rpm   # .rpm package
    $ mvn jdeb:jdeb # .deb package

### Generate Source Code

    $ mvn dbflute:download # (one time command)
    $ mvn dbflute:freegen
    $ mvn license:format

### Integration Tests

Launch Fess Server and run the following command:

    $ mvn test -P integrationTests -Dtest.fess.url="http://localhost:8080" -Dtest.es.url="http://localhost:9201"

To run a single test case, you can use:

    $ mvn test -P integrationTests -Dtest.fess.url="http://localhost:8080" -Dtest.es.url="http://localhost:9201" -Dtest=SearchApiTests

### Translate In Your Language

Fess is internationalized software.

If you want to add labels/messages for your language, please translate properties file and then rename to fess\_\*\_[lang].properties.

* [fess_label_en.properties](https://github.com/codelibs/fess/blob/master/src/main/resources/fess_label_en.properties)
* [fess_message_en.properties](https://github.com/codelibs/fess/blob/master/src/main/resources/fess_message_en.properties)

For search/index analyzer, if [doc.json](https://github.com/codelibs/fess/blob/master/src/main/resources/fess_indices/fess/doc.json) contains lang\_[lang] for your language, please modify the analyzer for your language. For more details about Analyzers, see the [Elasticsearch documentation](https://www.elastic.co/guide/en/elasticsearch/reference/current/analysis-analyzers.html).

We welcome pull requests for your language.

## Powered By

* [Lasta Di](https://github.com/lastaflute/lasta-di "Lasta Di"): DI Container
* [LastaFlute](https://github.com/lastaflute/lastaflute "LastaFlute"): Web Framework
* [Lasta Job](https://github.com/lastaflute/lasta-job "Lasta Job"): Job Scheduler
* [Fess Crawler](https://github.com/codelibs/fess-crawler "Fess Crawler"): Web Crawler
* [Elasticsearch](https://github.com/elastic/elasticsearch "Elasticsearch"): Search Engine
