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
import org.codelibs.fess.es.user.bsbhv.BsGroupBhv;
import org.codelibs.fess.es.user.exentity.Group;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.exception.IllegalBehaviorStateException;
import org.dbflute.util.DfTypeUtil;

/**
 * @author FreeGen
 */
public class GroupBhv extends BsGroupBhv {
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
    protected <RESULT extends Group> RESULT createEntity(final Map<String, Object> source, final Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setName(DfTypeUtil.toString(source.get("name")));
            result.setAttributes(source.entrySet().stream().filter(e -> !"name".equals(e.getKey()))
                    .map(e -> new Pair<>(e.getKey(), (String) e.getValue())).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
            return result;
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }
}
