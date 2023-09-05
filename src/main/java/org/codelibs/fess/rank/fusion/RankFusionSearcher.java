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
package org.codelibs.fess.rank.fusion;

import java.util.Locale;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.entity.SearchRequestParams;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.dbflute.optional.OptionalThing;

public abstract class RankFusionSearcher {
    protected String name;

    public String getName() {
        if (name == null) {
            name = StringUtil.decamelize(this.getClass().getSimpleName().replace("Searcher", StringUtil.EMPTY)).toLowerCase(Locale.ENGLISH);
        }
        return name;
    }

    protected abstract SearchResult search(String query, SearchRequestParams params, OptionalThing<FessUserBean> userBean);

}
