/*
 * Copyright 2012-2018 CodeLibs Project and the Others.
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

import org.codelibs.fess.es.query.StoredLtrQueryBuilder;
import org.elasticsearch.search.rescore.QueryRescorerBuilder;
import org.elasticsearch.search.rescore.RescorerBuilder;

public class LtrQueryRescorer implements QueryRescorer {

    protected String modelName;

    protected int windowSize = 100;

    @Override
    public RescorerBuilder<?> evaluate(Map<String, Object> params) {
        return new QueryRescorerBuilder(new StoredLtrQueryBuilder().modelName(modelName).params(params)).windowSize(windowSize);
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public void setWindowSize(int windowSize) {
        this.windowSize = windowSize;
    }
}
