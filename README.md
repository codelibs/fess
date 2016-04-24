Enterprise Search Server: Fess 
====

## Overview

Fess is very powerful and easily deployable Enterprise Search Server. You can install and run Fess quickly on any platforms, which have Java runtime environment. Fess is provided under Apache license.

Fess is Elasticsearch based search server, but knowledge/experience about Elasticsearch is NOT needed because of All-in-One Enterprise Search Server. Fess provides Administration GUI to configure the system on your browser. Fess also contains a crawler, which can crawl documents on Web/FileSystem/DB and supports many file formats, such as MS Office, pdf and zip.

## Web Sites

 - [English](http://fess.codelibs.org/)
 - [Japanese](http://fess.codelibs.org/ja/)

## Issues/Questions

Please file an [issue](https://github.com/codelibs/fess/issues "issue").
(Japanese forum is [here](https://github.com/codelibs/codelibs-ja-forum "here").)

## Getting Started

### Download

Fess 10.0 is available.
The release page is [HERE](https://github.com/codelibs/fess/releases "download").

### Install/Run Fess

    $ unzip fess-10.0.x.zip
    $ cd fess-10.0.x
    $ ./bin/fess

For the details, see [Installation Guide](http://fess.codelibs.org/10.0/install/index.html).

### Access Fess

- Search UI: http://localhost:8080/
- Admin UI: http://localhost:8080/admin/ (username/password is admin/admin)

You can register crawling targets on Web/File System/Data Store of admin pages, and then start Crawler on Scheduler page manually.

## Fess on Docker Hub

We provide Docker image on Docker Hub. 
For more details, see [Public Repository](https://hub.docker.com/r/codelibs/fess/).

## Development Information

### Get Source Code

First of all, clone Fess's repositories:

    $ cd ~/workspace
    $ git clone https://github.com/codelibs/fess.git

and then imports it as Maven project on eclipse or ohter IDE.

### Setup for Elasticsearch Plugins

Run antrun:run to download plugins into plugins directory:

    $ mvn antrun:run

### Run Fess

Run or debug org.codelibs.fess.FessBoot on IDE, and then access http://localhost:8080/fess/

### Build Package

Run package goal and then the release file is created in target/releases.

    $ mvn package

### Generate Source Code

    $ mvn dbflute:download # (one time command)
    $ mvn dbflute:freegen
    $ mvn license:format

## Localization

Fess is internationalized software, and supports the following languages at the moment:

* English
* Japanese

If you want to add labels/messages for your language, please translate properties file and then rename to fess\_*_[lang].properties.

* [fess_label_en.properties](https://github.com/codelibs/fess/blob/master/src/main/resources/fess_label_en.properties)
* [fess_message_en.properties](https://github.com/codelibs/fess/blob/master/src/main/resources/fess_message_en.properties)

For search/index analyzer, if [doc.json](https://github.com/codelibs/fess/blob/master/src/main/resources/fess_indices/fess/doc.json) contains lang\_[lang] for your language, please modify analyzer for your language. For more details about elasticsearch's analyzer, see [Analyzers](https://www.elastic.co/guide/en/elasticsearch/reference/current/analysis-analyzers.html).

We are waiting for pull requests of your language.

## Powered By

* [Lasta Di](https://github.com/lastaflute/lasta-di "Lasta Di"): DI Container
* [LastaFlute](https://github.com/lastaflute/lastaflute "LastaFlute"): Web Framework
* [Lasta Job](https://github.com/lastaflute/lasta-job "Lasta Job"): Job Scheduler
* [Fess Crawler](https://github.com/codelibs/fess-crawler "Fess Crawler"): Web Crawler
* [Elasticsearch](https://github.com/elastic/elasticsearch "Elasticsearch"): Search Engine
