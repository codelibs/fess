package org.codelibs.fess.es.cbean.bs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.es.bsentity.dbmeta.ScheduledJobDbm;
import org.codelibs.fess.es.cbean.ScheduledJobCB;
import org.codelibs.fess.es.cbean.cq.ScheduledJobCQ;
import org.codelibs.fess.es.cbean.cq.bs.BsScheduledJobCQ;
import org.dbflute.cbean.ConditionQuery;
import org.elasticsearch.action.count.CountRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.index.query.QueryBuilder;

/**
 * @author FreeGen
 */
public class BsScheduledJobCB extends AbstractConditionBean {

    protected BsScheduledJobCQ _conditionQuery;

    protected HpSpecification _specification;

    @Override
    public ScheduledJobDbm asDBMeta() {
        return ScheduledJobDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "scheduled_job";
    }

    @Override
    public boolean hasSpecifiedColumn() {
        return _specification != null;
    }

    @Override
    public ConditionQuery localCQ() {
        return doGetConditionQuery();
    }

    public ScheduledJobCB acceptPK(String id) {
        assertObjectNotNull("id", id);
        BsScheduledJobCB cb = this;
        cb.query().docMeta().setId_Equal(id);
        return (ScheduledJobCB) this;
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

    public BsScheduledJobCQ query() {
        assertQueryPurpose();
        return doGetConditionQuery();
    }

    protected BsScheduledJobCQ doGetConditionQuery() {
        if (_conditionQuery == null) {
            _conditionQuery = createLocalCQ();
        }
        return _conditionQuery;
    }

    protected BsScheduledJobCQ createLocalCQ() {
        return new ScheduledJobCQ();
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

        public void columnCrawler() {
            doColumn("crawler");
        }

        public void columnCreatedBy() {
            doColumn("createdBy");
        }

        public void columnCreatedTime() {
            doColumn("createdTime");
        }

        public void columnCronExpression() {
            doColumn("cronExpression");
        }

        public void columnId() {
            doColumn("id");
        }

        public void columnJobLogging() {
            doColumn("jobLogging");
        }

        public void columnName() {
            doColumn("name");
        }

        public void columnScriptData() {
            doColumn("scriptData");
        }

        public void columnScriptType() {
            doColumn("scriptType");
        }

        public void columnSortOrder() {
            doColumn("sortOrder");
        }

        public void columnTarget() {
            doColumn("target");
        }

        public void columnUpdatedBy() {
            doColumn("updatedBy");
        }

        public void columnUpdatedTime() {
            doColumn("updatedTime");
        }
    }
}
