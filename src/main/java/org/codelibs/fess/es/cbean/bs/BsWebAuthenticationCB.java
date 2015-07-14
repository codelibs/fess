package org.codelibs.fess.es.cbean.bs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.es.bsentity.dbmeta.WebAuthenticationDbm;
import org.codelibs.fess.es.cbean.WebAuthenticationCB;
import org.codelibs.fess.es.cbean.cq.WebAuthenticationCQ;
import org.codelibs.fess.es.cbean.cq.bs.BsWebAuthenticationCQ;
import org.dbflute.cbean.ConditionQuery;
import org.elasticsearch.action.count.CountRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.index.query.QueryBuilder;

/**
 * @author FreeGen
 */
public class BsWebAuthenticationCB extends AbstractConditionBean {

    protected BsWebAuthenticationCQ _conditionQuery;

    protected HpSpecification _specification;

    @Override
    public WebAuthenticationDbm asDBMeta() {
        return WebAuthenticationDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "web_authentication";
    }

    @Override
    public boolean hasSpecifiedColumn() {
        return _specification != null;
    }

    @Override
    public ConditionQuery localCQ() {
        return doGetConditionQuery();
    }

    public WebAuthenticationCB acceptPK(String id) {
        assertObjectNotNull("id", id);
        BsWebAuthenticationCB cb = this;
        cb.query().docMeta().setId_Equal(id);
        return (WebAuthenticationCB) this;
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

    public BsWebAuthenticationCQ query() {
        assertQueryPurpose();
        return doGetConditionQuery();
    }

    protected BsWebAuthenticationCQ doGetConditionQuery() {
        if (_conditionQuery == null) {
            _conditionQuery = createLocalCQ();
        }
        return _conditionQuery;
    }

    protected BsWebAuthenticationCQ createLocalCQ() {
        return new WebAuthenticationCQ();
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

        public void columnAuthRealm() {
            doColumn("authRealm");
        }

        public void columnCreatedBy() {
            doColumn("createdBy");
        }

        public void columnCreatedTime() {
            doColumn("createdTime");
        }

        public void columnHostname() {
            doColumn("hostname");
        }

        public void columnId() {
            doColumn("id");
        }

        public void columnParameters() {
            doColumn("parameters");
        }

        public void columnPassword() {
            doColumn("password");
        }

        public void columnPort() {
            doColumn("port");
        }

        public void columnProtocolScheme() {
            doColumn("protocolScheme");
        }

        public void columnUpdatedBy() {
            doColumn("updatedBy");
        }

        public void columnUpdatedTime() {
            doColumn("updatedTime");
        }

        public void columnUsername() {
            doColumn("username");
        }

        public void columnWebConfigId() {
            doColumn("webConfigId");
        }
    }
}
