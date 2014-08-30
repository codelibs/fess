/*
 * Copyright 2009-2014 the CodeLibs Project and the Others.
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

package jp.sf.fess.db.bsbhv.pmbean;

import java.util.ArrayList;
import java.util.Date;

import jp.sf.fess.db.allcommon.DBFluteConfig;
import jp.sf.fess.db.exbhv.FavoriteLogBhv;
import jp.sf.fess.db.exentity.customize.FavoriteUrlRanking;

import org.seasar.dbflute.cbean.SimplePagingBean;
import org.seasar.dbflute.jdbc.FetchBean;
import org.seasar.dbflute.jdbc.ParameterUtil;
import org.seasar.dbflute.jdbc.ParameterUtil.ShortCharHandlingMode;
import org.seasar.dbflute.outsidesql.typed.AutoPagingHandlingPmb;
import org.seasar.dbflute.outsidesql.typed.EntityHandlingPmb;
import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.dbflute.util.DfTypeUtil;

/**
 * The base class for typed parameter-bean of FavoriteUrlRanking. <br />
 * This is related to "<span style="color: #AD4747">selectFavoriteUrlRanking</span>" on FavoriteLogBhv.
 * @author DBFlute(AutoGenerator)
 */
public class BsFavoriteUrlRankingPmb extends SimplePagingBean implements
        EntityHandlingPmb<FavoriteLogBhv, FavoriteUrlRanking>,
        AutoPagingHandlingPmb<FavoriteLogBhv, FavoriteUrlRanking>, FetchBean {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The parameter of fromCreatedTime. */
    protected java.sql.Timestamp _fromCreatedTime;

    /** The parameter of toCreatedTime. */
    protected java.sql.Timestamp _toCreatedTime;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Constructor for the typed parameter-bean of FavoriteUrlRanking. <br />
     * This is related to "<span style="color: #AD4747">selectFavoriteUrlRanking</span>" on FavoriteLogBhv.
     */
    public BsFavoriteUrlRankingPmb() {
        if (DBFluteConfig.getInstance().isPagingCountLater()) {
            enablePagingCountLater();
        }
    }

    // ===================================================================================
    //                                                                Typed Implementation
    //                                                                ====================
    /**
     * {@inheritDoc}
     */
    @Override
    public String getOutsideSqlPath() {
        return "selectFavoriteUrlRanking";
    }

    /**
     * Get the type of an entity for result. (implementation)
     * @return The type instance of an entity, customize entity. (NotNull)
     */
    @Override
    public Class<FavoriteUrlRanking> getEntityType() {
        return FavoriteUrlRanking.class;
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected String filterStringParameter(final String value) {
        if (isEmptyStringParameterAllowed()) {
            return value;
        }
        return convertEmptyToNull(value);
    }

    protected boolean isEmptyStringParameterAllowed() {
        return DBFluteConfig.getInstance().isEmptyStringParameterAllowed();
    }

    protected String convertEmptyToNull(final String value) {
        return ParameterUtil.convertEmptyToNull(value);
    }

    protected String handleShortChar(final String propertyName,
            final String value, final Integer size) {
        final ShortCharHandlingMode mode = getShortCharHandlingMode(
                propertyName, value, size);
        return ParameterUtil.handleShortChar(propertyName, value, size, mode);
    }

    protected ShortCharHandlingMode getShortCharHandlingMode(
            final String propertyName, final String value, final Integer size) {
        return ShortCharHandlingMode.NONE;
    }

    @SuppressWarnings("unchecked")
    protected <ELEMENT> ArrayList<ELEMENT> newArrayList(
            final ELEMENT... elements) { // might be called by option handling
        final Object obj = DfCollectionUtil.newArrayList(elements);
        return (ArrayList<ELEMENT>) obj; // to avoid the warning between JDK6 and JDK7
    }

    @SuppressWarnings("unchecked")
    protected <NUMBER extends Number> NUMBER toNumber(final Object obj,
            final Class<NUMBER> type) { // might be called by option handling
        return (NUMBER) DfTypeUtil.toNumber(obj, type);
    }

    protected Boolean toBoolean(final Object obj) {
        return DfTypeUtil.toBoolean(obj);
    }

    protected Date toUtilDate(final Date date) {
        return DfTypeUtil.toDate(date); // if sub class, re-create as pure date
    }

    protected String formatUtilDate(final Date date) {
        final String pattern = "yyyy-MM-dd";
        return DfTypeUtil.toString(date, pattern);
    }

    protected String formatByteArray(final byte[] bytes) {
        return "byte["
                + (bytes != null ? String.valueOf(bytes.length) : "null") + "]";
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    /**
     * @return The display string of all parameters. (NotNull)
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(DfTypeUtil.toClassTitle(this)).append(":");
        sb.append(xbuildColumnString());
        return sb.toString();
    }

    protected String xbuildColumnString() {
        final String dm = ", ";
        final StringBuilder sb = new StringBuilder();
        sb.append(dm).append(_fromCreatedTime);
        sb.append(dm).append(_toCreatedTime);
        if (sb.length() > 0) {
            sb.delete(0, dm.length());
        }
        sb.insert(0, "{").append("}");
        return sb.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    /**
     * [get] fromCreatedTime <br />
     * @return The value of fromCreatedTime. (Nullable, NotEmptyString(when String): if empty string, returns null)
     */
    public java.sql.Timestamp getFromCreatedTime() {
        return _fromCreatedTime;
    }

    /**
     * [set] fromCreatedTime <br />
     * @param fromCreatedTime The value of fromCreatedTime. (NullAllowed)
     */
    public void setFromCreatedTime(final java.sql.Timestamp fromCreatedTime) {
        _fromCreatedTime = fromCreatedTime;
    }

    /**
     * [get] toCreatedTime <br />
     * @return The value of toCreatedTime. (Nullable, NotEmptyString(when String): if empty string, returns null)
     */
    public java.sql.Timestamp getToCreatedTime() {
        return _toCreatedTime;
    }

    /**
     * [set] toCreatedTime <br />
     * @param toCreatedTime The value of toCreatedTime. (NullAllowed)
     */
    public void setToCreatedTime(final java.sql.Timestamp toCreatedTime) {
        _toCreatedTime = toCreatedTime;
    }
}
