Fess Enterprise Search Server [![Tweet](https://img.shields.io/twitter/url/http/shields.io.svg?style=social)](https://twitter.com/intent/tweet?text=Fess+is+very+powerful+and+easily+deployable+Enterprise+Search+Server.&url=https://github.com/codelibs/fess)
====
[![Java CI avec Maven](https://github.com/codelibs/fess/actions/workflows/maven.yml/badge.svg)](https://github.com/codelibs/fess/actions/workflows/maven.yml)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.codelibs.fess/fess/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.codelibs.fess/fess)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/gitbucket/gitbucket/blob/master/LICENSE)

## Overview

Fess est un serveur de moteur de recherche très puissant et facile à déployer en entreprise.
Vous pouvez rapidement l'installer et l'exécuter sur toute plateforme qui propose l'environnement  Java Runtime. Fess est distribué sous [License Apache 2.0](LICENSE).

Fess est basé sur  [OpenSearch](https://github.com/opensearch-project/OpenSearch)/[Elasticsearch](https://github.com/elastic/elasticsearch), mais il _ne_ requiert _PAS_ connaissance ou expérience avec les solutions OpenSearch et Elasticsearch. Fess fourni une interface d'administration simple pour configurer le système via votre navigateur.
Fess comprend aussi un Crawler, qui peut explorer les documents d'un [serveur Web](https://fess.codelibs.org/14.6/admin/webconfig-guide.html), un [système de fichiers](https://fess.codelibs.org/14.6/admin/fileconfig-guide.html), ou un [Stockage de données](https://fess.codelibs.org/14.6/admin/dataconfig-guide.html) (tel qu'une base de donnée ou des CSV). Plusieurs formats de fichiers sont supportés tels que (mais pas uniquement): Microsoft Office, PDF, et zip.

*[FEss Site Search](https://github.com/codelibs/fess-site-search)* est une alternative libre à [Google Site Search](https://enterprise.google.com/search/products/gss.html). Pour plus de détails, jetez un oeil à [FSS JS Generator documentation](https://fss-generator.codelibs.org/docs/manual).

## Website

[fess.codelibs.org](https://fess.codelibs.org/)

## Incidents/Questions

[discuss.codelibs.org](https://discuss.codelibs.org/c/FessEN/)

## Pour démarrer

Il y a 2 façons d'essayer Fess. La première est de télécharger et l'installer vous-même. La seconde étant d'utiliser [Docker](https://www.docker.com/products/docker-engine).

### Télécharger, installer, lancer

La dernière version de Fess est maintenant disponible et peut être téléchargée sur la [page des releases](https://github.com/codelibs/fess/releases "download"). 3 formats vous sont proposés : deb, rpm, zip.

Les commandes suivantes montre comment utiliser la version zip:

    $ unzip fess-14.6.x.zip
    $ cd fess-14.6.x
    $ ./bin/fess

Pour plus de détails, consultez le [guide d'installation](https://fess.codelibs.org/14.6/install/index.html).

### Docker

Nous fournissions des images docker sur [ghcr.io](https://github.com/orgs/codelibs/packages).
Nous proposons aussi un fichier Docker Compose (YAML) dans [ce repository](https://github.com/codelibs/docker-fess/tree/master/compose).

### Interfaces Web

- Interface de recherche : http://localhost:8080/

![Interface de recherche](https://fess.codelibs.org/_images/fess_search_result1.png)

- Interface d'administration : http://localhost:8080/admin/ (identifiant et mot de passe à l'installation : admin/admin)

![Interface d'administration](https://fess.codelibs.org/_images/fess_admin_dashboard.png)

Vous pouvez définir les cibles à indexer (Web, Fichiers, stockage de données) dans les pages de configuration de l'exploration de l'interface d'administration, et lancer manuellement l'exploration sur la [page de planification](https://fess.codelibs.org/14.6/admin/scheduler-guide.html).

## Migration depuis un autre moteur de recherche

Détaillé sur la page [MIGRATION.md](MIGRATION.md).

## Stockages de données

Actuellement, Fess supporte l'exploration des [emplacements de stockage et APIs](https://fess.codelibs.org/14.6/admin/dataconfig-guide.html) suivantes:

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

## Thèmes

 - [Simple](https://github.com/codelibs/fess-theme-simple)
 - [Classic](https://github.com/codelibs/fess-theme-classic)

## Ingest

 - [Logger](https://github.com/codelibs/fess-ingest-logger)
 - [NDJSON](https://github.com/codelibs/fess-ingest-ndjson)

## Script

 - [Groovy](https://github.com/codelibs/fess-script-groovy)
 - [OGNL](https://github.com/codelibs/fess-script-ognl)

## Localisation

### Japonnaise

 - [Website](https://fess.codelibs.org/ja/)

### Coréenne

 - [Forum](https://github.com/nocode2k/fess-kr-forum)

## Information de Développement

### Récuperer le code Source

1. Cloner le dépôt Fess:
    ```
    $ cd ~/workspace
    $ git clone https://github.com/codelibs/fess.git
    ```

2. Importer le dépot cloné comme projet [Maven](https://maven.apache.org/) dans [Eclipse](https://www.eclipse.org/eclipseide/) ou un autre IDE.

### Mettre en place les plugins OpenSearch

Exécuter antrun:run pour récupérer les plugins dans le répertoire des plugins :

    $ mvn antrun:run

### Éxecuter Fess

Exécuter ou debugger org.codelibs.fess.FessBoot dans votre IDE, et aller sur http://localhost:8080/

### Construire le Package

Éxecuter le `package` et ensuite livrer le fichier créé dans target/releases.

    $ mvn package
    $ mvn rpm:rpm   # .rpm package
    $ mvn jdeb:jdeb # .deb package

### Générer le code Source

    $ mvn dbflute:download # (one time command)
    $ mvn dbflute:freegen
    $ mvn license:format

### Test d'integration

Lancer le serveur Fess et éxécuter la commande suivante :

    $ mvn test -P integrationTests -Dtest.fess.url="http://localhost:8080" -Dtest.search_engine.url="http://localhost:9201"

Pour exécuter un seul cas de test case, vous pouvez utiliser la commande suivante :

    $ mvn test -P integrationTests -Dtest.fess.url="http://localhost:8080" -Dtest.search_engine.url="http://localhost:9201" -Dtest=SearchApiTests

### Traduire dans d'autres (votre) langue

Fess est internationalisé.

Si vous voulez les messages et labels dans votre langue, vous pouvez dupliquer et renommer en suivant le format fess\_\*\_[lang].properties et en traduire le contenu.

* [fess_label_en.properties](https://github.com/codelibs/fess/blob/master/src/main/resources/fess_label_en.properties)
* [fess_message_en.properties](https://github.com/codelibs/fess/blob/master/src/main/resources/fess_message_en.properties)

Pour l'analyseur de recherche et indexation, si [doc.json](https://github.com/codelibs/fess/blob/master/src/main/resources/fess_indices/fess/doc.json) contient lang\_[lang] pour votre langue, veuillez modifier l'analyseur pour votre langue. Pour plus d'information à propos des Analyzers, veuillez consulter la [documentaion d'Elasticsearch](https://www.elastic.co/guide/en/elasticsearch/reference/current/analysis-analyzers.html).

Nous acceuillons avec plaisir les pull request pour votre langue.

## Powered By

* [Lasta Di](https://github.com/lastaflute/lasta-di "Lasta Di"): Conteneur DI
* [LastaFlute](https://github.com/lastaflute/lastaflute "LastaFlute"): Framework Web
* [Lasta Job](https://github.com/lastaflute/lasta-job "Lasta Job"): Planificateur de tâches
* [Fess Crawler](https://github.com/codelibs/fess-crawler "Fess Crawler"): Explorateur Web
* [OpenSearch](https://opensearch.org/ "OpenSearch"): Moteur de recherche
* [Elasticsearch](https://github.com/elastic/elasticsearch "Elasticsearch"): Moteur de recherche
