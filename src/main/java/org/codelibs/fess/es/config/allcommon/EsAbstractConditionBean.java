/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.codelibs.fess.es.config.allcommon;

import org.dbflute.cbean.ConditionBean;
import org.dbflute.cbean.chelper.HpCBPurpose;
import org.dbflute.cbean.chelper.HpColumnSpHandler;
import org.dbflute.cbean.coption.CursorSelectOption;
import org.dbflute.cbean.coption.ScalarSelectOption;
import org.dbflute.cbean.coption.StatementConfigCall;
import org.dbflute.cbean.dream.SpecifiedColumn;
import org.dbflute.cbean.exception.ConditionBeanExceptionThrower;
import org.dbflute.cbean.ordering.OrderByBean;
import org.dbflute.cbean.paging.PagingBean;
import org.dbflute.cbean.paging.PagingInvoker;
import org.dbflute.cbean.scoping.AndQuery;
import org.dbflute.cbean.scoping.ModeQuery;
import org.dbflute.cbean.scoping.OrQuery;
import org.dbflute.cbean.scoping.UnionQuery;
import org.dbflute.cbean.sqlclause.SqlClause;
import org.dbflute.cbean.sqlclause.orderby.OrderByClause;
import org.dbflute.dbmeta.DBMeta;
import org.dbflute.dbmeta.accessory.DerivedTypeHandler;
import org.dbflute.jdbc.StatementConfig;
import org.dbflute.system.DBFluteSystem;
import org.dbflute.twowaysql.style.BoundDateDisplayStyle;
import org.opensearch.action.search.SearchRequestBuilder;
import org.opensearch.common.unit.TimeValue;

/**
 * @author ESFlute (using FreeGen)
 */
public abstract class EsAbstractConditionBean implements ConditionBean {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final SqlClause _sqlClause = new EsSqlClause(asTableDbName());
    protected int _safetyMaxResultSize;
    protected SearchRequestParams _searchRequestParams = new SearchRequestParams();
    protected String _preference;

    // ===================================================================================
    //                                                                             Builder
    //                                                                             =======
    public abstract SearchRequestBuilder build(SearchRequestBuilder builder);

    // ===================================================================================
    //                                                                              Paging
    //                                                                              ======
    @Override
    public boolean isPaging() {
        return false;
    }

    @Override
    public boolean canPagingCountLater() {
        return false;
    }

    @Override
    public boolean canPagingReSelect() {
        return true;
    }

    @Override
    public void paging(int pageSize, int pageNumber) {
        if (pageSize <= 0) {
            throwPagingPageSizeNotPlusException(pageSize, pageNumber);
        }
        fetchFirst(pageSize);
        xfetchPage(pageNumber);
    }

    protected void throwPagingPageSizeNotPlusException(int pageSize, int pageNumber) {
        createCBExThrower().throwPagingPageSizeNotPlusException(this, pageSize, pageNumber);
    }

    protected ConditionBeanExceptionThrower createCBExThrower() {
        return new ConditionBeanExceptionThrower();
    }

    @Override
    public void xsetPaging(boolean paging) {
        // Do nothing because this is unsupported on ConditionBean.
        // And it is possible that this method is called by PagingInvoker.
    }

    @Override
    public void enablePagingCountLater() {
        // nothing
    }

    @Override
    public void disablePagingCountLater() {
        // nothing
    }

    @Override
    public void enablePagingReSelect() {
        // nothing
    }

    @Override
    public void disablePagingReSelect() {
        // nothing
    }

    @Override
    public PagingBean fetchFirst(int fetchSize) {
        getSqlClause().fetchFirst(fetchSize);
        return this;
    }

    @Override
    public PagingBean xfetchScope(int fetchStartIndex, int fetchSize) {
        getSqlClause().fetchScope(fetchStartIndex, fetchSize);
        return this;
    }

    @Override
    public PagingBean xfetchPage(int fetchPageNumber) {
        getSqlClause().fetchPage(fetchPageNumber);
        return this;
    }

    @Override
    public <ENTITY> PagingInvoker<ENTITY> createPagingInvoker(String tableDbName) {
        return null;
    }

    // ===================================================================================
    //                                                              Various Implementation
    //                                                              ======================
    @Override
    public int getFetchStartIndex() {
        return getSqlClause().getFetchStartIndex();
    }

    @Override
    public int getFetchSize() {
        return getSqlClause().getFetchSize();
    }

    @Override
    public int getFetchPageNumber() {
        return getSqlClause().getFetchPageNumber();
    }

    @Override
    public int getPageStartIndex() {
        return getSqlClause().getPageStartIndex();
    }

    @Override
    public int getPageEndIndex() {
        return getSqlClause().getPageEndIndex();
    }

    @Override
    public boolean isFetchScopeEffective() {
        return getSqlClause().isFetchScopeEffective();
    }

    @Override
    public int getFetchNarrowingSkipStartIndex() {
        return getPageStartIndex();
    }

    @Override
    public int getFetchNarrowingLoopCount() {
        return getFetchSize();
    }

    @Override
    public boolean isFetchNarrowingSkipStartIndexEffective() {
        return false;
    }

    @Override
    public boolean isFetchNarrowingLoopCountEffective() {
        return false;
    }

    @Override
    public boolean isFetchNarrowingEffective() {
        return getSqlClause().isFetchNarrowingEffective();
    }

    @Override
    public void xdisableFetchNarrowing() {
        // no need to disable in ConditionBean, basically for OutsideSql
        String msg = "This method is unsupported on ConditionBean!";
        throw new UnsupportedOperationException(msg);
    }

    @Override
    public void xenableIgnoredFetchNarrowing() {
        // do nothing
    }

    @Override
    public void checkSafetyResult(int safetyMaxResultSize) {
        _safetyMaxResultSize = safetyMaxResultSize;
    }

    @Override
    public int getSafetyMaxResultSize() {
        return _safetyMaxResultSize;
    }

    @Override
    public String getOrderByClause() {
        return null;
    }

    @Override
    public OrderByClause getOrderByComponent() {
        return null;
    }

    @Override
    public OrderByBean clearOrderBy() {
        return null;
    }

    @Override
    public void overTheWaves(SpecifiedColumn dreamCruiseTicket) {
        // do nothing
    }

    @Override
    public void mysticRhythms(Object mysticBinding) {
        // do nothing
    }

    @Override
    public DBMeta asDBMeta() {
        return null;
    }

    @Override
    public SqlClause getSqlClause() {
        return _sqlClause;
    }

    @Override
    public ConditionBean addOrderBy_PK_Asc() {
        return null;
    }

    @Override
    public ConditionBean addOrderBy_PK_Desc() {
        return null;
    }

    @Override
    public HpColumnSpHandler localSp() {
        return null;
    }

    @Override
    public void enableInnerJoinAutoDetect() {
        // do nothing
    }

    @Override
    public void disableInnerJoinAutoDetect() {
        // do nothing
    }

    @Override
    public SpecifiedColumn inviteDerivedToDreamCruise(String derivedAlias) {
        return null;
    }

    @Override
    public ConditionBean xcreateDreamCruiseCB() {
        return null;
    }

    @Override
    public void xmarkAsDeparturePortForDreamCruise() {
        // do nothing
    }

    @Override
    public boolean xisDreamCruiseDeparturePort() {
        return false;
    }

    @Override
    public boolean xisDreamCruiseShip() {
        return false;
    }

    @Override
    public ConditionBean xgetDreamCruiseDeparturePort() {
        return null;
    }

    @Override
    public boolean xhasDreamCruiseTicket() {
        return false;
    }

    @Override
    public SpecifiedColumn xshowDreamCruiseTicket() {
        return null;
    }

    @Override
    public void xkeepDreamCruiseJourneyLogBook(String relationPath) {
        // do nothing
    }

    @Override
    public void xsetupSelectDreamCruiseJourneyLogBook() {
        // do nothing
    }

    @Override
    public void xsetupSelectDreamCruiseJourneyLogBookIfUnionExists() {
        // do nothing
    }

    @Override
    public Object xgetMysticBinding() {
        return null;
    }

    @Override
    public void ignoreNullOrEmptyQuery() {
    }

    @Override
    public void checkNullOrEmptyQuery() {
    }

    @Override
    public void enableEmptyStringQuery(ModeQuery noArgInLambda) {
        // do nothing
    }

    @Override
    public void disableEmptyStringQuery() {
    }

    @Override
    public void enableOverridingQuery(ModeQuery noArgInLambda) {
        // do nothing
    }

    @Override
    public void disableOverridingQuery() {
        // do nothing
    }

    @Override
    public void enablePagingCountLeastJoin() {
        // do nothing
    }

    @Override
    public void disablePagingCountLeastJoin() {
        // do nothing
    }

    @Override
    public boolean canPagingSelectAndQuerySplit() {
        return false;
    }

    @Override
    public ConditionBean lockForUpdate() {
        return null;
    }

    @Override
    public ConditionBean xsetupSelectCountIgnoreFetchScope(boolean uniqueCount) {
        return null;
    }

    @Override
    public ConditionBean xafterCareSelectCountIgnoreFetchScope() {
        return null;
    }

    @Override
    public boolean isSelectCountIgnoreFetchScope() {
        return false;
    }

    @Override
    public CursorSelectOption getCursorSelectOption() {
        return null;
    }

    @Override
    public void xacceptScalarSelectOption(ScalarSelectOption option) {
        // do nothing
    }

    @Override
    public void configure(StatementConfigCall<StatementConfig> confLambda) {
        // do nothing
    }

    @Override
    public StatementConfig getStatementConfig() {
        return null;
    }

    @Override
    public boolean canRelationMappingCache() {
        return false;
    }

    @Override
    public void enableNonSpecifiedColumnAccess() {
        // do nothing
    }

    @Override
    public void disableNonSpecifiedColumnAccess() {
        // do nothing
    }

    @Override
    public boolean isNonSpecifiedColumnAccessAllowed() {
        return false;
    }

    @Override
    public void enableColumnNullObject() {
    }

    @Override
    public void disableColumnNullObject() {
    }

    @Override
    public void enableQueryUpdateCountPreCheck() {
        // do nothing
    }

    @Override
    public void disableQueryUpdateCountPreCheck() {
        // do nothing
    }

    @Override
    public boolean isQueryUpdateCountPreCheck() {
        return false;
    }

    @Override
    public String toDisplaySql() {
        return null;
    }

    @Override
    public void styleLogDateDisplay(BoundDateDisplayStyle logDateDisplayStyle) {
        // do nothing
    }

    @Override
    public BoundDateDisplayStyle getLogDateDisplayStyle() {
        return null;
    }

    @Override
    public boolean hasWhereClauseOnBaseQuery() {
        return false;
    }

    @Override
    public void clearWhereClauseOnBaseQuery() {
        // do nothing
    }

    @Override
    public boolean hasSelectAllPossible() {
        return false;
    }

    @Override
    public boolean hasOrderByClause() {
        return false;
    }

    @Override
    public boolean hasUnionQueryOrUnionAllQuery() {
        return false;
    }

    @Override
    public void invokeSetupSelect(String foreignPropertyNamePath) {
        // do nothing
    }

    @Override
    public SpecifiedColumn invokeSpecifyColumn(String columnPropertyPath) {
        return null;
    }

    @Override
    public void invokeOrScopeQuery(OrQuery<ConditionBean> orQuery) {
        // do nothing
    }

    @Override
    public void invokeOrScopeQueryAndPart(AndQuery<ConditionBean> andQuery) {
        // do nothing
    }

    @Override
    public void xregisterUnionQuerySynchronizer(UnionQuery<ConditionBean> unionQuerySynchronizer) {
        // do nothing
    }

    @Override
    public DerivedTypeHandler xgetDerivedTypeHandler() {
        return null;
    }

    @Override
    public HpCBPurpose getPurpose() {
        return null;
    }

    @Override
    public void xsetupForScalarSelect() {
        // do nothing
    }

    @Override
    public void xsetupForQueryInsert() {
        // do nothing
    }

    @Override
    public void xsetupForSpecifiedUpdate() {
        // do nothing
    }

    @Override
    public void xsetupForVaryingUpdate() {
        // do nothing
    }

    @Override
    public void enableThatsBadTiming() {
        // do nothing
    }

    @Override
    public void disableThatsBadTiming() {
        // do nothing
    }

    // no annotation for compatible with 1.1.1
    public void enableSpecifyColumnRequired() {
        // do nothing
    }

    public void disableSpecifyColumnRequired() {
        // do nothing
    }

    public void xcheckSpecifyColumnRequiredIfNeeds() {
        // do nothing
    }

    @Override
    public boolean hasSpecifiedLocalColumn() {
        return false;
    }

    @Override
    public void enableUndefinedClassificationSelect() {
    }

    @Override
    public void disableUndefinedClassificationSelect() {
    }

    @Override
    public boolean isUndefinedClassificationSelectAllowed() {
        return false;
    }

    // ===================================================================================
    //                                                                   Request Parameter
    //                                                                   =================
    public SearchRequestParams request() {
        return _searchRequestParams;
    }

    public void setPreference(final String preference) {
        _preference = preference;
    }

    public String getPreference() {
        return _preference;
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String ln() {
        return DBFluteSystem.ln();
    }

    protected void assertObjectNotNull(String variableName, Object value) {
        if (variableName == null) {
            String msg = "The value should not be null: variableName=null value=" + value;
            throw new IllegalArgumentException(msg);
        }
        if (value == null) {
            String msg = "The value should not be null: variableName=" + variableName;
            throw new IllegalArgumentException(msg);
        }
    }

    // ===================================================================================
    //                                                                        Assist Class
    //                                                                        ============
    public static class SearchRequestParams {

        private Boolean explain;

        private Float minScore;

        private String preference;

        private String routing;

        private String searchType;

        private long timeoutInMillis = -1;

        private Boolean version;

        private Boolean seqNoAndPrimaryTerm = Boolean.TRUE;

        private int terminateAfter = 0;

        public void build(SearchRequestBuilder builder) {
            if (explain != null) {
                builder.setExplain(explain);
            }
            if (minScore != null) {
                builder.setMinScore(minScore);
            }
            if (preference != null) {
                builder.setPreference(preference);
            }
            if (routing != null) {
                builder.setRouting(routing);
            }
            if (searchType != null) {
                builder.setSearchType(searchType);
            }
            if (timeoutInMillis != -1) {
                builder.setTimeout(new TimeValue(timeoutInMillis));
            }
            if (version != null) {
                builder.setVersion(version);
            }
            if (seqNoAndPrimaryTerm != null) {
                builder.seqNoAndPrimaryTerm(seqNoAndPrimaryTerm);
            }
            if (terminateAfter > 0) {
                builder.setTerminateAfter(terminateAfter);
            }
        }

        public void setExplain(boolean explain) {
            this.explain = explain;
        }

        public void setMinScore(float minScore) {
            this.minScore = minScore;
        }

        public void setPreference(String preference) {
            this.preference = preference;
        }

        public void setRouting(String routing) {
            this.routing = routing;
        }

        public void setSearchType(String searchType) {
            this.searchType = searchType;
        }

        public void setTimeoutInMillis(long timeoutInMillis) {
            this.timeoutInMillis = timeoutInMillis;
        }

        public void setVersion(boolean version) {
            this.version = version;
        }

        public void setTerminateAfter(int terminateAfter) {
            this.terminateAfter = terminateAfter;
        }
    }
}
