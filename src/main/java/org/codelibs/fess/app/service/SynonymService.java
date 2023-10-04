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
import org.codelibs.fess.app.pager.SynonymPager;
import org.codelibs.fess.dict.DictionaryFile.PagingList;
import org.codelibs.fess.dict.DictionaryManager;
import org.codelibs.fess.dict.synonym.SynonymFile;
import org.codelibs.fess.dict.synonym.SynonymItem;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.dbflute.optional.OptionalEntity;

public class SynonymService {
    @Resource
    protected DictionaryManager dictionaryManager;

    @Resource
    protected FessConfig fessConfig;

    public List<SynonymItem> getSynonymList(final String dictId, final SynonymPager synonymPager) {
        return getSynonymFile(dictId).map(file -> {
            final int pageSize = synonymPager.getPageSize();
            final PagingList<SynonymItem> synonymList = file.selectList((synonymPager.getCurrentPageNumber() - 1) * pageSize, pageSize);

            // update pager
            BeanUtil.copyBeanToBean(synonymList, synonymPager, option -> option.include(Constants.PAGER_CONVERSION_RULE));
            synonymList.setPageRangeSize(fessConfig.getPagingPageRangeSizeAsInteger());
            synonymPager.setPageNumberList(synonymList.createPageNumberList());

            return (List<SynonymItem>) synonymList;
        }).orElse(Collections.emptyList());
    }

    public OptionalEntity<SynonymFile> getSynonymFile(final String dictId) {
        return dictionaryManager.getDictionaryFile(dictId).filter(SynonymFile.class::isInstance)
                .map(file -> OptionalEntity.of((SynonymFile) file)).orElse(OptionalEntity.empty());
    }

    public OptionalEntity<SynonymItem> getSynonymItem(final String dictId, final long id) {
        return getSynonymFile(dictId).map(file -> file.get(id).get());
    }

    public void store(final String dictId, final SynonymItem synonymItem) {
        getSynonymFile(dictId).ifPresent(file -> {
            if (synonymItem.getId() == 0) {
                file.insert(synonymItem);
            } else {
                file.update(synonymItem);
            }
        });
    }

    public void delete(final String dictId, final SynonymItem synonymItem) {
        getSynonymFile(dictId).ifPresent(file -> {
            file.delete(synonymItem);
        });
    }
}
