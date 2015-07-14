package org.codelibs.fess.es.cbean.bs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.es.bsentity.dbmeta.WebConfigDbm;
import org.codelibs.fess.es.cbean.WebConfigCB;
import org.codelibs.fess.es.cbean.cq.WebConfigCQ;
import org.codelibs.fess.es.cbean.cq.bs.BsWebConfigCQ;
import org.dbflute.cbean.ConditionQuery;
import org.elasticsearch.action.count.CountRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.index.query.QueryBuilder;

/**
 * @author FreeGen
 */
public class BsWebConfigCB extends AbstractConditionBean {

    protected BsWebConfigCQ _conditionQuery;

    protected HpSpecification _specification;

    @Override
    public WebConfigDbm asDBMeta() {
        return WebConfigDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "web_config";
    }

    @Override
    public boolean hasSpecifiedColumn() {
        return _specification != null;
    }

    @Override
    public ConditionQuery localCQ() {
        return doGetConditionQuery();
    }

    public WebConfigCB acceptPK(String id) {
        assertObjectNotNull("id", id);
        BsWebConfigCB cb = this;
        cb.query().docMeta().setId_Equal(id);
        return (WebConfigCB) this;
    }

    @Override
    public void acceptPrimaryKeyMap(Map<String, ? extends Object> primaryKeyMap) {
        acceptPK((String) primaryKeyMap.get("_id"));
    }

    @Override
    public CountRequestBuilder build(CountRequestBuilder builder) {
        if (_conditionQuery != null) {
            QueryBuilder queryBuilder = _conditionQuery.getQuery();
            if (queryBuilder != null) {
                builder.setQuery(queryBuilder);
            }
        }
        return builder;
    }

    @Override
    public SearchRequestBuilder build(SearchRequestBuilder builder) {
        if (_conditionQuery != null) {
            QueryBuilder queryBuilder = _conditionQuery.getQuery();
            if (queryBuilder != null) {
                builder.setQuery(queryBuilder);
            }
            _conditionQuery.getFieldSortBuilderList().forEach(sort -> {
                builder.addSort(sort);
            });
        }

        if (_specification != null) {
            builder.setFetchSource(_specification.columnList.toArray(new String[_specification.columnList.size()]), null);
        }

        return builder;
    }

    public BsWebConfigCQ query() {
        assertQueryPurpose();
        return doGetConditionQuery();
    }

    protected BsWebConfigCQ doGetConditionQuery() {
        if (_conditionQuery == null) {
            _conditionQuery = createLocalCQ();
        }
        return _conditionQuery;
    }

    protected BsWebConfigCQ createLocalCQ() {
        return new WebConfigCQ();
    }

    public HpSpecification specify() {
        assertSpecifyPurpose();
        if (_specification == null) {
            _specification = new HpSpecification();
        }
        return _specification;
    }

    protected void assertQueryPurpose() {
        // TODO
    }

    protected void assertSpecifyPurpose() {
        // TODO
    }

    public static class HpSpecification {
        private List<String> columnList = new ArrayList<>();

        private void doColumn(String name) {
            columnList.add(name);
        }

        public void columnAvailable() {
            doColumn("available");
        }

        public void columnBoost() {
            doColumn("boost");
        }

        public void columnConfigParameter() {
            doColumn("configParameter");
        }

        public void columnCreatedBy() {
            doColumn("createdBy");
        }

        public void columnCreatedTime() {
            doColumn("createdTime");
        }

        public void columnDepth() {
            doColumn("depth");
        }

        public void columnExcludedDocUrls() {
            doColumn("excludedDocUrls");
        }

        public void columnExcludedUrls() {
            doColumn("excludedUrls");
        }

        public void columnId() {
            doColumn("id");
        }

        public void columnIncludedDocUrls() {
            doColumn("includedDocUrls");
        }

        public void columnIncludedUrls() {
            doColumn("includedUrls");
        }

        public void columnIntervalTime() {
            doColumn("intervalTime");
        }

        public void columnMaxAccessCount() {
            doColumn("maxAccessCount");
        }

        public void columnName() {
            doColumn("name");
        }

        public void columnNumOfThread() {
            doColumn("numOfThread");
        }

        public void columnSortOrder() {
            doColumn("sortOrder");
        }

        public void columnUpdatedBy() {
            doColumn("updatedBy");
        }

        public void columnUpdatedTime() {
            doColumn("updatedTime");
        }

        public void columnUrls() {
            doColumn("urls");
        }

        public void columnUserAgent() {
            doColumn("userAgent");
        }
    }
}
