package org.codelibs.fess.es.cbean.sqlclause;

import org.dbflute.cbean.sqlclause.AbstractSqlClause;
import org.dbflute.dbway.DBWay;

public class SqlClauseEs extends AbstractSqlClause {

    private static final long serialVersionUID = 1L;

    public SqlClauseEs(String tableDbName) {
        super(tableDbName);
    }

    @Override
    public void lockForUpdate() {
        // TODO Auto-generated method stub

    }

    @Override
    public DBWay dbway() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void doFetchFirst() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void doFetchPage() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void doClearFetchPageClause() {
        // TODO Auto-generated method stub

    }

    @Override
    protected String createSelectHint() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected String createFromBaseTableHint() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected String createFromHint() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected String createSqlSuffix() {
        // TODO Auto-generated method stub
        return null;
    }

}
