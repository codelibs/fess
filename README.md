Enterprise Search Server: Fess 
====

## Overview

Fess is very powerful and easily deployable Enterprise Search Server. You can install and run Fess quickly on any platforms, which have Java runtime environment. Fess is provided under Apache license.

Fess is Solr based search server, but knowledge/experience about Solr is NOT needed because of All-in-One Enterprise Search Server. Fess provides Administration GUI to configure the system on your browser. Fess also contains a crawler, which can crawl documents on Web/File System/DB and support many file formats, such as MS Office, pdf and zip.

## Web Sites

 - [English](http://fess.codelibs.org/)
 - [Japanese](http://fess.codelibs.org/ja/)

### Issues/Questions

Please file an [issue](https://github.com/codelibs/fess/issues "issue").
(Japanese forum is [here](https://github.com/codelibs/codelibs-ja-forum "here").)

### Development

First of all, clone Fess's repositories:

    $ cd ~/workspace
    $ git clone https://github.com/codelibs/fess.git
    $ git clone https://github.com/codelibs/fess-db.git
    $ git clone https://github.com/codelibs/fess-suggest.git
    $ git clone https://github.com/codelibs/fess-solr-plugin.git

and then imports them as Maven project.
If you want to re-create H2 database for Fess, runs:

    $ cd fess-db/fess-db-h2
    $ mvn dbflute:download
    $ mvn dbflute:replace-schema
