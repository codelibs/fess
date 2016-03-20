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
import org.codelibs.fess.crawler.util.UnsafeStringBuilder;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DocumentHelper implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(DocumentHelper.class);

    public String getContent(final ResponseData responseData, final String content, final Map<String, Object> dataMap) {
        if (content == null) {
            return StringUtil.EMPTY; // empty
        }

        final int maxAlphanumTermSize = getMaxAlphanumTermSize();
        final int maxSymbolTermSize = getMaxSymbolTermSize();
        final UnsafeStringBuilder buf = new UnsafeStringBuilder(content.length());
        boolean isSpace = false;
        int alphanumSize = 0;
        int symbolSize = 0;
        for (int i = 0; i < content.length(); i++) {
            final char c = content.charAt(i);
            if (Character.isISOControl(c) || c == '\u0020' || c == '\u3000' || c == 65533) {
                // space
                if (!isSpace) {
                    buf.append(' ');
                    isSpace = true;
                }
                alphanumSize = 0;
                symbolSize = 0;
            } else if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
                // alphanum
                if (maxAlphanumTermSize >= 0) {
                    if (alphanumSize < maxAlphanumTermSize) {
                        buf.append(c);
                    }
                    alphanumSize++;
                } else {
                    buf.append(c);
                }
                isSpace = false;
                symbolSize = 0;
            } else if ((c >= '!' && c <= '/') || (c >= ':' && c <= '@') || (c >= '[' && c <= '`') || (c >= '{' && c <= '~')) {
                // symbol
                if (maxSymbolTermSize >= 0) {
                    if (symbolSize < maxSymbolTermSize) {
                        buf.append(c);
                    }
                    symbolSize++;
                } else {
                    buf.append(c);
                }
                isSpace = false;
                alphanumSize = 0;
            } else {
                buf.append(c);
                isSpace = false;
                alphanumSize = 0;
                symbolSize = 0;
            }
        }

        return buf.toUnsafeString().trim();
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

        final String originalStr = subContent.replaceAll("[\u0000-\u0020\u007f\u3000]+", " ").trim();
        return StringUtils.abbreviate(originalStr, maxWidth);
    }
}
