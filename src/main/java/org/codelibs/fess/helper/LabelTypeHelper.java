/*
 * Copyright 2009-2015 the CodeLibs Project and the Others.
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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.es.exentity.LabelType;
import org.codelibs.fess.service.LabelTypeService;
import org.lastaflute.di.core.SingletonLaContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LabelTypeHelper implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(LabelTypeHelper.class);

    private static final long serialVersionUID = 1L;

    @Resource
    protected RoleQueryHelper roleQueryHelper;

    protected volatile List<LabelTypeItem> labelTypeItemList = new ArrayList<LabelTypeItem>();

    protected volatile List<LabelTypePattern> labelTypePatternList;

    protected LabelTypeService getLabelTypeService() {
        return SingletonLaContainer.getComponent(LabelTypeService.class);
    }

    @PostConstruct
    public void init() {
        final List<LabelType> labelTypeList = getLabelTypeService().getLabelTypeListWithRoles();
        buildLabelTypeItems(labelTypeList);
    }

    public void refresh(final List<LabelType> labelTypeList) {
        buildLabelTypeItems(labelTypeList);
    }

    private void buildLabelTypeItems(final List<LabelType> labelTypeList) {
        final List<LabelTypeItem> itemList = new ArrayList<LabelTypeItem>();
        for (final LabelType labelType : labelTypeList) {
            final LabelTypeItem item = new LabelTypeItem();
            item.setLabel(labelType.getName());
            item.setValue(labelType.getValue());
            item.setRoleValueList(labelType.getRoleValueList());
            itemList.add(item);
        }
        labelTypeItemList = itemList;
    }

    public List<Map<String, String>> getLabelTypeItemList() {
        if (labelTypeItemList == null) {
            init();
        }

        final List<Map<String, String>> itemList = new ArrayList<Map<String, String>>();
        final Set<String> roleSet = roleQueryHelper.build();
        if (roleSet.isEmpty()) {
            for (final LabelTypeItem item : labelTypeItemList) {
                final Map<String, String> map = new HashMap<String, String>(2);
                map.put(Constants.ITEM_LABEL, item.getLabel());
                map.put(Constants.ITEM_VALUE, item.getValue());
                itemList.add(map);
            }
        } else {
            for (final LabelTypeItem item : labelTypeItemList) {
                for (final String roleValue : roleSet) {
                    if (item.getRoleValueList().contains(roleValue)) {
                        final Map<String, String> map = new HashMap<String, String>(2);
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
                    final List<LabelType> labelTypeList = getLabelTypeService().getLabelTypeList();
                    final List<LabelTypePattern> list = new ArrayList<LabelTypePattern>();
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

        final Set<String> valueSet = new HashSet<String>();
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

        private List<String> roleValueList;

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

        public List<String> getRoleValueList() {
            return roleValueList;
        }

        public void setRoleValueList(final List<String> roleValueList) {
            this.roleValueList = roleValueList;
        }
    }

    protected static class LabelTypePattern {

        private final String value;

        private Pattern includedPaths;

        private Pattern excludedPaths;

        public LabelTypePattern(final String value, final String includedPaths, final String excludedPaths) {
            this.value = value;

            if (StringUtil.isNotBlank(includedPaths)) {
                final StringBuilder buf = new StringBuilder();
                char split = 0;
                for (final String path : includedPaths.split("\n")) {
                    if (split == 0) {
                        split = '|';
                    } else {
                        buf.append(split);
                    }
                    buf.append(path.trim());
                }
                this.includedPaths = Pattern.compile(buf.toString());
            }

            if (StringUtil.isNotBlank(excludedPaths)) {
                final StringBuilder buf = new StringBuilder();
                char split = 0;
                for (final String path : excludedPaths.split("\n")) {
                    if (split == 0) {
                        split = '|';
                    } else {
                        buf.append(split);
                    }
                    buf.append(path.trim());
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
                        return false;
                    }
                    return true;
                }
                return false;
            } else {
                return !excludedPaths.matcher(path).matches();
            }
        }

    }
}
