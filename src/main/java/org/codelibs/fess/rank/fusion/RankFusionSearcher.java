/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
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

/**
 * Abstract base class for rank fusion searchers in the Fess search system.
 * Rank fusion searchers are responsible for executing search queries and
 * can be combined to implement advanced ranking strategies.
 */
public abstract class RankFusionSearcher {
    /** The name of this searcher, lazily initialized. */
    protected String name;

    /**
     * Returns the name of this searcher.
     * The name is derived from the class name by converting it to lowercase
     * and removing the "Searcher" suffix.
     *
     * @return the searcher name
     */
    public String getName() {
        if (name == null) {
            name = StringUtil.decamelize(this.getClass().getSimpleName().replace("Searcher", StringUtil.EMPTY)).toLowerCase(Locale.ENGLISH);
        }
        return name;
    }

    /**
     * Executes a search operation with the specified parameters.
     * This method must be implemented by concrete searcher classes.
     *
     * @param query the search query string
     * @param params the search request parameters including pagination, filters, etc.
     * @param userBean the optional user bean for access control and personalization
     * @return the search result containing matched documents and metadata
     */
    protected abstract SearchResult search(String query, SearchRequestParams params, OptionalThing<FessUserBean> userBean);

}
