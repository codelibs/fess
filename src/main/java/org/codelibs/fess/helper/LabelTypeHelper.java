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

import static org.codelibs.core.stream.StreamUtil.stream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.service.LabelTypeService;
import org.codelibs.fess.entity.SearchRequestParams.SearchRequestType;
import org.codelibs.fess.es.config.exentity.LabelType;
import org.codelibs.fess.util.ComponentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LabelTypeHelper {
    private static final Logger logger = LoggerFactory.getLogger(LabelTypeHelper.class);

    protected volatile List<LabelTypeItem> labelTypeItemList = new ArrayList<>();

    protected volatile List<LabelTypePattern> labelTypePatternList;

    @PostConstruct
    public void init() {
        update();
    }

    public int update() {
        final List<LabelType> labelTypeList = ComponentUtil.getComponent(LabelTypeService.class).getLabelTypeList();
        buildLabelTypeItems(labelTypeList);
        return labelTypeList.size();
    }

    public void refresh(final List<LabelType> labelTypeList) {
        buildLabelTypeItems(labelTypeList);
    }

    private void buildLabelTypeItems(final List<LabelType> labelTypeList) {
        final List<LabelTypeItem> itemList = new ArrayList<>();
        for (final LabelType labelType : labelTypeList) {
            final LabelTypeItem item = new LabelTypeItem();
            item.setLabel(labelType.getName());
            item.setValue(labelType.getValue());
            item.setPermissions(labelType.getPermissions());
            item.setVirtualHost(labelType.getVirtualHost());
            itemList.add(item);
        }
        labelTypeItemList = itemList;
    }

    public List<Map<String, String>> getLabelTypeItemList(final SearchRequestType searchRequestType) {
        if (labelTypeItemList == null) {
            init();
        }

        final String virtualHostKey = ComponentUtil.getVirtualHostHelper().getVirtualHostKey();
        final List<LabelTypeItem> labelList;
        if (StringUtil.isBlank(virtualHostKey)) {
            labelList = labelTypeItemList;
        } else {
            labelList =
                    labelTypeItemList.stream().filter(item -> virtualHostKey.equals(item.getVirtualHost())).collect(Collectors.toList());
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

    public Set<String> getMatchedLabelValueSet(final String path) {
        if (labelTypePatternList == null) {
            synchronized (this) {
                if (labelTypePatternList == null) {
                    final List<LabelType> labelTypeList = ComponentUtil.getComponent(LabelTypeService.class).getLabelTypeList();
                    final List<LabelTypePattern> list = new ArrayList<>();
                    for (final LabelType labelType : labelTypeList) {
                        final String includedPaths = labelType.getIncludedPaths();
                        final String excludedPaths = labelType.getExcludedPaths();
                        if (StringUtil.isNotBlank(includedPaths) || StringUtil.isNotBlank(excludedPaths)) {
                            try {
                                list.add(new LabelTypePattern(labelType.getValue(), includedPaths, excludedPaths));
                            } catch (final Exception e) {
                                logger.warn("Failed to create a matching pattern of a label: " + labelType.getValue() + ", includedPaths:"
                                        + includedPaths + ", excludedPaths:" + excludedPaths, e);
                            }
                        }
                    }
                    labelTypePatternList = list;
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

    protected static class LabelTypeItem {
        private String label;

        private String value;

        private String[] permissions;

        private String virtualHost;

        public String getLabel() {
            return label;
        }

        public void setLabel(final String label) {
            this.label = label;
        }

        public String getValue() {
            return value;
        }

        public void setValue(final String value) {
            this.value = value;
        }

        public String[] getPermissions() {
            return permissions;
        }

        public void setPermissions(final String[] permissions) {
            this.permissions = permissions;
        }

        public String getVirtualHost() {
            return virtualHost;
        }

        public void setVirtualHost(final String virtualHost) {
            this.virtualHost = virtualHost;
        }
    }

    public static class LabelTypePattern {

        private final String value;

        private Pattern includedPaths;

        private Pattern excludedPaths;

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

        public String getValue() {
            return value;
        }

        public boolean match(final String path) {
            if (includedPaths != null) {
                if (includedPaths.matcher(path).matches()) {
                    if (excludedPaths != null && excludedPaths.matcher(path).matches()) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Path " + path + " matched against the excludes paths expression " + excludedPaths.toString());
                        }
                        return false;
                    }
                    return true;
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("Path " + path + " wasn't matched against the include paths expression " + includedPaths.toString());
                }
                return false;
            } else {
                if (excludedPaths != null && excludedPaths.matcher(path).matches()) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Path " + path + " matched against the excludes paths expression " + excludedPaths.toString());
                    }
                    return false;
                }
                return true;
            }
        }

    }
}
