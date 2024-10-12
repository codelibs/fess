# Fess: Enterprise-Suchserver
[![Java CI with Maven](https://github.com/codelibs/fess/actions/workflows/maven.yml/badge.svg)](https://github.com/codelibs/fess/actions/workflows/maven.yml)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.codelibs.fess/fess/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.codelibs.fess/fess)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/gitbucket/gitbucket/blob/master/LICENSE)

## Übersicht

Fess ist ein sehr leistungsstarker und einfach zu implementierender Enterprise-Suchserver. Sie können Fess schnell auf jeder Plattform installieren und ausführen, auf der die Java-Laufzeitumgebung (JRE) läuft. Fess wird unter der [Apache-Lizenz 2.0](LICENSE) bereitgestellt.

Fess basiert auf [OpenSearch](https://github.com/opensearch-project/OpenSearch), aber es ist kein Wissen oder Erfahrung mit OpenSearch erforderlich. Fess bietet eine einfach zu bedienende Administrations-GUI zur Konfiguration des Systems über Ihren Browser.
Fess enthält auch einen Crawler, der Dokumente auf einem [Webserver](https://fess.codelibs.org/14.17/admin/webconfig-guide.html), [Dateisystem](https://fess.codelibs.org/14.17/admin/fileconfig-guide.html) oder [Datenspeicher](https://fess.codelibs.org/14.17/admin/dataconfig-guide.html) (wie CSV oder Datenbank) durchsuchen kann. Viele Dateiformate werden unterstützt, einschließlich (aber nicht beschränkt auf): Microsoft Office, PDF und zip.

*[Fess Site Search](https://github.com/codelibs/fess-site-search)* ist eine kostenlose Alternative zur [Google Site Search](https://enterprise.google.com/search/products/gss.html). Weitere Details finden Sie in der [Dokumentation des FSS JS Generators](https://fss-generator.codelibs.org/docs/manual).

## Website

[fess.codelibs.org](https://fess.codelibs.org/)

## Probleme/Fragen

[discuss.codelibs.org](https://discuss.codelibs.org/c/FessEN/)

## Erste Schritte

Es gibt zwei Möglichkeiten, Fess auszuprobieren. Die erste besteht darin, es selbst herunterzuladen und zu installieren. Die zweite besteht darin, [Docker](https://www.docker.com/products/docker-engine) zu verwenden.

### Herunterladen und Installieren/Ausführen

Fess 14.17 ist jetzt verfügbar und kann auf der [Release-Seite](https://github.com/codelibs/fess/releases "download") heruntergeladen werden. Downloads gibt es in drei Formaten: deb, rpm, zip.

Die folgenden Befehle zeigen, wie der Zip-Download verwendet wird:

    $ unzip fess-14.17.x.zip
    $ cd fess-14.17.x
    $ ./bin/fess

Weitere Informationen finden Sie im [Installationshandbuch](https://fess.codelibs.org/14.17/install/index.html).

### Docker

Wir bieten Docker-Images auf [ghcr.io](https://github.com/orgs/codelibs/packages) an. Wir stellen auch eine Docker Compose (YAML)-Datei in [diesem Repository](https://github.com/codelibs/docker-fess/tree/master/compose) bereit.

### Browser-Benutzeroberfläche

- Suchoberfläche: http://localhost:8080/

![Suchoberfläche](https://fess.codelibs.org/_images/fess_search_result1.png)

- Administrationsoberfläche: http://localhost:8080/admin/ (Standard-Benutzername/Passwort ist admin/admin)

![Administrationsoberfläche](https://fess.codelibs.org/_images/fess_admin_dashboard.png)

Sie können in der Administrationsoberfläche (Web, Datei, Datenspeicher) Crawling-Ziele in den Crawler-Konfigurationsseiten registrieren und den Crawler manuell auf der [Scheduler-Seite](https://fess.codelibs.org/14.17/admin/scheduler-guide.html) starten.

## Migration von einem anderen Suchanbieter

Bitte lesen Sie [MIGRATION.md](MIGRATION.md).

## Datenspeicher

Derzeit unterstützt Fess das Crawlen der folgenden [Speicherorte und APIs](https://fess.codelibs.org/14.17/admin/dataconfig-guide.html):

 - [Confluence/Jira](https://github.com/codelibs/fess-ds-atlassian)
 - [Box](https://github.com/codelibs/fess-ds-box)
 - [CSV](https://github.com/codelibs/fess-ds-csv)
 - [Datenbank](https://github.com/codelibs/fess-ds-db)
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

## Themen

 - [Simple](https://github.com/codelibs/fess-theme-simple)
 - [Classic](https://github.com/codelibs/fess-theme-classic)

## Ingest

 - [Logger](https://github.com/codelibs/fess-ingest-logger)
 - [NDJSON](https://github.com/codelibs/fess-ingest-ndjson)

## Skript

 - [Groovy](https://github.com/codelibs/fess-script-groovy)
 - [OGNL](https://github.com/codelibs/fess-script-ognl)

## Entwicklungsinformationen

### Quellcode abrufen

1. Klonen Sie das Fess-Repository:
    ```
    $ cd ~/workspace
    $ git clone https://github.com/codelibs/fess.git
    ```

2. Importieren Sie das geklonte Repository als ein [Maven](https://maven.apache.org/) Projekt in [Eclipse](https://www.eclipse.org/eclipseide/) oder einer anderen IDE.

### Einrichten von OpenSearch-Plugins

Führen Sie antrun:run aus, um Plugins in das Plugins-Verzeichnis herunterzuladen:

    $ mvn antrun:run

### Fess ausführen

Führen Sie `org.codelibs.fess.FessBoot` in Ihrer IDE aus oder debuggen Sie es und greifen Sie dann auf http://localhost:8080/ zu.

### Paket erstellen

Führen Sie das `package`-Ziel aus, und dann wird die Release-Datei im Verzeichnis target/releases erstellt.

    $ mvn package
    $ mvn rpm:rpm   # .rpm-Paket
    $ mvn jdeb:jdeb # .deb-Paket

### Quellcode generieren

    $ mvn dbflute:download # (einmaliger Befehl)
    $ mvn dbflute:freegen
    $ mvn license:format

### Integrationstests

Starten Sie den Fess-Server und führen Sie den folgenden Befehl aus:

    $ mvn test -P integrationTests -Dtest.fess.url="http://localhost:8080" -Dtest.search_engine.url="http://localhost:9201"

Um einen einzelnen Testfall auszuführen, können Sie verwenden:

    $ mvn test -P integrationTests -Dtest.fess.url="http://localhost:8080" -Dtest.search_engine.url="http://localhost:9201" -Dtest=SearchApiTests

### In Ihre Sprache übersetzen

Fess ist eine internationalisierte Software.

Wenn Sie Labels/Nachrichten für Ihre Sprache hinzufügen möchten, übersetzen Sie bitte die Eigenschaftendatei und benennen Sie sie in fess\_\*\_[lang].properties um.

* [fess_label_en.properties](https://github.com/codelibs/fess/blob/master/src/main/resources/fess_label_en.properties)
* [fess_message_en.properties](https://github.com/codelibs/fess/blob/master/src/main/resources/fess_message_en.properties)

Für den Such-/Index-Analyser: Wenn [doc.json](https://github.com/codelibs/fess/blob/master/src/main/resources/fess_indices/fess/doc.json) lang\_[lang] für Ihre Sprache enthält, passen Sie bitte den Analyser für Ihre Sprache an. Weitere Informationen zu Analysatoren finden Sie in der [OpenSearch-Dokumentation](https://opensearch.org/docs/latest/analyzers/search-analyzers/).

Wir begrüßen Pull-Requests für Ihre Sprache.

## Unterstützt von

* [Lasta Di](https://github.com/lastaflute/lasta-di "Lasta Di"): DI-Container
* [LastaFlute](https://github.com/lastaflute/lastaflute "LastaFlute"): Web-Framework
* [Lasta Job](https://github.com/lastaflute/lasta-job "Lasta Job"): Job-Scheduler
* [Fess Crawler](https://github.com/codelibs/fess-crawler "Fess Crawler"): Web-Crawler
* [OpenSearch](https://opensearch.org/ "OpenSearch"): Suchmaschine
