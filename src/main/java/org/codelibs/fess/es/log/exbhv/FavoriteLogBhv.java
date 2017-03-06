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

import org.codelibs.fess.es.log.bsbhv.BsFavoriteLogBhv;
import org.codelibs.fess.es.log.exentity.FavoriteLog;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.exception.IllegalBehaviorStateException;
import org.dbflute.util.DfTypeUtil;

/**
 * @author FreeGen
 */
public class FavoriteLogBhv extends BsFavoriteLogBhv {
    @Override
    protected <RESULT extends FavoriteLog> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
            final RESULT result = entityType.newInstance();
            result.setCreatedAt(systemHelper.toLocalDateTime(source.get("createdAt")));
            result.setUrl(DfTypeUtil.toString(source.get("url")));
            result.setDocId(DfTypeUtil.toString(source.get("docId")));
            result.setQueryId(DfTypeUtil.toString(source.get("queryId")));
            result.setUserInfoId(DfTypeUtil.toString(source.get("userInfoId")));
            return result;
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }
}
