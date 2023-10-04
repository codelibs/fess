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
package org.codelibs.fess.app.service;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.ProtwordsPager;
import org.codelibs.fess.dict.DictionaryFile.PagingList;
import org.codelibs.fess.dict.DictionaryManager;
import org.codelibs.fess.dict.protwords.ProtwordsFile;
import org.codelibs.fess.dict.protwords.ProtwordsItem;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.dbflute.optional.OptionalEntity;

public class ProtwordsService {
    @Resource
    protected DictionaryManager dictionaryManager;

    @Resource
    protected FessConfig fessConfig;

    public List<ProtwordsItem> getProtwordsList(final String dictId, final ProtwordsPager protwordsPager) {
        return getProtwordsFile(dictId).map(file -> {
            final int pageSize = protwordsPager.getPageSize();
            final PagingList<ProtwordsItem> protwordsList =
                    file.selectList((protwordsPager.getCurrentPageNumber() - 1) * pageSize, pageSize);

            // update pager
            BeanUtil.copyBeanToBean(protwordsList, protwordsPager, option -> option.include(Constants.PAGER_CONVERSION_RULE));
            protwordsList.setPageRangeSize(fessConfig.getPagingPageRangeSizeAsInteger());
            protwordsPager.setPageNumberList(protwordsList.createPageNumberList());

            return (List<ProtwordsItem>) protwordsList;
        }).orElse(Collections.emptyList());
    }

    public OptionalEntity<ProtwordsFile> getProtwordsFile(final String dictId) {
        return dictionaryManager.getDictionaryFile(dictId).filter(ProtwordsFile.class::isInstance)
                .map(file -> OptionalEntity.of((ProtwordsFile) file)).orElse(OptionalEntity.empty());
    }

    public OptionalEntity<ProtwordsItem> getProtwordsItem(final String dictId, final long id) {
        return getProtwordsFile(dictId).map(file -> file.get(id).get());
    }

    public void store(final String dictId, final ProtwordsItem protwordsItem) {
        getProtwordsFile(dictId).ifPresent(file -> {
            if (protwordsItem.getId() == 0) {
                file.insert(protwordsItem);
            } else {
                file.update(protwordsItem);
            }
        });
    }

    public void delete(final String dictId, final ProtwordsItem protwordsItem) {
        getProtwordsFile(dictId).ifPresent(file -> {
            file.delete(protwordsItem);
        });
    }
}
