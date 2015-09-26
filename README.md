Enterprise Search Server: Fess 
====

## Overview

Fess is very powerful and easily deployable Enterprise Search Server. You can install and run Fess quickly on any platforms, which have Java runtime environment. Fess is provided under Apache license.

Fess is Solr based search server, but knowledge/experience about Solr is NOT needed because of All-in-One Enterprise Search Server. Fess provides Administration GUI to configure the system on your browser. Fess also contains a crawler, which can crawl documents on Web/File System/DB and support many file formats, such as MS Office, pdf and zip.

## Web Sites

 - [English](http://fess.codelibs.org/)
 - [Japanese](http://fess.codelibs.org/ja/)

## Issues/Questions

Please file an [issue](https://github.com/codelibs/fess/issues "issue").
(Japanese forum is [here](https://github.com/codelibs/codelibs-ja-forum "here").)

## Development

### Get Source Code

First of all, clone Fess's repositories:

    $ cd ~/workspace
    $ git clone https://github.com/codelibs/fess.git

and then imports it as Maven project on eclipse or ohter IDE.

### Download Elasticsearch Plugins

Run antrun:run to download plugins into plugins directory:

    $ mvn antrun:run

### Run Fess

Run or debug org.codelibs.fess.FessBoot on IDE, and then access http://localhost:8080/fess/

