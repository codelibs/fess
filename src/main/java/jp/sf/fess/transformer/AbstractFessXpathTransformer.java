/*
 * Copyright 2009-2014 the CodeLibs Project and the Others.
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

package jp.sf.fess.transformer;

import java.net.URLDecoder;

import jp.sf.fess.Constants;

import org.apache.commons.lang.StringUtils;
import org.seasar.framework.util.StringUtil;
import org.seasar.robot.transformer.impl.XpathTransformer;

public abstract class AbstractFessXpathTransformer extends XpathTransformer {
    public int maxSiteLength = 50;

    public String unknownHostname = "unknown";

    public String siteEncoding;

    public boolean replaceSiteEncodingWhenEnglish = false;

    protected String getHost(final String u) {
        if (StringUtil.isBlank(u)) {
            return Constants.EMPTY_STRING; // empty
        }

        String url = u;
        final String originalUrl = url;

        int idx = url.indexOf("://");
        if (idx >= 0) {
            url = url.substring(idx + 3);
        }

        idx = url.indexOf('/');
        if (idx >= 0) {
            url = url.substring(0, idx);
        }

        if (url.equals(originalUrl)) {
            return unknownHostname;
        }

        return url;
    }

    protected String getSite(final String u, final String encoding) {
        if (StringUtil.isBlank(u)) {
            return Constants.EMPTY_STRING; // empty
        }

        String url = u;
        int idx = url.indexOf("://");
        if (idx >= 0) {
            url = url.substring(idx + 3);
        }

        idx = url.indexOf('?');
        if (idx >= 0) {
            url = url.substring(0, idx);
        }

        if (encoding != null) {
            String enc;
            if (siteEncoding != null) {
                if (replaceSiteEncodingWhenEnglish) {
                    if ("ISO-8859-1".equalsIgnoreCase(encoding)
                            || "US-ASCII".equalsIgnoreCase(encoding)) {
                        enc = siteEncoding;
                    } else {
                        enc = encoding;
                    }
                } else {
                    enc = siteEncoding;
                }
            } else {
                enc = encoding;
            }

            try {
                url = URLDecoder.decode(url, enc);
            } catch (final Exception e) {
            }
        }

        return StringUtils.abbreviate(url, maxSiteLength);
    }

    protected String normalizeContent(final String content) {
        if (content == null) {
            return Constants.EMPTY_STRING; // empty
        }
        return content.replaceAll("\\s+", " ");
    }
}