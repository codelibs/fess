
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
    $ mvn rpm:rpm   # .rpm package
    $ mvn jdeb:jdeb # .deb package

### Generate Source Code

    $ mvn antrun:run # (one time command)
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
