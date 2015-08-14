/*
 * Copyright 2009-2015 the CodeLibs Project and the Others.
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

package jp.sf.fess.api;

public class BaseApiManager {
    protected static final String FAVORITES_API = "/favoritesApi";

    protected static final String FAVORITE_API = "/favoriteApi";

    protected static final String HOT_SEARCH_WORD_API = "/hotSearchWordApi";

    protected static final String ANALYSIS_API = "/analysisApi";

    protected static final String SUGGEST_API = "/suggestApi";

    protected static final String SPELLCHECK_API = "/spellCheckApi";

    protected static final String SEARCH_API = "/searchApi";

    protected static enum FormatType {
        SEARCH, LABEL, SUGGEST, SPELLCHECK, ANALYSIS, HOTSEARCHWORD, FAVORITE, FAVORITES, OTHER, PING;
    }

    public BaseApiManager() {
        super();
    }

    protected FormatType getFormatType(final String formatType) {
        if (formatType == null) {
            return FormatType.SEARCH;
        }
        final String type = formatType.toUpperCase();
        if (FormatType.SEARCH.name().equals(type)) {
            return FormatType.SEARCH;
        } else if (FormatType.LABEL.name().equals(type)) {
            return FormatType.LABEL;
        } else if (FormatType.SUGGEST.name().equals(type)) {
            return FormatType.SUGGEST;
        } else if (FormatType.SPELLCHECK.name().equals(type)) {
            return FormatType.SPELLCHECK;
        } else if (FormatType.ANALYSIS.name().equals(type)) {
            return FormatType.ANALYSIS;
        } else if (FormatType.HOTSEARCHWORD.name().equals(type)) {
            return FormatType.HOTSEARCHWORD;
        } else if (FormatType.FAVORITE.name().equals(type)) {
            return FormatType.FAVORITE;
        } else if (FormatType.FAVORITES.name().equals(type)) {
            return FormatType.FAVORITES;
        } else if (FormatType.PING.name().equals(type)) {
            return FormatType.PING;
        } else {
            // default
            return FormatType.OTHER;
        }
    }

}