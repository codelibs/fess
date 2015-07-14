package org.codelibs.fess.es.cbean.bs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.es.bsentity.dbmeta.JobLogDbm;
import org.codelibs.fess.es.cbean.JobLogCB;
import org.codelibs.fess.es.cbean.cq.JobLogCQ;
import org.codelibs.fess.es.cbean.cq.bs.BsJobLogCQ;
import org.dbflute.cbean.ConditionQuery;
import org.elasticsearch.action.count.CountRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.index.query.QueryBuilder;

/**
 * @author FreeGen
 */
public class BsJobLogCB extends AbstractConditionBean {

    protected BsJobLogCQ _conditionQuery;

    protected HpSpecification _specification;

    @Override
    public JobLogDbm asDBMeta() {
        return JobLogDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "job_log";
    }

    @Override
    public boolean hasSpecifiedColumn() {
        return _specification != null;
    }

    @Override
    public ConditionQuery localCQ() {
        return doGetConditionQuery();
    }

    public JobLogCB acceptPK(String id) {
        assertObjectNotNull("id", id);
        BsJobLogCB cb = this;
        cb.query().docMeta().setId_Equal(id);
        return (JobLogCB) this;
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

    public BsJobLogCQ query() {
        assertQueryPurpose();
        return doGetConditionQuery();
    }

    protected BsJobLogCQ doGetConditionQuery() {
        if (_conditionQuery == null) {
            _conditionQuery = createLocalCQ();
        }
        return _conditionQuery;
    }

    protected BsJobLogCQ createLocalCQ() {
        return new JobLogCQ();
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

        public void columnEndTime() {
            doColumn("endTime");
        }

        public void columnId() {
            doColumn("id");
        }

        public void columnJobName() {
            doColumn("jobName");
        }

        public void columnJobStatus() {
            doColumn("jobStatus");
        }

        public void columnScriptData() {
            doColumn("scriptData");
        }

        public void columnScriptResult() {
            doColumn("scriptResult");
        }

        public void columnScriptType() {
            doColumn("scriptType");
        }

        public void columnStartTime() {
            doColumn("startTime");
        }

        public void columnTarget() {
            doColumn("target");
        }
    }
}
