/*
 * Copyright 2012-2015 CodeLibs Project and the Others.
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
package org.codelibs.fess.api;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.codelibs.core.exception.IORuntimeException;
import org.lastaflute.web.util.LaRequestUtil;
import org.lastaflute.web.util.LaResponseUtil;

public abstract class BaseApiManager implements WebApiManager {
    protected static final String FAVORITES_API = "/favoritesApi";

    protected static final String FAVORITE_API = "/favoriteApi";

    protected static final String HOT_SEARCH_WORD_API = "/hotSearchWordApi";

    protected static final String SEARCH_API = "/searchApi";

    protected String pathPrefix;

    protected static enum FormatType {
        SEARCH, LABEL, HOTSEARCHWORD, FAVORITE, FAVORITES, OTHER, PING;
    }

    public String getPathPrefix() {
        return pathPrefix;
    }

    public void setPathPrefix(final String pathPrefix) {
        this.pathPrefix = pathPrefix;
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

    public static void write(final String text, String contentType, String encoding) {
        if (contentType == null) {
            contentType = "text/plain";
        }
        if (encoding == null) {
            encoding = LaRequestUtil.getRequest().getCharacterEncoding();
            if (encoding == null) {
                encoding = "UTF-8";
            }
        }
        final HttpServletResponse response = LaResponseUtil.getResponse();
        response.setContentType(contentType + "; charset=" + encoding);
        try {
            PrintWriter out = null;
            try {
                out = new PrintWriter(new OutputStreamWriter(response.getOutputStream(), encoding));
                out.print(text);
            } finally {
                if (out != null) {
                    out.close();
                }
            }
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

}