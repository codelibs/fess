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

