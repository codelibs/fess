/*
 * Copyright 2012-2019 CodeLibs Project and the Others.
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
package org.codelibs.fess.score;

import java.util.Map;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.es.query.StoredLtrQueryBuilder;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.elasticsearch.search.rescore.QueryRescorerBuilder;
import org.elasticsearch.search.rescore.RescorerBuilder;

public class LtrQueryRescorer implements QueryRescorer {

    @Override
    public RescorerBuilder<?> evaluate(final Map<String, Object> params) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String modelName = fessConfig.getLtrModelName();
        if (StringUtil.isBlank(modelName)) {
            return null;
        }
        return new QueryRescorerBuilder(new StoredLtrQueryBuilder().modelName(modelName).params(params)).windowSize(fessConfig
                .getLtrWindowSize());
    }
}
