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
package org.codelibs.fess.helper;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.util.TextUtil;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;

public class DocumentHelper implements Serializable {

    private static final long serialVersionUID = 1L;

    public String getContent(final ResponseData responseData, final String content, final Map<String, Object> dataMap) {
        if (content == null) {
            return StringUtil.EMPTY; // empty
        }

        final int maxAlphanumTermSize = getMaxAlphanumTermSize();
        final int maxSymbolTermSize = getMaxSymbolTermSize();
        // TODO removeDuplication
        return TextUtil.normalizeText(content, content.length(), maxAlphanumTermSize, maxSymbolTermSize, false);
    }

    protected int getMaxAlphanumTermSize() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        return fessConfig.getCrawlerDocumentMaxAlphanumTermSizeAsInteger().intValue();
    }

    protected int getMaxSymbolTermSize() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        return fessConfig.getCrawlerDocumentMaxSymbolTermSizeAsInteger().intValue();
    }

    public String getDigest(final ResponseData responseData, final String content, final Map<String, Object> dataMap, final int maxWidth) {
        if (content == null) {
            return StringUtil.EMPTY; // empty
        }

        String subContent;
        if (content.length() < maxWidth * 2) {
            subContent = content;
        } else {
            subContent = content.substring(0, maxWidth * 2);
        }

        final String originalStr = TextUtil.normalizeText(subContent, subContent.length(), -1, -1, false);
        return StringUtils.abbreviate(originalStr, maxWidth);
    }
}
