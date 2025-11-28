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
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.app.service.DuplicateHostService;
import org.codelibs.fess.opensearch.config.exentity.DuplicateHost;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.di.core.exception.AutoBindingFailureException;

import jakarta.annotation.PostConstruct;

/**
 * Helper class for managing duplicate host configurations in the Fess search system.
 * This class handles URL conversion based on duplicate host rules, allowing multiple
 * hostnames or URLs to be treated as equivalent for crawling and indexing purposes.
 * It maintains a list of DuplicateHost rules and applies them to URLs.
 *
 */
public class DuplicateHostHelper {
    private static final Logger logger = LogManager.getLogger(DuplicateHostHelper.class);

    /** List of duplicate host rules for URL conversion */
    protected List<DuplicateHost> duplicateHostList;

    /**
     * Default constructor for DuplicateHostHelper.
     * Creates a new duplicate host helper instance.
     */
    public DuplicateHostHelper() {
        // Default constructor
    }

    /**
     * Initializes the duplicate host helper after construction.
     * Loads duplicate host configurations from the DuplicateHostService.
     */
    @PostConstruct
    public void init() {
        if (logger.isDebugEnabled()) {
            logger.debug("Initializing {}", this.getClass().getSimpleName());
        }
        if (duplicateHostList == null) {
            duplicateHostList = new ArrayList<>();
        }
        try {
            final DuplicateHostService duplicateHostService = ComponentUtil.getComponent(DuplicateHostService.class);
            duplicateHostList.addAll(duplicateHostService.getDuplicateHostList());
        } catch (final AutoBindingFailureException e) {
            logger.warn("DuplicateHostService is not found.", e);
        }
    }

    /**
     * Sets the list of duplicate host rules.
     *
     * @param duplicateHostList the list of duplicate host rules to use
     */
    public void setDuplicateHostList(final List<DuplicateHost> duplicateHostList) {
        this.duplicateHostList = duplicateHostList;
    }

    /**
     * Adds a new duplicate host rule to the list.
     * Initializes the list if it doesn't exist.
     *
     * @param duplicateHost the duplicate host rule to add
     */
    public void add(final DuplicateHost duplicateHost) {
        if (duplicateHostList == null) {
            duplicateHostList = new ArrayList<>();
        }
        duplicateHostList.add(duplicateHost);
    }

    /**
     * Converts a URL using all configured duplicate host rules.
     * Applies each duplicate host rule in sequence to transform the URL
     * according to the configured patterns.
     *
     * @param url the URL to convert
     * @return the converted URL after applying all duplicate host rules,
     *         or null if the input URL is null
     */
    public String convert(final String url) {
        if (url == null) {
            return null;
        }

        if (duplicateHostList == null) {
            init();
        }

        String newUrl = url;
        for (final DuplicateHost duplicateHost : duplicateHostList) {
            newUrl = duplicateHost.convert(newUrl);
        }
        return newUrl;
    }
}
