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

import static org.codelibs.core.stream.StreamUtil.stream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.service.LabelTypeService;
import org.codelibs.fess.entity.SearchRequestParams.SearchRequestType;
import org.codelibs.fess.opensearch.config.exentity.LabelType;
import org.codelibs.fess.util.ComponentUtil;

import jakarta.annotation.PostConstruct;

/**
 * Helper class for label types.
 */
public class LabelTypeHelper extends AbstractConfigHelper {
    private static final Logger logger = LogManager.getLogger(LabelTypeHelper.class);

    /** A list of label type items. */
    protected volatile List<LabelTypeItem> labelTypeItemList;

    /** A list of label type patterns. */
    protected volatile List<LabelTypePattern> labelTypePatternList;

    /**
     * Default constructor.
     */
    public LabelTypeHelper() {
        super();
    }

    /**
     * Initializes the helper.
     */
    @PostConstruct
    public void init() {
        if (logger.isDebugEnabled()) {
            logger.debug("Initialize {}", this.getClass().getSimpleName());
        }
        load();
    }

    @Override
    public int load() {
        final List<LabelType> labelTypeList = ComponentUtil.getComponent(LabelTypeService.class).getLabelTypeList();
        buildLabelTypeItems(labelTypeList);
        buildLabelTypePatternList(labelTypeList);
        return labelTypeList.size();
    }

    /**
     * Refreshes the label type items and patterns.
     *
     * @param labelTypeList The list of label types.
     */
    public void refresh(final List<LabelType> labelTypeList) {
        buildLabelTypeItems(labelTypeList);
        buildLabelTypePatternList(labelTypeList);
    }

    /**
     * Builds a list of label type items.
     *
     * @param labelTypeList The list of label types.
     */
    protected void buildLabelTypeItems(final List<LabelType> labelTypeList) {
        final List<LabelTypeItem> itemList = new ArrayList<>();
        for (final LabelType labelType : labelTypeList) {
            final LabelTypeItem item = new LabelTypeItem();
            item.setLabel(labelType.getName());
            item.setValue(labelType.getValue());
            item.setPermissions(labelType.getPermissions());
            item.setVirtualHost(labelType.getVirtualHost());
            item.setLocale(labelType.getLocale());
            itemList.add(item);
        }
        labelTypeItemList = itemList;
    }

    /**
     * Returns a list of label type items.
     *
     * @param searchRequestType The search request type.
     * @return A list of label type items.
     */
    public List<Map<String, String>> getLabelTypeItemList(final SearchRequestType searchRequestType) {
        return getLabelTypeItemList(searchRequestType, Locale.ROOT);
    }

    /**
     * Returns a list of label type items.
     *
     * @param searchRequestType The search request type.
     * @param requestLocale The request locale.
     * @return A list of label type items.
     */
    public List<Map<String, String>> getLabelTypeItemList(final SearchRequestType searchRequestType, final Locale requestLocale) {
        if (labelTypeItemList == null) {
            init();
        }

        final String virtualHostKey = ComponentUtil.getVirtualHostHelper().getVirtualHostKey();
        final List<LabelTypeItem> labelList;
        if (StringUtil.isBlank(virtualHostKey)) {
            labelList =
                    labelTypeItemList.stream().filter(item -> matchLocale(requestLocale, item.getLocale())).collect(Collectors.toList());
        } else {
            labelList = labelTypeItemList.stream()
                    .filter(item -> matchLocale(requestLocale, item.getLocale()) && virtualHostKey.equals(item.getVirtualHost()))
                    .collect(Collectors.toList());
        }

        final List<Map<String, String>> itemList = new ArrayList<>();
        final Set<String> roleSet = ComponentUtil.getRoleQueryHelper().build(searchRequestType);
        if (roleSet.isEmpty()) {
            for (final LabelTypeItem item : labelList) {
                if (item.getPermissions().length == 0) {
                    final Map<String, String> map = new HashMap<>(2);
                    map.put(Constants.ITEM_LABEL, item.getLabel());
                    map.put(Constants.ITEM_VALUE, item.getValue());
                    itemList.add(map);
                }
            }
        } else {
            for (final LabelTypeItem item : labelList) {
                final Set<String> permissions = stream(item.getPermissions()).get(stream -> stream.collect(Collectors.toSet()));
                for (final String roleValue : roleSet) {
                    if (permissions.contains(roleValue)) {
                        final Map<String, String> map = new HashMap<>(2);
                        map.put(Constants.ITEM_LABEL, item.getLabel());
                        map.put(Constants.ITEM_VALUE, item.getValue());
                        itemList.add(map);
                        break;
                    }
                }
            }
        }

        return itemList;
    }

    /**
     * Matches the request locale with the target locale.
     *
     * @param requestLocale The request locale.
     * @param targetLocale The target locale.
     * @return True if the locales match, otherwise false.
     */
    protected boolean matchLocale(final Locale requestLocale, final Locale targetLocale) {
        if (targetLocale.equals(requestLocale) || targetLocale.equals(Locale.ROOT)) {
            return true;
        }
        if (requestLocale == null || !requestLocale.getLanguage().equals(targetLocale.getLanguage())
                || targetLocale.getCountry().length() > 0 && !requestLocale.getCountry().equals(targetLocale.getCountry())) {
            return false;
        }
        return true;
    }

    /**
     * Returns a set of matched label values.
     *
     * @param path The path to match.
     * @return A set of matched label values.
     */
    public Set<String> getMatchedLabelValueSet(final String path) {
        if (labelTypePatternList == null) {
            synchronized (this) {
                if (labelTypePatternList == null) {
                    buildLabelTypePatternList(ComponentUtil.getComponent(LabelTypeService.class).getLabelTypeList());
                }
            }
        }

        if (labelTypePatternList.isEmpty()) {
            return Collections.emptySet();
        }

        final Set<String> valueSet = new HashSet<>();
        for (final LabelTypePattern pattern : labelTypePatternList) {
            if (pattern.match(path)) {
                valueSet.add(pattern.getValue());
            }
        }
        return valueSet;
    }

    /**
     * Builds a list of label type patterns.
     *
     * @param labelTypeList The list of label types.
     */
    protected void buildLabelTypePatternList(final List<LabelType> labelTypeList) {
        final List<LabelTypePattern> list = new ArrayList<>();
        for (final LabelType labelType : labelTypeList) {
            final String includedPaths = labelType.getIncludedPaths();
            final String excludedPaths = labelType.getExcludedPaths();
            if (StringUtil.isNotBlank(includedPaths) || StringUtil.isNotBlank(excludedPaths)) {
                try {
                    list.add(new LabelTypePattern(labelType.getValue(), includedPaths, excludedPaths));
                } catch (final Exception e) {
                    logger.warn("Failed to create label pattern: label={}, includedPaths={}, excludedPaths={}", labelType.getValue(),
                            includedPaths, excludedPaths, e);
                }
            }
        }
        labelTypePatternList = list;
    }

    /**
     * An item of a label type.
     */
    protected static class LabelTypeItem {
        /**
         * Default constructor.
         */
        public LabelTypeItem() {
            // do nothing
        }

        private String label;

        private String value;

        private String[] permissions;

        private String virtualHost;

        private Locale locale;

        /**
         * Returns the label.
         *
         * @return The label.
         */
        public String getLabel() {
            return label;
        }

        /**
         * Sets the label.
         *
         * @param label The label.
         */
        public void setLabel(final String label) {
            this.label = label;
        }

        /**
         * Returns the value.
         *
         * @return The value.
         */
        public String getValue() {
            return value;
        }

        /**
         * Sets the value.
         *
         * @param value The value.
         */
        public void setValue(final String value) {
            this.value = value;
        }

        /**
         * Returns the permissions.
         *
         * @return The permissions.
         */
        public String[] getPermissions() {
            return permissions;
        }

        /**
         * Sets the permissions.
         *
         * @param permissions The permissions.
         */
        public void setPermissions(final String[] permissions) {
            this.permissions = permissions;
        }

        /**
         * Returns the virtual host.
         *
         * @return The virtual host.
         */
        public String getVirtualHost() {
            return virtualHost;
        }

        /**
         * Sets the virtual host.
         *
         * @param virtualHost The virtual host.
         */
        public void setVirtualHost(final String virtualHost) {
            this.virtualHost = virtualHost;
        }

        /**
         * Returns the locale.
         *
         * @return The locale.
         */
        public Locale getLocale() {
            return locale;
        }

        /**
         * Sets the locale.
         *
         * @param locale The locale.
         */
        public void setLocale(final Locale locale) {
            this.locale = locale;
        }
    }

    /**
     * A pattern of a label type.
     */
    public static class LabelTypePattern {

        private final String value;

        private Pattern includedPaths;

        private Pattern excludedPaths;

        /**
         * Constructs a new label type pattern.
         *
         * @param value The value.
         * @param includedPaths The included paths.
         * @param excludedPaths The excluded paths.
         */
        public LabelTypePattern(final String value, final String includedPaths, final String excludedPaths) {
            this.value = value;

            final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
            if (StringUtil.isNotBlank(includedPaths)) {
                final StringBuilder buf = new StringBuilder(100);
                char split = 0;
                for (final String path : includedPaths.split("\n")) {
                    if (split == 0) {
                        split = '|';
                    } else {
                        buf.append(split);
                    }
                    final String normalizePath = systemHelper.normalizeConfigPath(path);
                    if (StringUtil.isNotBlank(normalizePath)) {
                        buf.append(normalizePath);
                    }
                }
                this.includedPaths = Pattern.compile(buf.toString());
            }

            if (StringUtil.isNotBlank(excludedPaths)) {
                final StringBuilder buf = new StringBuilder(100);
                char split = 0;
                for (final String path : excludedPaths.split("\n")) {
                    if (split == 0) {
                        split = '|';
                    } else {
                        buf.append(split);
                    }
                    final String normalizePath = systemHelper.normalizeConfigPath(path);
                    if (StringUtil.isNotBlank(normalizePath)) {
                        buf.append(normalizePath);
                    }
                }
                this.excludedPaths = Pattern.compile(buf.toString());
            }
        }

        /**
         * Returns the value.
         *
         * @return The value.
         */
        public String getValue() {
            return value;
        }

        /**
         * Matches the given path.
         *
         * @param path The path to match.
         * @return True if the path matches, otherwise false.
         */
        public boolean match(final String path) {
            if (includedPaths == null) {
                if (excludedPaths != null && excludedPaths.matcher(path).matches()) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Path {} matches the exclude path expression {} on {} of label.", path, excludedPaths, value);
                    }
                    return false;
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("Path {} does not match the exclude path expression {} on {} of label.", path, excludedPaths, value);
                }
                return true;
            }
            if (includedPaths.matcher(path).matches()) {
                if (excludedPaths != null && excludedPaths.matcher(path).matches()) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Path {} matches the include/exclude path expression {} on {} of label.", path, excludedPaths, value);
                    }
                    return false;
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("Path {} matches the include path expression {} on {} of label.", path, excludedPaths, value);
                }
                return true;
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Path {} does not match the include path expression {} on {} of label.", path, excludedPaths, value);
            }
            return false;
        }

    }
}
