package org.codelibs.fess.es.cbean.bs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.es.bsentity.dbmeta.FailureUrlDbm;
import org.codelibs.fess.es.cbean.FailureUrlCB;
import org.codelibs.fess.es.cbean.cq.FailureUrlCQ;
import org.codelibs.fess.es.cbean.cq.bs.BsFailureUrlCQ;
import org.dbflute.cbean.ConditionQuery;
import org.elasticsearch.action.count.CountRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.index.query.QueryBuilder;

/**
 * @author FreeGen
 */
public class BsFailureUrlCB extends AbstractConditionBean {

    protected BsFailureUrlCQ _conditionQuery;

    protected HpSpecification _specification;

    @Override
    public FailureUrlDbm asDBMeta() {
        return FailureUrlDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "failure_url";
    }

    @Override
    public boolean hasSpecifiedColumn() {
        return _specification != null;
    }

    @Override
    public ConditionQuery localCQ() {
        return doGetConditionQuery();
    }

    public FailureUrlCB acceptPK(String id) {
        assertObjectNotNull("id", id);
        BsFailureUrlCB cb = this;
        cb.query().docMeta().setId_Equal(id);
        return (FailureUrlCB) this;
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

    public BsFailureUrlCQ query() {
        assertQueryPurpose();
        return doGetConditionQuery();
    }

    protected BsFailureUrlCQ doGetConditionQuery() {
        if (_conditionQuery == null) {
            _conditionQuery = createLocalCQ();
        }
        return _conditionQuery;
    }

    protected BsFailureUrlCQ createLocalCQ() {
        return new FailureUrlCQ();
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

        public void columnConfigId() {
            doColumn("configId");
        }

        public void columnErrorCount() {
            doColumn("errorCount");
        }

        public void columnErrorLog() {
            doColumn("errorLog");
        }

        public void columnErrorName() {
            doColumn("errorName");
        }

        public void columnId() {
            doColumn("id");
        }

        public void columnLastAccessTime() {
            doColumn("lastAccessTime");
        }

        public void columnThreadName() {
            doColumn("threadName");
        }

        public void columnUrl() {
            doColumn("url");
        }
    }
}
