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
import org.codelibs.fess.app.pager.StopwordsPager;
import org.codelibs.fess.dict.DictionaryFile.PagingList;
import org.codelibs.fess.dict.DictionaryManager;
import org.codelibs.fess.dict.stopwords.StopwordsFile;
import org.codelibs.fess.dict.stopwords.StopwordsItem;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.dbflute.optional.OptionalEntity;

public class StopwordsService {
    @Resource
    protected DictionaryManager dictionaryManager;

    @Resource
    protected FessConfig fessConfig;

    public List<StopwordsItem> getStopwordsList(final String dictId, final StopwordsPager stopwordsPager) {
        return getStopwordsFile(dictId).map(file -> {
            final int pageSize = stopwordsPager.getPageSize();
            final PagingList<StopwordsItem> stopwordsList =
                    file.selectList((stopwordsPager.getCurrentPageNumber() - 1) * pageSize, pageSize);

            // update pager
            BeanUtil.copyBeanToBean(stopwordsList, stopwordsPager, option -> option.include(Constants.PAGER_CONVERSION_RULE));
            stopwordsList.setPageRangeSize(fessConfig.getPagingPageRangeSizeAsInteger());
            stopwordsPager.setPageNumberList(stopwordsList.createPageNumberList());

            return (List<StopwordsItem>) stopwordsList;
        }).orElse(Collections.emptyList());
    }

    public OptionalEntity<StopwordsFile> getStopwordsFile(final String dictId) {
        return dictionaryManager.getDictionaryFile(dictId).filter(StopwordsFile.class::isInstance)
                .map(file -> OptionalEntity.of((StopwordsFile) file)).orElse(OptionalEntity.empty());
    }

    public OptionalEntity<StopwordsItem> getStopwordsItem(final String dictId, final long id) {
        return getStopwordsFile(dictId).map(file -> file.get(id).get());
    }

    public void store(final String dictId, final StopwordsItem stopwordsItem) {
        getStopwordsFile(dictId).ifPresent(file -> {
            if (stopwordsItem.getId() == 0) {
                file.insert(stopwordsItem);
            } else {
                file.update(stopwordsItem);
            }
        });
    }

    public void delete(final String dictId, final StopwordsItem stopwordsItem) {
        getStopwordsFile(dictId).ifPresent(file -> {
            file.delete(stopwordsItem);
        });
    }
}
