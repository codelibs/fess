/*
 * Copyright 2012-2016 CodeLibs Project and the Others.
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
import java.util.stream.Collectors;

import org.codelibs.core.misc.Pair;
import org.codelibs.fess.es.log.bsbhv.BsClickLogBhv;
import org.codelibs.fess.es.log.exentity.ClickLog;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.exception.IllegalBehaviorStateException;
import org.dbflute.util.DfTypeUtil;

/**
 * @author FreeGen
 */
public class ClickLogBhv extends BsClickLogBhv {

    private static final String QUERY_REQUESTED_AT = "queryRequestedAt";
    private static final String REQUESTED_AT = "requestedAt";
    private static final String QUERY_ID = "queryId";
    private static final String DOC_ID = "docId";
    private static final String USER_SESSION_ID = "userSessionId";
    private static final String URL = "url";
    private static final String ORDER = "order";

    @Override
    protected <RESULT extends ClickLog> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
            final RESULT result = entityType.newInstance();
            result.setQueryRequestedAt(systemHelper.toLocalDateTime(source.get("queryRequestedAt")));
            result.setRequestedAt(DfTypeUtil.toLocalDateTime(source.get("requestedAt")));
            result.setQueryId(DfTypeUtil.toString(source.get("queryId")));
            result.setDocId(DfTypeUtil.toString(source.get("docId")));
            result.setUserSessionId(DfTypeUtil.toString(source.get("userSessionId")));
            result.setUrl(DfTypeUtil.toString(source.get("url")));
            result.setOrder(DfTypeUtil.toInteger(source.get("order")));
            result.setAttributes(source.entrySet().stream().filter(e -> isAttribute(e.getKey()))
                    .map(e -> new Pair<>(e.getKey(), (String) e.getValue()))
                    .collect(Collectors.toMap(t -> t.getFirst(), t -> t.getSecond())));
            return result;
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    private boolean isAttribute(final String key) {
        return !QUERY_REQUESTED_AT.equals(key) && !REQUESTED_AT.equals(key) && !QUERY_ID.equals(key) && !DOC_ID.equals(key)
                && !USER_SESSION_ID.equals(key) && !URL.equals(key) && !ORDER.equals(key);
    }
}
