# Fess: Servidor de Búsqueda Empresarial
[![Java CI with Maven](https://github.com/codelibs/fess/actions/workflows/maven.yml/badge.svg)](https://github.com/codelibs/fess/actions/workflows/maven.yml)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.codelibs.fess/fess/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.codelibs.fess/fess)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/gitbucket/gitbucket/blob/master/LICENSE)

## Resumen

Fess es un servidor de búsqueda empresarial muy potente y fácil de desplegar. Puedes instalar y ejecutar Fess rápidamente en cualquier plataforma que sea capaz de ejecutar el entorno de ejecución de Java (Java Runtime Environment). Fess se distribuye bajo la [Licencia Apache 2.0](LICENSE).

Fess está basado en [OpenSearch](https://github.com/opensearch-project/OpenSearch), pero no se requiere conocimiento o experiencia en OpenSearch. Fess proporciona una GUI de administración fácil de usar para configurar el sistema a través de tu navegador.
Fess también incluye un rastreador (crawler), que puede rastrear documentos en un [servidor web](https://fess.codelibs.org/14.17/admin/webconfig-guide.html), [sistema de archivos](https://fess.codelibs.org/14.17/admin/fileconfig-guide.html), o [almacenamiento de datos](https://fess.codelibs.org/14.17/admin/dataconfig-guide.html) (como un archivo CSV o base de datos). Se admiten muchos formatos de archivos, incluidos (pero no limitados a): Microsoft Office, PDF y zip.

*[Fess Site Search](https://github.com/codelibs/fess-site-search)* es una alternativa gratuita a [Google Site Search](https://enterprise.google.com/search/products/gss.html). Para más detalles, consulta la [documentación de FSS JS Generator](https://fss-generator.codelibs.org/docs/manual).

## Sitio web

[fess.codelibs.org](https://fess.codelibs.org/)

## Problemas/Preguntas

[discuss.codelibs.org](https://discuss.codelibs.org/c/FessEN/)

## Cómo Empezar

Hay dos maneras de probar Fess. La primera es descargar e instalarlo tú mismo. La segunda es usar [Docker](https://www.docker.com/products/docker-engine).

### Descargar e Instalar/Ejecutar

Fess 14.17 ya está disponible y se puede descargar en la [página de lanzamientos](https://github.com/codelibs/fess/releases "download"). Las descargas están disponibles en 3 formatos: deb, rpm y zip.

Los siguientes comandos muestran cómo usar la descarga en formato zip:

    $ unzip fess-14.17.x.zip
    $ cd fess-14.17.x
    $ ./bin/fess

Para más detalles, consulta la [Guía de Instalación](https://fess.codelibs.org/14.17/install/index.html).

### Docker

Proporcionamos imágenes de Docker en [ghcr.io](https://github.com/orgs/codelibs/packages). También proporcionamos un archivo Docker Compose (YAML) en [este repositorio](https://github.com/codelibs/docker-fess/tree/master/compose).

### Interfaz de Usuario en el Navegador

- Interfaz de búsqueda: http://localhost:8080/

![Interfaz de Búsqueda](https://fess.codelibs.org/_images/fess_search_result1.png)

- Interfaz de administración: http://localhost:8080/admin/ (el nombre de usuario/contraseña por defecto es admin/admin)

![Interfaz de Administración](https://fess.codelibs.org/_images/fess_admin_dashboard.png)

Puedes registrar los objetivos de rastreo en la interfaz de administración en las páginas de configuración del rastreador (Web, Archivo, Almacenamiento de Datos), y luego iniciar el rastreador manualmente en la [página del Programador](https://fess.codelibs.org/14.17/admin/scheduler-guide.html).

## Migración desde otro proveedor de búsqueda

Consulta [MIGRATION.md](MIGRATION.md).

## Almacenamiento de Datos

Actualmente, Fess admite el rastreo de las siguientes [ubicaciones de almacenamiento y APIs](https://fess.codelibs.org/14.17/admin/dataconfig-guide.html):

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

## Tema

 - [Simple](https://github.com/codelibs/fess-theme-simple)
 - [Classic](https://github.com/codelibs/fess-theme-classic)

## Ingesta

 - [Logger](https://github.com/codelibs/fess-ingest-logger)
 - [NDJSON](https://github.com/codelibs/fess-ingest-ndjson)

## Script

 - [Groovy](https://github.com/codelibs/fess-script-groovy)
 - [OGNL](https://github.com/codelibs/fess-script-ognl)

## Información de Desarrollo

### Obtener el Código Fuente

1. Clona el repositorio de Fess:
    ```
    $ cd ~/workspace
    $ git clone https://github.com/codelibs/fess.git
    ```

2. Importa el repositorio clonado como un proyecto [Maven](https://maven.apache.org/) en [Eclipse](https://www.eclipse.org/eclipseide/) u otro IDE.

### Configuración de Plugins para OpenSearch

Ejecuta antrun:run para descargar los plugins en el directorio de plugins:

    $ mvn antrun:run

### Ejecutar Fess

Ejecuta o depura `org.codelibs.fess.FessBoot` en tu IDE, y luego accede a http://localhost:8080/

### Construir el Paquete

Ejecuta el objetivo `package` y luego se creará el archivo de lanzamiento en target/releases.

    $ mvn package
    $ mvn rpm:rpm   # Paquete .rpm
    $ mvn jdeb:jdeb # Paquete .deb

### Generar Código Fuente

    $ mvn dbflute:download # (comando ejecutado una sola vez)
    $ mvn dbflute:freegen
    $ mvn license:format

### Pruebas de Integración

Inicia el servidor Fess y ejecuta el siguiente comando:

    $ mvn test -P integrationTests -Dtest.fess.url="http://localhost:8080" -Dtest.search_engine.url="http://localhost:9201"

Para ejecutar un único caso de prueba, puedes usar:

    $ mvn test -P integrationTests -Dtest.fess.url="http://localhost:8080" -Dtest.search_engine.url="http://localhost:9201" -Dtest=SearchApiTests

### Traducir a Tu Idioma

Fess es un software internacionalizado.

Si deseas agregar etiquetas/mensajes para tu idioma, por favor traduce el archivo de propiedades y renómbralo a fess\_\*\_[lang].properties.

* [fess_label_en.properties](https://github.com/codelibs/fess/blob/master/src/main/resources/fess_label_en.properties)
* [fess_message_en.properties](https://github.com/codelibs/fess/blob/master/src/main/resources/fess_message_en.properties)

Para el analizador de búsqueda/índices, si [doc.json](https://github.com/codelibs/fess/blob/master/src/main/resources/fess_indices/fess/doc.json) contiene lang\_[lang] para tu idioma, por favor modifica el analizador para tu idioma. Para más detalles sobre los analizadores, consulta la [documentación de OpenSearch](https://opensearch.org/docs/latest/analyzers/search-analyzers/).

Damos la bienvenida a pull requests para tu idioma.

## Impulsado por

* [Lasta Di](https://github.com/lastaflute/lasta-di "Lasta Di"): Contenedor DI
* [LastaFlute](https://github.com/lastaflute/lastaflute "LastaFlute"): Framework Web
* [Lasta Job](https://github.com/lastaflute/lasta-job "Lasta Job"): Programador de Tareas
* [Fess Crawler](https://github.com/codelibs/fess-crawler "Fess Crawler"): Rastreador Web
* [OpenSearch](https://opensearch.org/ "OpenSearch"): Motor de Búsqueda
