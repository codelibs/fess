package org.codelibs.fess.es.cbean.bs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.es.bsentity.dbmeta.SearchLogDbm;
import org.codelibs.fess.es.cbean.SearchLogCB;
import org.codelibs.fess.es.cbean.cq.SearchLogCQ;
import org.codelibs.fess.es.cbean.cq.bs.BsSearchLogCQ;
import org.dbflute.cbean.ConditionQuery;
import org.elasticsearch.action.count.CountRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.index.query.QueryBuilder;

/**
 * @author FreeGen
 */
public class BsSearchLogCB extends AbstractConditionBean {

    protected BsSearchLogCQ _conditionQuery;

    protected HpSpecification _specification;

    @Override
    public SearchLogDbm asDBMeta() {
        return SearchLogDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "search_log";
    }

    @Override
    public boolean hasSpecifiedColumn() {
        return _specification != null;
    }

    @Override
    public ConditionQuery localCQ() {
        return doGetConditionQuery();
    }

    public SearchLogCB acceptPK(String id) {
        assertObjectNotNull("id", id);
        BsSearchLogCB cb = this;
        cb.query().docMeta().setId_Equal(id);
        return (SearchLogCB) this;
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

    public BsSearchLogCQ query() {
        assertQueryPurpose();
        return doGetConditionQuery();
    }

    protected BsSearchLogCQ doGetConditionQuery() {
        if (_conditionQuery == null) {
            _conditionQuery = createLocalCQ();
        }
        return _conditionQuery;
    }

    protected BsSearchLogCQ createLocalCQ() {
        return new SearchLogCQ();
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

        public void columnAccessType() {
            doColumn("accessType");
        }

        public void columnClientIp() {
            doColumn("clientIp");
        }

        public void columnHitCount() {
            doColumn("hitCount");
        }

        public void columnId() {
            doColumn("id");
        }

        public void columnQueryOffset() {
            doColumn("queryOffset");
        }

        public void columnQueryPageSize() {
            doColumn("queryPageSize");
        }

        public void columnReferer() {
            doColumn("referer");
        }

        public void columnRequestedTime() {
            doColumn("requestedTime");
        }

        public void columnResponseTime() {
            doColumn("responseTime");
        }

        public void columnSearchWord() {
            doColumn("searchWord");
        }

        public void columnUserAgent() {
            doColumn("userAgent");
        }

        public void columnUserId() {
            doColumn("userId");
        }

        public void columnUserSessionId() {
            doColumn("userSessionId");
        }
    }
}
