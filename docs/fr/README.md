# Fess : Serveur de recherche pour entreprises
[![Java CI with Maven](https://github.com/codelibs/fess/actions/workflows/maven.yml/badge.svg)](https://github.com/codelibs/fess/actions/workflows/maven.yml)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.codelibs.fess/fess/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.codelibs.fess/fess)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/gitbucket/gitbucket/blob/master/LICENSE)

## Vue d'ensemble

Fess est un serveur de recherche très puissant et facilement déployable pour les entreprises. Vous pouvez rapidement installer et exécuter Fess sur toute plateforme où vous pouvez exécuter l'environnement d'exécution Java. Fess est fourni sous la licence [Apache License 2.0](LICENSE).

Fess est basé sur [OpenSearch](https://github.com/opensearch-project/OpenSearch), mais aucune connaissance ni expérience d'OpenSearch n'est _nécessaire_. Fess fournit une interface d'administration facile à utiliser pour configurer le système via votre navigateur.
Fess comprend également un Crawler, capable d'explorer les documents sur un [serveur web](https://fess.codelibs.org/14.17/admin/webconfig-guide.html), un [système de fichiers](https://fess.codelibs.org/14.17/admin/fileconfig-guide.html), ou un [Data Store](https://fess.codelibs.org/14.17/admin/dataconfig-guide.html) (comme un fichier CSV ou une base de données). De nombreux formats de fichiers sont pris en charge, y compris (mais sans s'y limiter) : Microsoft Office, PDF, et zip.

*[Fess Site Search](https://github.com/codelibs/fess-site-search)* est une alternative gratuite à [Google Site Search](https://enterprise.google.com/search/products/gss.html). Pour plus de détails, consultez la [documentation FSS JS Generator](https://fss-generator.codelibs.org/docs/manual).

## Site Web

[fess.codelibs.org](https://fess.codelibs.org/)

## Problèmes/Questions

[discuss.codelibs.org](https://discuss.codelibs.org/c/FessEN/)

## Démarrage rapide

Il existe 2 manières d'essayer Fess. La première est de le télécharger et l'installer vous-même. La seconde est d'utiliser [Docker](https://www.docker.com/products/docker-engine).

### Télécharger et Installer/Exécuter

Fess 14.17 est désormais disponible et peut être téléchargé sur la [page de Releases](https://github.com/codelibs/fess/releases "download"). Les téléchargements sont disponibles en 3 formats : deb, rpm, zip.

Les commandes suivantes montrent comment utiliser le téléchargement zip :

    $ unzip fess-14.17.x.zip
    $ cd fess-14.17.x
    $ ./bin/fess

Pour plus de détails, consultez le [guide d'installation](https://fess.codelibs.org/14.17/install/index.html).

### Docker

Nous fournissons des images Docker sur [ghcr.io](https://github.com/orgs/codelibs/packages). Nous fournissons également un fichier Docker Compose (YAML) dans [ce dépôt](https://github.com/codelibs/docker-fess/tree/master/compose). 

### Interface Web

- Interface de recherche : http://localhost:8080/

![Search UI](https://fess.codelibs.org/_images/fess_search_result1.png)

- Interface d'administration : http://localhost:8080/admin/ (nom d'utilisateur/mot de passe par défaut : admin/admin)

![Admin UI](https://fess.codelibs.org/_images/fess_admin_dashboard.png)

Vous pouvez enregistrer des cibles à explorer dans l'interface d'administration sur les pages de configuration des crawlers (Web, Fichiers, Data Store), puis démarrer manuellement le Crawler sur la [page du Planificateur](https://fess.codelibs.org/14.17/admin/scheduler-guide.html).

## Migration depuis un autre fournisseur de recherche

Veuillez consulter [MIGRATION.md](MIGRATION.md).

## Data Store

Actuellement, Fess prend en charge le parcours des [emplacements de stockage et API](https://fess.codelibs.org/14.17/admin/dataconfig-guide.html) suivants :

 - [Confluence/Jira](https://github.com/codelibs/fess-ds-atlassian)
 - [Box](https://github.com/codelibs/fess-ds-box)
 - [CSV](https://github.com/codelibs/fess-ds-csv)
 - [Base de données](https://github.com/codelibs/fess-ds-db)
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
 - [Classique](https://github.com/codelibs/fess-theme-classic)

## Ingest

 - [Logger](https://github.com/codelibs/fess-ingest-logger)
 - [NDJSON](https://github.com/codelibs/fess-ingest-ndjson)

## Scripts

 - [Groovy](https://github.com/codelibs/fess-script-groovy)
 - [OGNL](https://github.com/codelibs/fess-script-ognl)

## Localisation

### Japonais

 - [Site Web](https://fess.codelibs.org/ja/)

### Coréen

 - [Forum](https://github.com/nocode2k/fess-kr-forum)

## Informations de Développement

### Récupérer le Code Source

1. Clonez le dépôt Fess :
    ```
    $ cd ~/workspace
    $ git clone https://github.com/codelibs/fess.git
    ```
    
2. Importez le dépôt cloné en tant que projet [Maven](https://maven.apache.org/) sur [Eclipse](https://www.eclipse.org/eclipseide/) ou un autre IDE.

### Configuration pour les Plugins OpenSearch

Exécutez antrun:run pour télécharger les plugins dans le répertoire plugins :

    $ mvn antrun:run

### Exécuter Fess

Exécutez ou déboguez org.codelibs.fess.FessBoot dans votre IDE, puis accédez à http://localhost:8080/

### Créer un Package

Exécutez la commande `package` et le fichier de release sera créé dans target/releases.

    $ mvn package
    $ mvn rpm:rpm   # package .rpm
    $ mvn jdeb:jdeb # package .deb

### Générer le Code Source

    $ mvn dbflute:download # (commande unique)
    $ mvn dbflute:freegen
    $ mvn license:format

### Tests d'Intégration

Lancez le serveur Fess et exécutez la commande suivante :

    $ mvn test -P integrationTests -Dtest.fess.url="http://localhost:8080" -Dtest.search_engine.url="http://localhost:9201"

Pour exécuter un seul cas de test, vous pouvez utiliser :

    $ mvn test -P integrationTests -Dtest.fess.url="http://localhost:8080" -Dtest.search_engine.url="http://localhost:9201" -Dtest=SearchApiTests

### Traduire dans Votre Langue

Fess est un logiciel internationalisé.

Si vous souhaitez ajouter des libellés/messages pour votre langue, veuillez traduire le fichier de propriétés et le renommer en fess\_\*\_[lang].properties.

* [fess_label_en.properties](https://github.com/codelibs/fess/blob/master/src/main/resources/fess_label_en.properties)
* [fess_message_en.properties](https://github.com/codelibs/fess/blob/master/src/main/resources/fess_message_en.properties)

Pour l'analyseur de recherche/index, si [doc.json](https://github.com/codelibs/fess/blob/master/src/main/resources/fess_indices/fess/doc.json) contient lang\_[lang] pour votre langue, veuillez modifier l'analyseur pour votre langue. Pour plus de détails sur les analyseurs, consultez la [documentation OpenSearch](https://opensearch.org/docs/latest/analyzers/search-analyzers/).

Nous accueillons les pull requests pour votre langue.

## Alimenté par

* [Lasta Di](https://github.com/lastaflute/lasta-di "Lasta Di") : Conteneur DI
* [LastaFlute](https://github.com/lastaflute/lastaflute "LastaFlute") : Framework Web
* [Lasta Job](https://github.com/lastaflute/lasta-job "Lasta Job") : Planificateur de tâches
* [Fess Crawler](https://github.com/codelibs/fess-crawler "Fess Crawler") : Crawler Web
* [OpenSearch](https://opensearch.org/ "OpenSearch") : Moteur de recherche
