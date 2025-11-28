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
package org.codelibs.fess.entity;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.util.ComponentUtil;

import jakarta.annotation.PostConstruct;

/**
 * View class for managing facet query configurations and their display.
 * This class handles the setup and organization of query facets for the search interface,
 * including automatic generation of file type facets and custom query mappings.
 */
public class FacetQueryView {
    /** Logger instance for this class */
    private static final Logger logger = LogManager.getLogger(FacetQueryView.class);

    /** Title for this facet query view */
    protected String title;

    /** Map of display keys to their corresponding query strings */
    protected Map<String, String> queryMap = new LinkedHashMap<>();

    /**
     * Default constructor for FacetQueryView.
     */
    public FacetQueryView() {
        // Default constructor
    }

    /**
     * Initializes the facet query view with default file type queries.
     * This method is called after dependency injection to set up file type facets
     * and register all queries with the FacetInfo component.
     */
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
                logger.debug("Updated query map: queryMap={}", queryMap);
            }
        }

        final FacetInfo facetInfo = ComponentUtil.getComponent("facetInfo");
        queryMap.values().stream().distinct().forEach(facetInfo::addQuery);
    }

    /**
     * Gets the title for this facet query view.
     *
     * @return the title string
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title for this facet query view.
     *
     * @param title the title to set
     */
    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * Gets the map of display keys to query strings.
     *
     * @return the query map
     */
    public Map<String, String> getQueryMap() {
        return queryMap;
    }

    /**
     * Adds a query to the query map with the specified display key.
     *
     * @param key the display key for the query
     * @param query the query string to add
     */
    public void addQuery(final String key, final String query) {
        queryMap.put(key, query);
    }

    /**
     * Returns a string representation of this FacetQueryView object.
     * Includes the title and query map for debugging purposes.
     *
     * @return string representation of this FacetQueryView instance
     */
    @Override
    public String toString() {
        return "FacetQueryView [title=" + title + ", queryMap=" + queryMap + "]";
    }

}
