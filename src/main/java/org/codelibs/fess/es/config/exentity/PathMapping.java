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
package org.codelibs.fess.es.config.exentity;

import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.es.config.bsentity.BsPathMapping;
import org.codelibs.fess.helper.PathMappingHelper;

/**
 * @author FreeGen
 */
public class PathMapping extends BsPathMapping {

    private static final Logger logger = LogManager.getLogger(PathMapping.class);

    private static final long serialVersionUID = 1L;

    protected Pattern userAgentPattern;

    protected Pattern regexPattern;

    protected BiFunction<String, Matcher, String> pathMapperFunc;

    public String getId() {
        return asDocMeta().id();
    }

    public void setId(final String id) {
        asDocMeta().id(id);
    }

    public Long getVersionNo() {
        return asDocMeta().version();
    }

    public void setVersionNo(final Long version) {
        asDocMeta().version(version);
    }

    public String process(final PathMappingHelper pathMappingHelper, final String input) {
        if (regexPattern == null) {
            regexPattern = Pattern.compile(getRegex());
        }
        final Matcher matcher = regexPattern.matcher(input);
        if (matcher.find()) {
            if (pathMapperFunc == null) {
                final String replacement = StringUtil.isNotBlank(getReplacement()) ? getReplacement() : StringUtil.EMPTY;
                pathMapperFunc = pathMappingHelper.createPathMatcher(matcher, replacement);
            }
            try {
                return pathMapperFunc.apply(input, matcher);
            } catch (final Exception e) {
                logger.warn("Failed to apply {} to {}.", regexPattern.pattern(), input, e);
            }
        }
        return input;
    }

    public boolean hasUAMathcer() {
        return StringUtil.isNotBlank(getUserAgent());
    }

    public Matcher getUAMatcher(final CharSequence input) {
        if (!hasUAMathcer()) {
            return null;
        }

        if (userAgentPattern == null) {
            userAgentPattern = Pattern.compile(getUserAgent());
        }
        return userAgentPattern.matcher(input);
    }

    @Override
    public String toString() {
        return "PathMapping [regexPattern=" + regexPattern + ", createdBy=" + createdBy + ", createdTime=" + createdTime + ", processType="
                + processType + ", regex=" + regex + ", replacement=" + replacement + ", sortOrder=" + sortOrder + ", userAgent="
                + userAgent + ", updatedBy=" + updatedBy + ", updatedTime=" + updatedTime + ", docMeta=" + docMeta + "]";
    }
}
