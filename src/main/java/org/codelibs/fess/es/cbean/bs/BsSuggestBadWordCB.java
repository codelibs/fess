package org.codelibs.fess.es.cbean.bs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.es.bsentity.dbmeta.SuggestBadWordDbm;
import org.codelibs.fess.es.cbean.SuggestBadWordCB;
import org.codelibs.fess.es.cbean.cq.SuggestBadWordCQ;
import org.codelibs.fess.es.cbean.cq.bs.BsSuggestBadWordCQ;
import org.dbflute.cbean.ConditionQuery;
import org.elasticsearch.action.count.CountRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.index.query.QueryBuilder;

/**
 * @author FreeGen
 */
public class BsSuggestBadWordCB extends AbstractConditionBean {

    protected BsSuggestBadWordCQ _conditionQuery;

    protected HpSpecification _specification;

    @Override
    public SuggestBadWordDbm asDBMeta() {
        return SuggestBadWordDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "suggest_bad_word";
    }

    @Override
    public boolean hasSpecifiedColumn() {
        return _specification != null;
    }

    @Override
    public ConditionQuery localCQ() {
        return doGetConditionQuery();
    }

    public SuggestBadWordCB acceptPK(String id) {
        assertObjectNotNull("id", id);
        BsSuggestBadWordCB cb = this;
        cb.query().docMeta().setId_Equal(id);
        return (SuggestBadWordCB) this;
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

    public BsSuggestBadWordCQ query() {
        assertQueryPurpose();
        return doGetConditionQuery();
    }

    protected BsSuggestBadWordCQ doGetConditionQuery() {
        if (_conditionQuery == null) {
            _conditionQuery = createLocalCQ();
        }
        return _conditionQuery;
    }

    protected BsSuggestBadWordCQ createLocalCQ() {
        return new SuggestBadWordCQ();
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

        public void columnCreatedBy() {
            doColumn("createdBy");
        }

        public void columnCreatedTime() {
            doColumn("createdTime");
        }

        public void columnId() {
            doColumn("id");
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
