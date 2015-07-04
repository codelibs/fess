package org.codelibs.fess.es.cbean.bs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.es.bsentity.dbmeta.CrawlingSessionInfoDbm;
import org.codelibs.fess.es.cbean.CrawlingSessionInfoCB;
import org.codelibs.fess.es.cbean.cq.CrawlingSessionInfoCQ;
import org.codelibs.fess.es.cbean.cq.bs.BsCrawlingSessionInfoCQ;
import org.dbflute.cbean.ConditionQuery;
import org.elasticsearch.action.count.CountRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.index.query.QueryBuilder;

/**
 * @author FreeGen
 */
public class BsCrawlingSessionInfoCB extends AbstractConditionBean {

    protected BsCrawlingSessionInfoCQ _conditionQuery;

    protected HpSpecification _specification;

    @Override
    public CrawlingSessionInfoDbm asDBMeta() {
        return CrawlingSessionInfoDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "crawling_session_info";
    }

    @Override
    public boolean hasSpecifiedColumn() {
        return _specification != null;
    }

    @Override
    public ConditionQuery localCQ() {
        return doGetConditionQuery();
    }

    public CrawlingSessionInfoCB acceptPK(String id) {
        assertObjectNotNull("id", id);
        BsCrawlingSessionInfoCB cb = this;
        cb.query().docMeta().setId_Equal(id);
        return (CrawlingSessionInfoCB) this;
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

    public BsCrawlingSessionInfoCQ query() {
        assertQueryPurpose();
        return doGetConditionQuery();
    }

    protected BsCrawlingSessionInfoCQ doGetConditionQuery() {
        if (_conditionQuery == null) {
            _conditionQuery = createLocalCQ();
        }
        return _conditionQuery;
    }

    protected BsCrawlingSessionInfoCQ createLocalCQ() {
        return new CrawlingSessionInfoCQ();
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

        public void columnCrawlingSessionId() {
            doColumn("crawlingSessionId");
        }

        public void columnCreatedTime() {
            doColumn("createdTime");
        }

        public void columnId() {
            doColumn("id");
        }

        public void columnKey() {
            doColumn("key");
        }

        public void columnValue() {
            doColumn("value");
        }
    }
}
