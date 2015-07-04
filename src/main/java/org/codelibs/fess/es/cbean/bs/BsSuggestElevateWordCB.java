package org.codelibs.fess.es.cbean.bs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.es.bsentity.dbmeta.SuggestElevateWordDbm;
import org.codelibs.fess.es.cbean.SuggestElevateWordCB;
import org.codelibs.fess.es.cbean.cq.SuggestElevateWordCQ;
import org.codelibs.fess.es.cbean.cq.bs.BsSuggestElevateWordCQ;
import org.dbflute.cbean.ConditionQuery;
import org.elasticsearch.action.count.CountRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.index.query.QueryBuilder;

/**
 * @author FreeGen
 */
public class BsSuggestElevateWordCB extends AbstractConditionBean {

    protected BsSuggestElevateWordCQ _conditionQuery;

    protected HpSpecification _specification;

    @Override
    public SuggestElevateWordDbm asDBMeta() {
        return SuggestElevateWordDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "suggest_elevate_word";
    }

    @Override
    public boolean hasSpecifiedColumn() {
        return _specification != null;
    }

    @Override
    public ConditionQuery localCQ() {
        return doGetConditionQuery();
    }

    public SuggestElevateWordCB acceptPK(String id) {
        assertObjectNotNull("id", id);
        BsSuggestElevateWordCB cb = this;
        cb.query().docMeta().setId_Equal(id);
        return (SuggestElevateWordCB) this;
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

    public BsSuggestElevateWordCQ query() {
        assertQueryPurpose();
        return doGetConditionQuery();
    }

    protected BsSuggestElevateWordCQ doGetConditionQuery() {
        if (_conditionQuery == null) {
            _conditionQuery = createLocalCQ();
        }
        return _conditionQuery;
    }

    protected BsSuggestElevateWordCQ createLocalCQ() {
        return new SuggestElevateWordCQ();
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

        public void columnBoost() {
            doColumn("boost");
        }

        public void columnCreatedBy() {
            doColumn("createdBy");
        }

        public void columnCreatedTime() {
            doColumn("createdTime");
        }

        public void columnId() {
            doColumn("id");
        }

        public void columnReading() {
            doColumn("reading");
        }

        public void columnSuggestWord() {
            doColumn("suggestWord");
        }

        public void columnTargetLabel() {
            doColumn("targetLabel");
        }

        public void columnTargetRole() {
            doColumn("targetRole");
        }

        public void columnUpdatedBy() {
            doColumn("updatedBy");
        }

        public void columnUpdatedTime() {
            doColumn("updatedTime");
        }
    }
}
