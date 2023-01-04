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
package org.codelibs.fess.entity;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.util.ComponentUtil;

public class FacetQueryView {
    private static final Logger logger = LogManager.getLogger(FacetQueryView.class);

    protected String title;

    protected Map<String, String> queryMap = new LinkedHashMap<>();

    @PostConstruct
    public void init() {
        final String filetypeField = ComponentUtil.getFessConfig().getIndexFieldFiletype();
        final Collection<String> values = queryMap.values();
        if (values.stream().anyMatch(s -> s.startsWith(filetypeField))) {
            final ResourceBundle resources = ResourceBundle.getBundle("fess_label", Locale.ENGLISH);
            final String[] fileTypes = ComponentUtil.getFileTypeHelper().getTypes();
            for (final String fileType : fileTypes) {
                final String value = filetypeField + ":" + fileType;
                if (!values.contains(value)) {
                    final String key = "labels.facet_filetype_" + fileType;
                    if (resources.containsKey(key)) {
                        queryMap.put(key, value);
                    } else {
                        queryMap.put(fileType.toUpperCase(Locale.ROOT), value);
                    }
                }
            }
            queryMap.remove("labels.facet_filetype_others");
            queryMap.put("labels.facet_filetype_others", "filetype:others");
            if (logger.isDebugEnabled()) {
                logger.debug("updated query map: {}", queryMap);
            }
        }

        final FacetInfo facetInfo = ComponentUtil.getComponent("facetInfo");
        queryMap.values().stream().distinct().forEach(facetInfo::addQuery);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public Map<String, String> getQueryMap() {
        return queryMap;
    }

    public void addQuery(final String key, final String query) {
        queryMap.put(key, query);
    }

    @Override
    public String toString() {
        return "FacetQueryView [title=" + title + ", queryMap=" + queryMap + "]";
    }

}
