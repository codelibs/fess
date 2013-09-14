/*
 * Copyright 2009-2013 the Fess Project and the Others.
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

package jp.sf.fess.db.exentity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import jp.sf.fess.Constants;
import jp.sf.fess.db.bsentity.BsFileCrawlingConfig;
import jp.sf.fess.helper.SystemHelper;

import org.seasar.framework.container.SingletonS2Container;
import org.seasar.framework.util.StringUtil;

/**
 * The entity of FILE_CRAWLING_CONFIG.
 * <p>
 * You can implement your original methods here.
 * This class remains when re-generating.
 * </p>
 * @author DBFlute(AutoGenerator)
 */
public class FileCrawlingConfig extends BsFileCrawlingConfig implements
        CrawlingConfig {

    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    private String[] browserTypeIds;

    private String[] labelTypeIds;

    private String[] roleTypeIds;

    protected volatile Pattern[] includedDocPathPatterns;

    protected volatile Pattern[] excludedDocPathPatterns;

    public FileCrawlingConfig() {
        super();
        setBoost(BigDecimal.ONE);
    }

    public String[] getBrowserTypeIds() {
        if (browserTypeIds == null) {
            return StringUtil.EMPTY_STRINGS;
        }
        return browserTypeIds;
    }

    public void setBrowserTypeIds(final String[] browserTypeIds) {
        this.browserTypeIds = browserTypeIds;
    }

    @Override
    public String[] getBrowserTypeValues() {
        final List<String> values = new ArrayList<String>();
        final List<FileConfigToBrowserTypeMapping> list = getFileConfigToBrowserTypeMappingList();
        if (list != null && !list.isEmpty()) {
            for (final FileConfigToBrowserTypeMapping mapping : list) {
                values.add(mapping.getBrowserType().getValue());
            }
        }
        return values.toArray(new String[values.size()]);
    }

    public String[] getLabelTypeIds() {
        if (labelTypeIds == null) {
            return StringUtil.EMPTY_STRINGS;
        }
        return labelTypeIds;
    }

    public void setLabelTypeIds(final String[] labelTypeIds) {
        this.labelTypeIds = labelTypeIds;
    }

    @Override
    public String[] getLabelTypeValues() {
        final List<String> values = new ArrayList<String>();
        final List<FileConfigToLabelTypeMapping> list = getFileConfigToLabelTypeMappingList();
        if (list != null && !list.isEmpty()) {
            for (final FileConfigToLabelTypeMapping mapping : list) {
                values.add(mapping.getLabelType().getValue());
            }
        }
        return values.toArray(new String[values.size()]);
    }

    public String[] getRoleTypeIds() {
        if (roleTypeIds == null) {
            return StringUtil.EMPTY_STRINGS;
        }
        return roleTypeIds;
    }

    public void setRoleTypeIds(final String[] roleTypeIds) {
        this.roleTypeIds = roleTypeIds;
    }

    @Override
    public String[] getRoleTypeValues() {
        final List<String> values = new ArrayList<String>();
        final List<FileConfigToRoleTypeMapping> list = getFileConfigToRoleTypeMappingList();
        if (list != null && !list.isEmpty()) {
            for (final FileConfigToRoleTypeMapping mapping : list) {
                values.add(mapping.getRoleType().getValue());
            }
        }
        return values.toArray(new String[values.size()]);
    }

    @Override
    public String getDocumentBoost() {
        return Float.valueOf(getBoost().floatValue()).toString();
    }

    @Override
    public String getIndexingTarget(final String input) {
        if (includedDocPathPatterns == null || excludedDocPathPatterns == null) {
            initDocPathPattern();
        }

        if (includedDocPathPatterns.length == 0
                && excludedDocPathPatterns.length == 0) {
            return Constants.TRUE;
        }

        for (final Pattern pattern : includedDocPathPatterns) {
            if (pattern.matcher(input).matches()) {
                return Constants.TRUE;
            }
        }

        for (final Pattern pattern : excludedDocPathPatterns) {
            if (pattern.matcher(input).matches()) {
                return Constants.FALSE;
            }
        }

        return Constants.TRUE;

    }

    protected synchronized void initDocPathPattern() {
        final SystemHelper systemHelper = SingletonS2Container
                .getComponent("systemHelper");

        if (includedDocPathPatterns == null) {
            if (StringUtil.isNotBlank(getIncludedDocPaths())) {
                final List<Pattern> pathPatterList = new ArrayList<Pattern>();
                final String[] paths = getIncludedDocPaths().split("[\r\n]");
                for (final String u : paths) {
                    if (StringUtil.isNotBlank(u) && !u.trim().startsWith("#")) {
                        pathPatterList.add(Pattern.compile(systemHelper
                                .encodeUrlFilter(u.trim())));
                    }
                }
                includedDocPathPatterns = pathPatterList
                        .toArray(new Pattern[pathPatterList.size()]);
            } else {
                includedDocPathPatterns = new Pattern[0];
            }
        }

        if (excludedDocPathPatterns == null) {
            if (StringUtil.isNotBlank(getExcludedDocPaths())) {
                final List<Pattern> pathPatterList = new ArrayList<Pattern>();
                final String[] paths = getExcludedDocPaths().split("[\r\n]");
                for (final String u : paths) {
                    if (StringUtil.isNotBlank(u) && !u.trim().startsWith("#")) {
                        pathPatterList.add(Pattern.compile(systemHelper
                                .encodeUrlFilter(u.trim())));
                    }
                }
                excludedDocPathPatterns = pathPatterList
                        .toArray(new Pattern[pathPatterList.size()]);
            } else if (includedDocPathPatterns.length > 0) {
                excludedDocPathPatterns = new Pattern[] { Pattern.compile(".*") };
            } else {
                excludedDocPathPatterns = new Pattern[0];
            }
        }
    }

    public String getBoostValue() {
        if (_boost != null) {
            return Integer.toString(_boost.intValue());
        }
        return null;
    }

    public void setBoostValue(final String value) {
        if (value != null) {
            try {
                _boost = new BigDecimal(value);
            } catch (final Exception e) {
            }
        }
    }
}
