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
package org.codelibs.fess.es.config.exentity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.es.config.bsentity.BsElevateWord;
import org.codelibs.fess.es.config.exbhv.ElevateWordToLabelBhv;
import org.codelibs.fess.es.config.exbhv.LabelTypeBhv;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.cbean.result.ListResultBean;

/**
 * @author ESFlute (using FreeGen)
 */
public class ElevateWord extends BsElevateWord {

    private static final long serialVersionUID = 1L;

    private String[] labelTypeIds;

    private volatile List<LabelType> labelTypeList;

    /* (non-Javadoc)
     * @see org.codelibs.fess.db.exentity.CrawlingConfig#getLabelTypeIds()
     */
    public String[] getLabelTypeIds() {
        if (labelTypeIds == null) {
            return StringUtil.EMPTY_STRINGS;
        }
        return labelTypeIds;
    }

    public void setLabelTypeIds(final String[] labelTypeIds) {
        this.labelTypeIds = labelTypeIds;
    }

    public List<LabelType> getLabelTypeList() {
        if (labelTypeList == null) {
            synchronized (this) {
                if (labelTypeList == null) {
                    final FessConfig fessConfig = ComponentUtil.getFessConfig();
                    final ElevateWordToLabelBhv elevateWordToLabelBhv = ComponentUtil.getComponent(ElevateWordToLabelBhv.class);
                    final ListResultBean<ElevateWordToLabel> mappingList = elevateWordToLabelBhv.selectList(cb -> {
                        cb.query().setElevateWordId_Equal(getId());
                        cb.specify().columnLabelTypeId();
                        cb.paging(fessConfig.getPageLabeltypeMaxFetchSizeAsInteger(), 1);
                    });
                    final List<String> labelIdList = new ArrayList<>();
                    for (final ElevateWordToLabel mapping : mappingList) {
                        labelIdList.add(mapping.getLabelTypeId());
                    }
                    final LabelTypeBhv labelTypeBhv = ComponentUtil.getComponent(LabelTypeBhv.class);
                    labelTypeList = labelIdList.isEmpty() ? Collections.emptyList() : labelTypeBhv.selectList(cb -> {
                        cb.query().setId_InScope(labelIdList);
                        cb.query().addOrderBy_SortOrder_Asc();
                        cb.fetchFirst(fessConfig.getPageLabeltypeMaxFetchSizeAsInteger());
                    });
                }
            }
        }
        return labelTypeList;
    }

    public String[] getLabelTypeValues() {
        final List<LabelType> list = getLabelTypeList();
        final List<String> labelValueList = new ArrayList<>(list.size());
        for (final LabelType labelType : list) {
            labelValueList.add(labelType.getValue());
        }
        return labelValueList.toArray(new String[labelValueList.size()]);
    }

    public String getId() {
        return asDocMeta().id();
    }

    public void setId(final String id) {
        asDocMeta().id(id);
    }

    public Long getVersionNo() {
        return asDocMeta().version();
    }

    public void setVersionNo(final Long version) {
        asDocMeta().version(version);
    }

    @Override
    public String toString() {
        return "ElevateWord [labelTypeIds=" + Arrays.toString(labelTypeIds) + ", labelTypeList=" + labelTypeList + ", boost=" + boost
                + ", createdBy=" + createdBy + ", createdTime=" + createdTime + ", reading=" + reading + ", suggestWord=" + suggestWord
                + ", updatedBy=" + updatedBy + ", updatedTime=" + updatedTime + ", docMeta=" + docMeta + "]";
    }

}
