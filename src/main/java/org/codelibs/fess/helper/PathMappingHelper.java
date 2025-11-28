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
package org.codelibs.fess.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.regex.Matcher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.opensearch.config.exbhv.PathMappingBhv;
import org.codelibs.fess.opensearch.config.exentity.PathMapping;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.DocumentUtil;
import org.lastaflute.di.core.exception.ComponentNotFoundException;
import org.lastaflute.di.core.factory.SingletonLaContainerFactory;
import org.lastaflute.web.util.LaRequestUtil;

import jakarta.annotation.PostConstruct;

/**
 * Helper class for path mapping configuration.
 */
public class PathMappingHelper extends AbstractConfigHelper {

    /**
     * Default constructor.
     */
    public PathMappingHelper() {
        super();
    }

    private static final Logger logger = LogManager.getLogger(PathMappingHelper.class);

    /** Function matcher for encode URL. */
    protected static final String FUNCTION_ENCODEURL_MATCHER = "function:encodeUrl";

    /** Groovy matcher prefix. */
    protected static final String GROOVY_MATCHER = "groovy:";

    /** Map of path mappings by process type. */
    protected final Map<String, List<PathMapping>> pathMappingMap = new HashMap<>();

    /** Cached list of path mappings. */
    protected volatile List<PathMapping> cachedPathMappingList = null;

    /**
     * Initializes the path mapping helper.
     */
    @PostConstruct
    public void init() {
        if (logger.isDebugEnabled()) {
            logger.debug("Initializing {}", this.getClass().getSimpleName());
        }
        load();
    }

    @Override
    public int load() {
        final List<String> ptList = getProcessTypeList();

        try {
            final PathMappingBhv pathMappingBhv = ComponentUtil.getComponent(PathMappingBhv.class);
            cachedPathMappingList = pathMappingBhv.selectList(cb -> {
                cb.query().addOrderBy_SortOrder_Asc();
                cb.query().setProcessType_InScope(ptList);
                cb.fetchFirst(ComponentUtil.getFessConfig().getPagePathMappingMaxFetchSizeAsInteger());
            });
            if (logger.isDebugEnabled()) {
                cachedPathMappingList.forEach(e -> {
                    logger.debug("path mapping: {}: {} -> {}", e.getId(), e.getRegex(), e.getReplacement());
                });
            }
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

    /**
     * Gets the list of process types.
     *
     * @return the list of process types
     */
    protected List<String> getProcessTypeList() {
        final List<String> ptList = new ArrayList<>();
        final String executeType = System.getProperty("lasta.env");
        if (Constants.EXECUTE_TYPE_CRAWLER.equalsIgnoreCase(executeType)) {
            ptList.add(Constants.PROCESS_TYPE_REPLACE);
        } else {
            ptList.add(Constants.PROCESS_TYPE_DISPLAYING);
            ptList.add(Constants.PROCESS_TYPE_BOTH);
        }
        return ptList;
    }

    /**
     * Sets the path mapping list for a session.
     *
     * @param sessionId the session ID
     * @param pathMappingList the path mapping list
     */
    public void setPathMappingList(final String sessionId, final List<PathMapping> pathMappingList) {
        if (sessionId != null) {
            if (pathMappingList != null) {
                pathMappingMap.put(sessionId, pathMappingList);
            } else {
                removePathMappingList(sessionId);
            }
        }
    }

    /**
     * Removes the path mapping list for a session.
     *
     * @param sessionId the session ID
     */
    public void removePathMappingList(final String sessionId) {
        pathMappingMap.remove(sessionId);
    }

    /**
     * Gets the path mapping list for a session.
     *
     * @param sessionId the session ID
     * @return the path mapping list
     */
    public List<PathMapping> getPathMappingList(final String sessionId) {
        if (sessionId == null) {
            return null;
        }
        return pathMappingMap.get(sessionId);
    }

    /**
     * Replaces URL for crawling.
     *
     * @param sessionId the session ID
     * @param url the URL to replace
     * @return the replaced URL
     */
    public String replaceUrl(final String sessionId, final String url) { // for crawling
        final List<PathMapping> pathMappingList = getPathMappingList(sessionId);
        if (pathMappingList == null) {
            return url;
        }
        return replaceUrl(pathMappingList, url);
    }

    /**
     * Replaces URLs in text.
     *
     * @param text the text containing URLs
     * @return the text with replaced URLs
     */
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

    /**
     * Replaces URL for display or URL converter.
     *
     * @param url the URL to replace
     * @return the replaced URL
     */
    public String replaceUrl(final String url) { // for display or url converer
        if (cachedPathMappingList == null) {
            synchronized (this) {
                if (cachedPathMappingList == null) {
                    init();
                }
            }
        }
        return replaceUrl(cachedPathMappingList, url);
    }

    /**
     * Creates a path matcher function for path mapping.
     *
     * @param matcher the regex matcher
     * @param replacement the replacement string
     * @return the path matcher function
     */
    public BiFunction<String, Matcher, String> createPathMatcher(final Matcher matcher, final String replacement) { // for PathMapping
        if (FUNCTION_ENCODEURL_MATCHER.equals(replacement)) {
            return (u, m) -> DocumentUtil.encodeUrl(u);
        }
        if (!replacement.startsWith(GROOVY_MATCHER)) {
            return (u, m) -> m.replaceAll(replacement);
        }
        final String template = replacement.substring(GROOVY_MATCHER.length());
        return (u, m) -> {
            final Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("url", u);
            paramMap.put("matcher", m);
            final Object value =
                    ComponentUtil.getScriptEngineFactory().getScriptEngine(Constants.DEFAULT_SCRIPT).evaluate(template, paramMap);
            if (value == null) {
                return u;
            }
            return value.toString();
        };
    }

    /**
     * Replaces URL using the given path mapping list.
     *
     * @param pathMappingList the path mapping list
     * @param url the URL to replace
     * @return the replaced URL
     */
    protected String replaceUrl(final List<PathMapping> pathMappingList, final String url) {
        String newUrl = url;
        for (final PathMapping pathMapping : pathMappingList) {
            if (matchUserAgent(pathMapping)) {
                newUrl = pathMapping.process(this, newUrl);
            }
        }
        if (logger.isDebugEnabled() && !StringUtil.equals(url, newUrl)) {
            logger.debug("replace: {} -> {}", url, newUrl);
        }
        return newUrl;
    }

    /**
     * Checks if the user agent matches the path mapping.
     *
     * @param pathMapping the path mapping
     * @return true if the user agent matches
     */
    protected boolean matchUserAgent(final PathMapping pathMapping) {
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
