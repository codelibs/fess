# Templates of the DBFlute engine

These are the `esflute` templates (`templates/om/java/plugin/esflute` of the DBFlute engine, 20 files) that generate
the classes under `src/main/java/org/codelibs/fess/opensearch`, kept here so that they can be changed with the code
they generate. `dbflute.xml` (target `dbflute.templates`, run by `mvn antrun:run`) downloads the release distribution of
DBFlute (`dbflute-<version>.zip` of https://github.com/dbflute/dbflute-core/releases, the `dbflute.version` of
`pom.xml`, as `dbflute-runtime`) and replaces the `esflute` directory of its `templates` with this one. The other
templates are those of the release; Fess does not generate anything from them.

- Source: `lastaflute/lastaflute-example-waterfront`, branch `fess-15.9`, commit
  `c12b3a95bcec704a2fd9f92feca48fd4377c5276`, `mydbflute/dbflute-1.x/templates/om/java/plugin/esflute` (DBFlute 1.2.8,
  Apache License 2.0). The `esflute` templates of the DBFlute releases 1.2.8 and 1.3.1 are the same files and equal
  these but for `allcommon/EsAbstractBehavior.vm`, where the branch searches over a point in time and the releases scroll.
- Changes made here:
  - `allcommon/EsAbstractConditionBean.vm`: `SearchRequestParams#version` is `Boolean.TRUE` by default, so that a search
    returns the `_version` of a document. The admin API compares the `version_no` of an update with it.
  - `allcommon/EsAbstractBehavior.vm`: the point-in-time search sorts by `_index` after `_shard_doc`, as the
    committed `EsAbstractBehavior` does.
  - Every `org.opensearch.` is written `org.codelibs.fesen.opensearch.`, the packages that Fess depends on (Fess does
    not depend on `org.opensearch:opensearch`).
- `LICENSE` and `NOTICE` are those of the engine (Apache License 2.0); `dbflute.xml` does not copy them, nor this file, into the engine.

To generate again, run `mvn antrun:run` (it downloads the engine when `mydbflute` does not hold the release of the
`dbflute.version`, and puts the templates in place), then `mvn dbflute:freegen` (or `sh manage.sh freegen` in
`dbflute_fess`; both use `mydbflute/dbflute-1.x`; the engine needs a Gson jar in `dbflute_fess/extlib`), then
`mvn formatter:format license:format`. The generated classes compile. They differ from the committed ones in the order
and wildcards of the imports, and by what the committed code lacks because it was not generated again after
`src/main/config/es/*.json` changed (the `metadata` column of the web, file and data store configuration, and
`columnSearchWord` of the click log). Read the diff before taking generated files.
