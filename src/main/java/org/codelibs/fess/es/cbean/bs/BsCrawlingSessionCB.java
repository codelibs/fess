package org.codelibs.fess.es.cbean.bs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.es.bsentity.dbmeta.CrawlingSessionDbm;
import org.codelibs.fess.es.cbean.CrawlingSessionCB;
import org.codelibs.fess.es.cbean.cq.CrawlingSessionCQ;
import org.codelibs.fess.es.cbean.cq.bs.BsCrawlingSessionCQ;
import org.dbflute.cbean.ConditionQuery;
import org.elasticsearch.action.count.CountRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.index.query.QueryBuilder;

/**
 * @author FreeGen
 */
public class BsCrawlingSessionCB extends AbstractConditionBean {

    protected BsCrawlingSessionCQ _conditionQuery;

    protected HpSpecification _specification;

    @Override
    public CrawlingSessionDbm asDBMeta() {
        return CrawlingSessionDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "crawling_session";
    }

    @Override
    public boolean hasSpecifiedColumn() {
        return _specification != null;
    }

    @Override
    public ConditionQuery localCQ() {
        return doGetConditionQuery();
    }

    public CrawlingSessionCB acceptPK(String id) {
        assertObjectNotNull("id", id);
        BsCrawlingSessionCB cb = this;
        cb.query().docMeta().setId_Equal(id);
        return (CrawlingSessionCB) this;
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

    public BsCrawlingSessionCQ query() {
        assertQueryPurpose();
        return doGetConditionQuery();
    }

    protected BsCrawlingSessionCQ doGetConditionQuery() {
        if (_conditionQuery == null) {
            _conditionQuery = createLocalCQ();
        }
        return _conditionQuery;
    }

    protected BsCrawlingSessionCQ createLocalCQ() {
        return new CrawlingSessionCQ();
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

        public void columnCreatedTime() {
            doColumn("createdTime");
        }

        public void columnExpiredTime() {
            doColumn("expiredTime");
        }

        public void columnId() {
            doColumn("id");
        }

        public void columnName() {
            doColumn("name");
        }

        public void columnSessionId() {
            doColumn("sessionId");
        }
    }
}
