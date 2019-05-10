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
package org.codelibs.fess.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.regex.Matcher;

import javax.annotation.PostConstruct;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.es.config.exbhv.PathMappingBhv;
import org.codelibs.fess.es.config.exentity.PathMapping;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.DocumentUtil;
import org.codelibs.fess.util.GroovyUtil;
import org.lastaflute.di.core.exception.ComponentNotFoundException;
import org.lastaflute.di.core.factory.SingletonLaContainerFactory;
import org.lastaflute.web.util.LaRequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PathMappingHelper {

    private static final Logger logger = LoggerFactory.getLogger(PathMappingHelper.class);

    protected static final String FUNCTION_ENCODEURL_MATCHER = "function:encodeUrl";

    protected static final String GROOVY_MATCHER = "groovy:";

    protected final Map<String, List<PathMapping>> pathMappingMap = new HashMap<>();

    protected volatile List<PathMapping> cachedPathMappingList = null;

    @PostConstruct
    public void init() {
        update();
    }

    public int update() {
        final List<String> ptList = new ArrayList<>();
        ptList.add(Constants.PROCESS_TYPE_DISPLAYING);
        ptList.add(Constants.PROCESS_TYPE_BOTH);

        try {
            final PathMappingBhv pathMappingBhv = ComponentUtil.getComponent(PathMappingBhv.class);
            cachedPathMappingList = pathMappingBhv.selectList(cb -> {
                cb.query().addOrderBy_SortOrder_Asc();
                cb.query().setProcessType_InScope(ptList);
                cb.fetchFirst(ComponentUtil.getFessConfig().getPagePathMappingMaxFetchSizeAsInteger());
            });
            return cachedPathMappingList.size();
        } catch (final ComponentNotFoundException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to load path mappings.", e);
            }
            cachedPathMappingList = new ArrayList<>();
        } catch (final Exception e) {
            logger.warn("Failed to load path mappings.", e);
        }
        return 0;
    }

    public void setPathMappingList(final String sessionId, final List<PathMapping> pathMappingList) {
        if (sessionId != null) {
            if (pathMappingList != null) {
                pathMappingMap.put(sessionId, pathMappingList);
            } else {
                removePathMappingList(sessionId);
            }
        }
    }

    public void removePathMappingList(final String sessionId) {
        pathMappingMap.remove(sessionId);
    }

    public List<PathMapping> getPathMappingList(final String sessionId) {
        if (sessionId == null) {
            return null;
        }
        return pathMappingMap.get(sessionId);
    }

    public String replaceUrl(final String sessionId, final String url) {
        final List<PathMapping> pathMappingList = getPathMappingList(sessionId);
        if (pathMappingList == null) {
            return url;
        }
        return replaceUrl(pathMappingList, url);
    }

    public String replaceUrls(final String text) {
        if (cachedPathMappingList == null) {
            synchronized (this) {
                if (cachedPathMappingList == null) {
                    init();
                }
            }
        }
        String result = text;
        for (final PathMapping pathMapping : cachedPathMappingList) {
            if (matchUserAgent(pathMapping)) {
                String replacement = pathMapping.getReplacement();
                if (replacement == null) {
                    replacement = StringUtil.EMPTY;
                }
                result = result.replaceAll("(\"[^\"]*)" + pathMapping.getRegex() + "([^\"]*\")", "$1" + replacement + "$2");
            }
        }
        return result;
    }

    public String replaceUrl(final String url) {
        if (cachedPathMappingList == null) {
            synchronized (this) {
                if (cachedPathMappingList == null) {
                    init();
                }
            }
        }
        return replaceUrl(cachedPathMappingList, url);
    }

    public BiFunction<String, Matcher, String> createPathMatcher(final Matcher matcher, final String replacement) {
        if (replacement.equals(FUNCTION_ENCODEURL_MATCHER)) {
            return (u, m) -> DocumentUtil.encodeUrl(u);
        } else if (replacement.startsWith(GROOVY_MATCHER)) {
            final String template = replacement.substring(GROOVY_MATCHER.length());
            return (u, m) -> {
                final Map<String, Object> paramMap = new HashMap<>();
                paramMap.put("url", u);
                paramMap.put("matcher", m);
                final Object value = GroovyUtil.evaluate(template, paramMap);
                if (value == null) {
                    return u;
                }
                return value.toString();
            };
        } else {
            return (u, m) -> m.replaceAll(replacement);
        }
    }

    private String replaceUrl(final List<PathMapping> pathMappingList, final String url) {
        String newUrl = url;
        for (final PathMapping pathMapping : pathMappingList) {
            if (matchUserAgent(pathMapping)) {
                newUrl = pathMapping.process(this, newUrl);
            }
        }
        return newUrl;
    }

    private boolean matchUserAgent(final PathMapping pathMapping) {
        if (!pathMapping.hasUAMathcer()) {
            return true;
        }

        if (SingletonLaContainerFactory.getExternalContext().getRequest() != null) {
            return LaRequestUtil.getOptionalRequest().map(request -> {
                final String userAgent = request.getHeader("user-agent");
                if (StringUtil.isBlank(userAgent)) {
                    return false;
                }

                return pathMapping.getUAMatcher(userAgent).find();
            }).orElse(false);
        }
        return false;
    }
}
