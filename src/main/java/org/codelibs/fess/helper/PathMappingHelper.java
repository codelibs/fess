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
package org.codelibs.fess.helper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import javax.annotation.PostConstruct;

import org.codelibs.fess.Constants;
import org.codelibs.fess.es.config.exbhv.PathMappingBhv;
import org.codelibs.fess.es.config.exentity.PathMapping;
import org.lastaflute.di.core.SingletonLaContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PathMappingHelper implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(PathMappingHelper.class);

    private final Map<String, List<PathMapping>> pathMappingMap = new HashMap<String, List<PathMapping>>();

    volatile List<PathMapping> cachedPathMappingList = null;

    @PostConstruct
    public void init() {
        final List<String> ptList = new ArrayList<>();
        ptList.add(Constants.PROCESS_TYPE_DISPLAYING);
        ptList.add(Constants.PROCESS_TYPE_BOTH);

        try {
            final PathMappingBhv pathMappingBhv = SingletonLaContainer.getComponent(PathMappingBhv.class);
            cachedPathMappingList = pathMappingBhv.selectList(cb -> {
                cb.query().addOrderBy_SortOrder_Asc();
                cb.query().setProcessType_InScope(ptList);
            });
        } catch (final Exception e) {
            logger.warn("Failed to load path mappings.", e);
        }
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
            result = result.replaceAll("(\"[^\"]*)" + pathMapping.getRegex() + "([^\"]*\")", "$1" + pathMapping.getReplacement() + "$2");
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

    private String replaceUrl(final List<PathMapping> pathMappingList, final String url) {
        String newUrl = url;
        for (final PathMapping pathMapping : pathMappingList) {
            final Matcher matcher = pathMapping.getMatcher(newUrl);
            if (matcher.find()) {
                newUrl = matcher.replaceAll(pathMapping.getReplacement());
            }
        }
        return newUrl;
    }
}
