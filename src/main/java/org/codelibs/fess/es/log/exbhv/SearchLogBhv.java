/*
 * Copyright 2012-2017 CodeLibs Project and the Others.
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
package org.codelibs.fess.es.log.exbhv;

import java.util.Map;

import org.codelibs.fess.es.log.bsbhv.BsSearchLogBhv;
import org.codelibs.fess.es.log.exentity.SearchLog;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.exception.IllegalBehaviorStateException;
import org.dbflute.util.DfTypeUtil;

/**
 * @author FreeGen
 */
public class SearchLogBhv extends BsSearchLogBhv {

    @Override
    protected <RESULT extends SearchLog> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        try {
            final RESULT result = entityType.newInstance();
            result.setAccessType(DfTypeUtil.toString(source.get("accessType")));
            result.setUser(DfTypeUtil.toString(source.get("user")));
            result.setRoles(toStringArray(source.get("roles")));
            result.setQueryId(DfTypeUtil.toString(source.get("queryId")));
            result.setClientIp(DfTypeUtil.toString(source.get("clientIp")));
            result.setHitCount(DfTypeUtil.toLong(source.get("hitCount")));
            result.setQueryOffset(DfTypeUtil.toInteger(source.get("queryOffset")));
            result.setQueryPageSize(DfTypeUtil.toInteger(source.get("queryPageSize")));
            result.setReferer(DfTypeUtil.toString(source.get("referer")));
            result.setRequestedAt(systemHelper.toLocalDateTime(source.get("requestedAt")));
            result.setResponseTime(DfTypeUtil.toLong(source.get("responseTime")));
            result.setQueryTime(DfTypeUtil.toLong(source.get("queryTime")));
            result.setSearchWord(DfTypeUtil.toString(source.get("searchWord")));
            result.setUserAgent(DfTypeUtil.toString(source.get("userAgent")));
            result.setUserInfoId(DfTypeUtil.toString(source.get("userInfoId")));
            result.setUserSessionId(DfTypeUtil.toString(source.get("userSessionId")));
            result.setLanguages(DfTypeUtil.toString(source.get("languages")));
            return result;
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }
}
