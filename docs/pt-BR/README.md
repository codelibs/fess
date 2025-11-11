# Fess: Servidor de Busca Empresarial
[![Java CI with Maven](https://github.com/codelibs/fess/actions/workflows/maven.yml/badge.svg)](https://github.com/codelibs/fess/actions/workflows/maven.yml)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/gitbucket/gitbucket/blob/master/LICENSE)
![GitHub Release](https://img.shields.io/github/v/release/codelibs/fess)

## Visão Geral

Fess é um servidor de busca empresarial muito poderoso e fácil de implantar. Você pode instalar e executar o Fess rapidamente em qualquer plataforma que suporte o Java Runtime Environment. O Fess é fornecido sob a [Licença Apache 2.0](LICENSE).

O Fess é baseado no [OpenSearch](https://github.com/opensearch-project/OpenSearch), mas não é necessário ter conhecimento ou experiência com OpenSearch. O Fess fornece uma interface de administração fácil de usar, que permite configurar o sistema através do seu navegador.
O Fess também inclui um rastreador (Crawler), que pode rastrear documentos em um [servidor web](https://fess.codelibs.org/15.3/admin/webconfig-guide.html), [sistema de arquivos](https://fess.codelibs.org/15.3/admin/fileconfig-guide.html) ou [Data Store](https://fess.codelibs.org/15.3/admin/dataconfig-guide.html) (como CSV ou banco de dados). Muitos formatos de arquivos são suportados, incluindo (mas não limitado a): Microsoft Office, PDF e zip.

*[Fess Site Search](https://github.com/codelibs/fess-site-search)* é uma alternativa gratuita ao [Google Site Search](https://enterprise.google.com/search/products/gss.html). Para mais detalhes, veja a [documentação do FSS JS Generator](https://fss-generator.codelibs.org/docs/manual).

## Site

[fess.codelibs.org](https://fess.codelibs.org/)

## Problemas/Perguntas

[discuss.codelibs.org](https://discuss.codelibs.org/c/FessEN/)

## Como Começar

Existem duas maneiras de testar o Fess. A primeira é baixar e instalar você mesmo. A segunda é usar [Docker](https://www.docker.com/products/docker-engine).

### Baixar e Instalar/Executar

O Fess 15.3 já está disponível e pode ser baixado na [página de lançamentos](https://github.com/codelibs/fess/releases "download"). As opções de download incluem: deb, rpm, zip.

Os comandos a seguir mostram como usar o download em formato zip:

    $ unzip fess-15.3.x.zip
    $ cd fess-15.3.x
    $ ./bin/fess

Para mais detalhes, veja o [Guia de Instalação](https://fess.codelibs.org/15.3/install/index.html).

### Docker

Nós fornecemos imagens Docker em [ghcr.io](https://github.com/orgs/codelibs/packages). Também fornecemos um arquivo Docker Compose (YAML) neste [repositório](https://github.com/codelibs/docker-fess/tree/master/compose).

### Interface do Navegador

- Interface de Busca: http://localhost:8080/

![Interface de Busca](https://fess.codelibs.org/_images/fess_search_result1.png)

- Interface de Administração: http://localhost:8080/admin/ (o nome de usuário/senha padrão é admin/admin)

![Interface de Administração](https://fess.codelibs.org/_images/fess_admin_dashboard.png)

Você pode registrar alvos de rastreamento na interface de administração nas páginas de configuração do rastreador (Web, Arquivo, Data Store), e iniciar manualmente o rastreador na [página do Agendador](https://fess.codelibs.org/15.3/admin/scheduler-guide.html).

## Migração de Outro Provedor de Busca

Consulte [MIGRATION.md](MIGRATION.md).

## Data Store

Atualmente, o Fess suporta o rastreamento dos seguintes [locais de armazenamento e APIs](https://fess.codelibs.org/15.3/admin/dataconfig-guide.html):

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

## Temas

 - [Simple](https://github.com/codelibs/fess-theme-simple)
 - [Classic](https://github.com/codelibs/fess-theme-classic)

## Ingestão

 - [Logger](https://github.com/codelibs/fess-ingest-logger)
 - [NDJSON](https://github.com/codelibs/fess-ingest-ndjson)

## Scripts

 - [Groovy](https://github.com/codelibs/fess-script-groovy)
 - [OGNL](https://github.com/codelibs/fess-script-ognl)

## Informações de Desenvolvimento

### Obter Código Fonte

1. Clone o repositório do Fess:
    ```
    $ cd ~/workspace
    $ git clone https://github.com/codelibs/fess.git
    ```

2. Importe o repositório clonado como um projeto [Maven](https://maven.apache.org/) no [Eclipse](https://www.eclipse.org/eclipseide/) ou em outro IDE.

### Configuração de Plugins para OpenSearch

Execute antrun:run para baixar os plugins no diretório de plugins:

    $ mvn antrun:run

### Executar o Fess

Execute ou depure `org.codelibs.fess.FessBoot` no seu IDE, e depois acesse http://localhost:8080/

### Criar Pacote

Execute o objetivo `package` e o arquivo de lançamento será criado em target/releases.

    $ mvn package
    $ mvn rpm:rpm   # pacote .rpm
    $ mvn jdeb:jdeb # pacote .deb

### Gerar Código Fonte

    $ mvn dbflute:download # (comando executado uma vez)
    $ mvn dbflute:freegen
    $ mvn license:format

### Testes de Integração

Inicie o servidor Fess e execute o seguinte comando:

    $ mvn test -P integrationTests -Dtest.fess.url="http://localhost:8080" -Dtest.search_engine.url="http://localhost:9201"

Para executar um único caso de teste, você pode usar:

    $ mvn test -P integrationTests -Dtest.fess.url="http://localhost:8080" -Dtest.search_engine.url="http://localhost:9201" -Dtest=SearchApiTests

### Traduzir para o Seu Idioma

Fess é um software internacionalizado.

Se você quiser adicionar rótulos/mensagens para o seu idioma, por favor, traduza o arquivo de propriedades e renomeie-o para fess\_\*\_[lang].properties.

* [fess_label_en.properties](https://github.com/codelibs/fess/blob/master/src/main/resources/fess_label_en.properties)
* [fess_message_en.properties](https://github.com/codelibs/fess/blob/master/src/main/resources/fess_message_en.properties)

Para o analisador de busca/índice, se [doc.json](https://github.com/codelibs/fess/blob/master/src/main/resources/fess_indices/fess/doc.json) contiver lang\_[lang] para o seu idioma, por favor, modifique o analisador para o seu idioma. Para mais detalhes sobre Analisadores, veja a [documentação do OpenSearch](https://opensearch.org/docs/latest/analyzers/search-analyzers/).

Aceitamos pull requests para o seu idioma.

### Traduções

- [English](../../README.md)
- [日本語 (Japanese)](../ja/README.md)
- [简体中文 (Simplified Chinese)](../zh-CN/README.md)
- [Español (Spanish)](../es/README.md)
- [Français (French)](../fr/README.md)
- [Deutsch (German)](../de/README.md)
- [한국어 (Korean)](../ko/README.md)

## Desenvolvido por

* [Lasta Di](https://github.com/lastaflute/lasta-di "Lasta Di"): Contêiner DI
* [LastaFlute](https://github.com/lastaflute/lastaflute "LastaFlute"): Framework Web
* [Lasta Job](https://github.com/lastaflute/lasta-job "Lasta Job"): Agendador de Tarefas
* [Fess Crawler](https://github.com/codelibs/fess-crawler "Fess Crawler"): Rastreador Web
* [OpenSearch](https://opensearch.org/ "OpenSearch"): Motor de Busca
