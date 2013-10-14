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
import jp.sf.fess.db.bsentity.BsDataCrawlingConfig;

import org.seasar.framework.util.StringUtil;

/**
 * The entity of DATA_CRAWLING_CONFIG.
 * <p>
 * You can implement your original methods here.
 * This class remains when re-generating.
 * </p>
 * @author DBFlute(AutoGenerator)
 */
public class DataCrawlingConfig extends BsDataCrawlingConfig implements
        CrawlingConfig {

    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    private String[] browserTypeIds;

    private String[] labelTypeIds;

    private String[] roleTypeIds;

    protected Pattern[] includedDocPathPatterns;

    protected Pattern[] excludedDocPathPatterns;

    public DataCrawlingConfig() {
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
        final List<DataConfigToBrowserTypeMapping> list = getDataConfigToBrowserTypeMappingList();
        if (list != null && !list.isEmpty()) {
            for (final DataConfigToBrowserTypeMapping mapping : list) {
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
        final List<DataConfigToLabelTypeMapping> list = getDataConfigToLabelTypeMappingList();
        if (list != null && !list.isEmpty()) {
            for (final DataConfigToLabelTypeMapping mapping : list) {
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
        final List<DataConfigToRoleTypeMapping> list = getDataConfigToRoleTypeMappingList();
        if (list != null && !list.isEmpty()) {
            for (final DataConfigToRoleTypeMapping mapping : list) {
                values.add(mapping.getRoleType().getValue());
            }
        }
        return values.toArray(new String[values.size()]);
    }

    @Override
    public String getDocumentBoost() {
        return Float.valueOf(getBoost().floatValue()).toString();
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

    @Override
    public String getIndexingTarget(final String input) {
        // always return true
        return Constants.TRUE;
    }

    @Override
    public String getConfigId() {
        if (getId() != null) {
            return Constants.DATE_CONFIG_ID_PREFIX + getId().toString();
        }
        return null;
    }
}
