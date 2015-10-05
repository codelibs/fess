package org.codelibs.fess.es.cbean.bs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.es.bsentity.dbmeta.EventLogDbm;
import org.codelibs.fess.es.cbean.EventLogCB;
import org.codelibs.fess.es.cbean.cq.EventLogCQ;
import org.codelibs.fess.es.cbean.cq.bs.BsEventLogCQ;
import org.dbflute.cbean.ConditionQuery;
import org.elasticsearch.action.count.CountRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.index.query.QueryBuilder;

/**
 * @author FreeGen
 */
public class BsEventLogCB extends AbstractConditionBean {

    protected BsEventLogCQ _conditionQuery;

    protected HpSpecification _specification;

    @Override
    public EventLogDbm asDBMeta() {
        return EventLogDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "event_log";
    }

    @Override
    public boolean hasSpecifiedColumn() {
        return _specification != null;
    }

    @Override
    public ConditionQuery localCQ() {
        return doGetConditionQuery();
    }

    public EventLogCB acceptPK(String id) {
        assertObjectNotNull("id", id);
        BsEventLogCB cb = this;
        cb.query().docMeta().setId_Equal(id);
        return (EventLogCB) this;
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

    public BsEventLogCQ query() {
        assertQueryPurpose();
        return doGetConditionQuery();
    }

    protected BsEventLogCQ doGetConditionQuery() {
        if (_conditionQuery == null) {
            _conditionQuery = createLocalCQ();
        }
        return _conditionQuery;
    }

    protected BsEventLogCQ createLocalCQ() {
        return new EventLogCQ();
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

        public void columnCreatedAt() {
            doColumn("createdAt");
        }

        public void columnCreatedBy() {
            doColumn("createdBy");
        }

        public void columnEventType() {
            doColumn("eventType");
        }

        public void columnId() {
            doColumn("id");
        }

        public void columnMessage() {
            doColumn("message");
        }

        public void columnPath() {
            doColumn("path");
        }
    }
}
