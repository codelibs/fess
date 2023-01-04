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
package org.codelibs.fess.es.user.exbhv;

import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.codelibs.core.misc.Pair;
import org.codelibs.fess.es.user.bsbhv.BsUserBhv;
import org.codelibs.fess.es.user.exentity.User;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.exception.IllegalBehaviorStateException;
import org.dbflute.util.DfTypeUtil;

/**
 * @author FreeGen
 */
public class UserBhv extends BsUserBhv {

    private static final String ROLES = "roles";
    private static final String GROUPS = "groups";
    private static final String PASSWORD = "password";
    private static final String NAME = "name";

    private String indexName = null;

    @Override
    protected String asEsIndex() {
        if (indexName == null) {
            final String name = ComponentUtil.getFessConfig().getIndexUserIndex();
            indexName = super.asEsIndex().replaceFirst(Pattern.quote("fess_user"), name);
        }
        return indexName;
    }

    @Override
    protected <RESULT extends User> RESULT createEntity(final Map<String, Object> source, final Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setName(DfTypeUtil.toString(source.get(NAME)));
            result.setPassword(DfTypeUtil.toString(source.get(PASSWORD)));
            result.setGroups(toStringArray(source.get(GROUPS)));
            result.setRoles(toStringArray(source.get(ROLES)));
            result.setAttributes(source.entrySet().stream().filter(e -> isAttribute(e.getKey()))
                    .map(e -> new Pair<>(e.getKey(), (String) e.getValue())).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
            return result;
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    private boolean isAttribute(final String key) {
        return !NAME.equals(key) && !PASSWORD.equals(key) && !GROUPS.equals(key) && !ROLES.equals(key);
    }

}
